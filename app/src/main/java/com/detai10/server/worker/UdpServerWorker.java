package com.detai10.server.worker;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Worker Thread chuyên trách lắng nghe socket UDP và điều phối xử lý gói tin (SRV-02).
 * <p>
 * Yêu cầu kỹ thuật & Kiến trúc:
 * 1. Chạy trên Worker Thread độc lập để không làm nghẽn Event Dispatch Thread (EDT) của giao diện Swing.
 * 2. Mở {@link DatagramSocket} trên cổng chỉ định với mảng buffer kích thước tối đa 65,507 bytes (Max UDP Payload).
 * 3. Tự động nhận diện gói tin Healthcheck {@code PING} (case-insensitive) và gửi phản hồi {@code PONG} tức thì (&lt; 50ms).
 * 4. Chuyển giao các gói tin dữ liệu khác cho {@link PacketHandler} để xử lý đa luồng qua {@link ExecutorService}.
 * 5. Mọi thao tác cập nhật giao diện (Log, thống kê) đều được bọc an toàn trong {@code SwingUtilities.invokeLater()}.
 * 6. Cơ chế dừng an toàn (Graceful Shutdown): Đóng socket, ngắt thread, bắt {@link SocketException} mà không gây crash hay rò rỉ tài nguyên mạng.
 */
public class UdpServerWorker implements Runnable, AutoCloseable {

    /** Kích thước mảng đệm tối đa của gói tin UDP (65,507 bytes = 65,535 - 20 IP header - 8 UDP header) */
    public static final int BUFFER_SIZE = 65507;

    /** Thông điệp kiểm tra kết nối (Healthcheck) */
    public static final String PING = "PING";
    public static final String PONG = "PONG";

    private final int port;
    private final ExecutorService requestPool;
    private volatile Consumer<String> logListener;
    private volatile PacketHandler packetHandler;

    private volatile DatagramSocket socket;
    private volatile boolean running = false;
    private Thread workerThread;

    /**
     * Interface chức năng xử lý các gói tin nhận được ngoài PING (phục vụ mở rộng tích hợp cho SRV-03/SRV-04).
     */
    @FunctionalInterface
    public interface PacketHandler {
        void handlePacket(byte[] data, int length, InetAddress clientAddress, int clientPort);
    }

    /**
     * Khởi tạo UdpServerWorker với cổng chỉ định.
     *
     * @param port Cổng UDP cần lắng nghe (1024 - 65535, hoặc 0 để lấy cổng ngẫu nhiên khả dụng)
     */
    public UdpServerWorker(int port) {
        this(port, null, null);
    }

    /**
     * Khởi tạo UdpServerWorker với cổng và listener ghi log.
     *
     * @param port        Cổng UDP cần lắng nghe
     * @param logListener Bộ lắng nghe để ghi vết thông điệp log
     */
    public UdpServerWorker(int port, Consumer<String> logListener) {
        this(port, logListener, null);
    }

    /**
     * Khởi tạo UdpServerWorker đầy đủ tham số.
     *
     * @param port          Cổng UDP cần lắng nghe
     * @param logListener   Bộ lắng nghe ghi log (tự động đẩy lên EDT nếu có)
     * @param packetHandler Bộ xử lý gói tin tùy biến
     */
    public UdpServerWorker(int port, Consumer<String> logListener, PacketHandler packetHandler) {
        this.port = port;
        this.logListener = logListener;
        this.packetHandler = packetHandler;
        this.requestPool = Executors.newFixedThreadPool(10, r -> {
            Thread t = new Thread(r, "UdpServerWorker-Pool");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Khởi động socket UDP và bắt đầu vòng lặp lắng nghe trên thread nền.
     *
     * @throws SocketException Nếu cổng bị chiếm dụng hoặc không thể mở socket
     */
    public synchronized void start() throws SocketException {
        if (running) {
            return;
        }

        socket = new DatagramSocket(port);
        running = true;

        workerThread = new Thread(this, "UdpServerWorker-Port-" + getLocalPort());
        workerThread.setDaemon(true);
        workerThread.start();

        safeLog("[INFO] UDP Server da khoi dong thanh cong tren cong " + getLocalPort());
    }

    /**
     * Dừng socket UDP an toàn, giải phóng cổng và chấm dứt luồng worker.
     */
    public synchronized void stop() {
        if (!running && socket == null) {
            return;
        }

        running = false;

        if (socket != null && !socket.isClosed()) {
            socket.close();
        }

        if (workerThread != null && workerThread.isAlive()) {
            workerThread.interrupt();
        }

        requestPool.shutdown();
        try {
            if (!requestPool.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                requestPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            requestPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        safeLog("[INFO] UDP Server da dung lang nghe.");
    }

    /**
     * Alias thuận tiện tương thích với tên stopServer().
     */
    public void stopServer() {
        stop();
    }

    /**
     * Kiểm tra trạng thái máy chủ đang chạy và socket còn mở.
     */
    public boolean isRunning() {
        return running && socket != null && !socket.isClosed();
    }

    /**
     * Lấy cổng cấu hình ban đầu.
     */
    public int getPort() {
        return port;
    }

    /**
     * Lấy cổng thực tế socket đang liên kết (hữu ích khi chỉ định port 0).
     */
    public int getLocalPort() {
        return socket != null ? socket.getLocalPort() : port;
    }

    public void setLogListener(Consumer<String> logListener) {
        this.logListener = logListener;
    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    @Override
    public void close() {
        stop();
    }

    /**
     * Vòng lặp chính liên tục đón nhận gói tin UDP từ Client (chạy trên Worker Thread).
     */
    @Override
    public void run() {
        byte[] buffer = new byte[BUFFER_SIZE];

        while (running && socket != null && !socket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                int length = packet.getLength();
                byte[] dataCopy = new byte[length];
                System.arraycopy(packet.getData(), packet.getOffset(), dataCopy, 0, length);

                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                // Chuyển giao xử lý gói tin sang ThreadPool để không chặn vòng lặp đón nhận gói tiếp theo
                requestPool.submit(() -> processIncomingPacket(dataCopy, length, clientAddress, clientPort));

            } catch (SocketException e) {
                // Socket bị đóng do gọi stop() -> Kết thúc vòng lặp bình thường, không coi là lỗi
                if (!running) {
                    break;
                }
                safeLog("[WARN] Socket gap loi: " + e.getMessage());
            } catch (IOException e) {
                if (running) {
                    safeLog("[ERROR] Ngoai le I/O khi nhan goi UDP: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Nhận diện và xử lý gói tin UDP đến.
     */
    private void processIncomingPacket(byte[] data, int length, InetAddress clientAddress, int clientPort) {
        String message = new String(data, 0, length, StandardCharsets.UTF_8);
        String clientIp = clientAddress.getHostAddress();

        // 1. Nhận diện gói PING (Healthcheck) và phản hồi PONG tức thời
        if (isPingMessage(message)) {
            safeLog("[RECV][PING] tu " + clientIp + ":" + clientPort);
            sendPong(clientAddress, clientPort);
            return;
        }

        // 2. Chuyển giao gói tin khác cho handler (nếu đã đăng ký)
        if (packetHandler != null) {
            try {
                packetHandler.handlePacket(data, length, clientAddress, clientPort);
            } catch (Exception ex) {
                safeLog("[ERROR] Loi khi xu ly goi tin tu " + clientIp + ":" + clientPort + " - " + ex.getMessage());
            }
        } else {
            safeLog("[RECV] " + length + " bytes tu " + clientIp + ":" + clientPort + ": " + truncateLog(message, 50));
        }
    }

    /**
     * Kiểm tra nội dung có phải là gói PING hay không (không phân biệt chữ hoa, chữ thường).
     */
    public static boolean isPingMessage(String message) {
        return message != null && PING.equalsIgnoreCase(message.trim());
    }

    /**
     * Gửi gói tin PONG phản hồi lại Client tức thì.
     */
    public void sendPong(InetAddress clientAddress, int clientPort) {
        try {
            byte[] pongBytes = PONG.getBytes(StandardCharsets.UTF_8);
            DatagramPacket pongPacket = new DatagramPacket(pongBytes, pongBytes.length, clientAddress, clientPort);
            sendPacket(pongPacket);
            safeLog("[SENT][PONG] toi " + clientAddress.getHostAddress() + ":" + clientPort);
        } catch (IOException e) {
            safeLog("[ERROR] Khong the gui phan hoi PONG toi " + clientAddress.getHostAddress() + ":" + clientPort + " - " + e.getMessage());
        }
    }

    /**
     * Gửi một gói tin UDP bất kỳ từ Server.
     */
    public void sendPacket(DatagramPacket packet) throws IOException {
        DatagramSocket currentSocket = this.socket;
        if (currentSocket != null && !currentSocket.isClosed()) {
            currentSocket.send(packet);
        } else {
            throw new SocketException("Socket UDP da bi dong hoac chua khoi tao");
        }
    }

    /**
     * Gửi phản hồi dạng chuỗi văn bản UTF-8 tới địa chỉ và cổng chỉ định.
     */
    public void sendTextResponse(InetAddress clientAddress, int clientPort, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, clientAddress, clientPort);
        sendPacket(packet);
    }

    /**
     * Đẩy log an toàn qua SwingUtilities.invokeLater nếu đang chạy trong môi trường có giao diện đồ họa.
     */
    private void safeLog(String message) {
        Consumer<String> listener = this.logListener;
        if (listener == null) {
            return;
        }

        if (SwingUtilities.isEventDispatchThread()) {
            listener.accept(message);
        } else {
            SwingUtilities.invokeLater(() -> listener.accept(message));
        }
    }

    private static String truncateLog(String text, int maxLength) {
        if (text == null) return "";
        String clean = text.replace('\n', ' ').replace('\r', ' ');
        return clean.length() <= maxLength ? clean : clean.substring(0, maxLength) + "...";
    }
}
