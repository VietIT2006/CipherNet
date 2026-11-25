package com.detai10.server.worker;

import com.detai10.core.cipher.VigenereCipher;
import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.io.FileHelper;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.core.search.KeywordFinder;

import javax.swing.SwingUtilities;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Worker Thread chuyên trách lắng nghe socket UDP và điều phối xử lý gói tin (SRV-02, SRV-04, SRV-05).
 * <p>
 * Yêu cầu kỹ thuật & Kiến trúc:
 * 1. Chạy trên Worker Thread độc lập để không làm nghẽn Event Dispatch Thread (EDT) của giao diện Swing.
 * 2. Mở {@link DatagramSocket} trên cổng chỉ định với mảng buffer kích thước tối đa 65,507 bytes (Max UDP Payload).
 * 3. Tự động nhận diện gói tin Healthcheck {@code PING} (case-insensitive) và gửi phản hồi {@code PONG} tức thì (&lt; 50ms).
 * 4. Tích hợp chu trình xử lý gói {@code DATA}:
 *    - Bóc tách an toàn với giới hạn 4 phần tử (Quy tắc 3: Safe Payload Parsing).
 *    - Mã hóa Vigenère bảo toàn ký tự số, ký hiệu (Quy tắc 4).
 *    - Ghi file và đọc lại kiểm chứng (Round-trip) theo chế độ TEXT/BINARY (Quy tắc 5).
 *    - Tìm kiếm toàn bộ vị trí từ khóa "HELLO" bằng {@link KeywordFinder} (CORE-05).
 *    - Lưu trữ lịch sử giao dịch vào CSDL qua {@link DatabaseHelper} bằng PreparedStatement (Quy tắc 6 / CORE-06).
 *    - Phản hồi gói UDP {@code RESULT|FOUND|...}, {@code RESULT|NOT_FOUND|...} hoặc {@code ERROR|...} về Client (&lt; 100ms).
 * 5. Đồng bộ thời gian thực với giao diện Server GUI qua {@link TransactionListener} và {@link Consumer} log listener (SRV-05).
 * 6. Mọi thao tác cập nhật giao diện (Log, bảng JTable) đều được bọc an toàn trong {@code SwingUtilities.invokeLater()}.
 * 7. Cơ chế dừng an toàn (Graceful Shutdown): Đóng socket, ngắt thread, bắt {@link SocketException} mà không gây crash hay rò rỉ tài nguyên mạng.
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
    private volatile TransactionListener transactionListener;
    private volatile Consumer<HistoryRecord> recordListener;

    private volatile String cipherKey = VigenereCipher.DEFAULT_KEY;
    private volatile File storageDir = new File("server_storage");
    private volatile DatabaseHelper dbHelper = DatabaseHelper.getInstance();

    private volatile DatagramSocket socket;
    private volatile boolean running = false;
    private Thread workerThread;

    /**
     * Interface chức năng xử lý các gói tin nhận được ngoài PING.
     */
    @FunctionalInterface
    public interface PacketHandler {
        void handlePacket(byte[] data, int length, InetAddress clientAddress, int clientPort);
    }

    /**
     * Sự kiện giao dịch dữ liệu UDP chuyển giao lên bảng JTable của Server GUI (SRV-05).
     */
    public static class TransactionEvent {
        private final String clientIp;
        private final int clientPort;
        private final String fileName;
        private final String fileMode;
        private final String originalContent;
        private final String encryptedContent;
        private final String helloPositions;
        private final int byteCount;
        private final long timestamp;

        public TransactionEvent(String clientIp, int clientPort, String fileName, String fileMode,
                                String originalContent, String encryptedContent, String helloPositions, int byteCount) {
            this.clientIp = clientIp != null ? clientIp : "UNKNOWN";
            this.clientPort = clientPort;
            this.fileName = fileName != null ? fileName : "unnamed.dat";
            this.fileMode = fileMode != null ? fileMode : "TEXT";
            this.originalContent = originalContent != null ? originalContent : "";
            this.encryptedContent = encryptedContent != null ? encryptedContent : "";
            this.helloPositions = helloPositions != null ? helloPositions : "-";
            this.byteCount = byteCount;
            this.timestamp = System.currentTimeMillis();
        }

        public String getClientIp() { return clientIp; }
        public int getClientPort() { return clientPort; }
        public String getFileName() { return fileName; }
        public String getFileMode() { return fileMode; }
        public String getOriginalContent() { return originalContent; }
        public String getEncryptedContent() { return encryptedContent; }
        public String getHelloPositions() { return helloPositions; }
        public int getByteCount() { return byteCount; }
        public long getTimestamp() { return timestamp; }
    }

    /**
     * Interface lắng nghe sự kiện giao dịch mới từ luồng mạng UDP để đồng bộ lên bảng JTable (SRV-05).
     */
    @FunctionalInterface
    public interface TransactionListener {
        void onTransaction(TransactionEvent event);
    }

    public UdpServerWorker(int port) {
        this(port, null, null);
    }

    public UdpServerWorker(int port, Consumer<String> logListener) {
        this(port, logListener, null);
    }

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

    public UdpServerWorker(int port, String cipherKey, File storageDir, DatabaseHelper dbHelper,
                           Consumer<String> logListener, Consumer<HistoryRecord> recordListener) {
        this(port, logListener, null);
        if (cipherKey != null && !cipherKey.trim().isEmpty()) {
            this.cipherKey = cipherKey.trim();
        }
        if (storageDir != null) {
            this.storageDir = storageDir;
        }
        if (dbHelper != null) {
            this.dbHelper = dbHelper;
        }
        this.recordListener = recordListener;
    }

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

    public void stopServer() {
        stop();
    }

    public boolean isRunning() {
        return running && socket != null && !socket.isClosed();
    }

    public int getPort() {
        return port;
    }

    public int getLocalPort() {
        return socket != null ? socket.getLocalPort() : port;
    }

    public String getCipherKey() {
        return cipherKey;
    }

    public void setCipherKey(String cipherKey) {
        this.cipherKey = cipherKey != null && !cipherKey.trim().isEmpty() ? cipherKey.trim() : VigenereCipher.DEFAULT_KEY;
    }

    public File getStorageDir() {
        return storageDir;
    }

    public void setStorageDir(File storageDir) {
        this.storageDir = storageDir;
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void setLogListener(Consumer<String> logListener) {
        this.logListener = logListener;
    }

    public void setPacketHandler(PacketHandler packetHandler) {
        this.packetHandler = packetHandler;
    }

    public void setTransactionListener(TransactionListener transactionListener) {
        this.transactionListener = transactionListener;
    }

    public void setRecordListener(Consumer<HistoryRecord> recordListener) {
        this.recordListener = recordListener;
    }

    @Override
    public void close() {
        stop();
    }

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

                requestPool.submit(() -> processIncomingPacket(dataCopy, length, clientAddress, clientPort));

            } catch (SocketException e) {
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

    private void processIncomingPacket(byte[] data, int length, InetAddress clientAddress, int clientPort) {
        String message = new String(data, 0, length, StandardCharsets.UTF_8);
        String clientIp = clientAddress.getHostAddress();

        // 1. Nhận diện gói PING (Healthcheck) và phản hồi PONG tức thời
        if (isPingMessage(message)) {
            safeLog("[RECV][PING] " + length + " bytes tu " + clientIp + ":" + clientPort);
            sendPong(clientAddress, clientPort);
            return;
        }

        // 2. Nhận diện gói DATA|<mode>|<fileName>|<content> (Quy tắc 3: Safe Payload Parsing với split limit 4)
        if (message.startsWith("DATA|")) {
            String[] parts = message.split("\\|", 4);
            if (parts.length >= 4) {
                String modeStr = parts[1].trim();
                String fileName = parts[2].trim();
                String content = parts[3];
                FileMode mode = FileMode.fromString(modeStr);

                safeLog("[RECV][DATA] " + length + " bytes tu " + clientIp + ":" + clientPort + " | File: " + fileName + " (" + mode + ")");

                // Tích hợp xử lý Core & CSDL & Phản hồi (SRV-04)
                try {
                    // 1. Mã hóa nội dung văn bản bằng Vigenère Cipher
                    String currentKey = this.cipherKey != null ? this.cipherKey : VigenereCipher.DEFAULT_KEY;
                    String encryptedContent = VigenereCipher.encrypt(content, currentKey);

                    // 2. Ghi file mã hóa xuống thư mục server_storage/ và đọc lại kiểm chứng (Round-trip)
                    File targetFile = new File(this.storageDir, fileName);
                    FileHelper.writeFile(targetFile, encryptedContent, mode);
                    String readEncrypted = FileHelper.readFile(targetFile, mode);
                    String decryptedContent = VigenereCipher.decrypt(readEncrypted, currentKey);

                    // 3. Quét tìm tất cả các vị trí xuất hiện của từ khóa "HELLO"
                    List<Integer> helloPositionsList = KeywordFinder.findHelloPositions(decryptedContent);
                    String positionsStr = KeywordFinder.formatPositions(helloPositionsList);

                    // 4. Lưu toàn bộ giao dịch vào CSDL qua PreparedStatement (chống SQL Injection 100%)
                    HistoryRecord record = new HistoryRecord(
                            clientIp,
                            fileName,
                            mode.name(),
                            content,
                            encryptedContent,
                            positionsStr
                    );

                    DatabaseHelper helper = this.dbHelper != null ? this.dbHelper : DatabaseHelper.getInstance();
                    boolean dbSaved = helper.saveHistory(record);
                    if (dbSaved) {
                        safeLog("[DB] Da luu lich su giao dich ID=" + record.getId() + " cho client " + clientIp);
                        if (recordListener != null) {
                            recordListener.accept(record);
                        }
                    } else {
                        safeLog("[WARN] Khong the luu giao dich vao CSDL cho client " + clientIp);
                    }

                    // 5. Đồng bộ sự kiện giao dịch lên Server GUI (JTable & Log qua TransactionListener)
                    safeTransaction(new TransactionEvent(
                            clientIp, clientPort, fileName, mode.name(), content, encryptedContent, positionsStr, length));

                    // 6. Đóng gói và phát gói phản hồi UDP về lại Client
                    String responseMessage;
                    if (helloPositionsList.isEmpty()) {
                        responseMessage = UdpProtocol.createResultNotFoundMessage();
                    } else {
                        responseMessage = UdpProtocol.createResultFoundMessage(positionsStr);
                    }

                    sendTextResponse(clientAddress, clientPort, responseMessage);
                    safeLog("[SUCCESS] Da phan hoi ve " + clientIp + ":" + clientPort + " -> " + responseMessage);

                } catch (Exception ex) {
                    safeLog("[ERROR] Loi khi xu ly goi tin DATA tu " + clientIp + ":" + clientPort + " - " + ex.getMessage());
                    try {
                        sendTextResponse(clientAddress, clientPort, UdpProtocol.createErrorMessage("Loi xu ly tren Server: " + ex.getMessage()));
                    } catch (IOException e) {
                        safeLog("[ERROR] Khong the gui phan hoi loi ve " + clientIp + ": " + e.getMessage());
                    }
                }
            } else {
                safeLog("[WARN][DATA-MALFORMED] " + length + " bytes tu " + clientIp + ":" + clientPort + ": sai cu phap");
                try {
                    sendTextResponse(clientAddress, clientPort, UdpProtocol.createErrorMessage("Sai dinh dang goi tin DATA"));
                } catch (IOException ignored) {
                }
            }
        } else if (packetHandler != null) {
            // 3. Chuyển giao gói tin khác cho handler tùy biến
            try {
                packetHandler.handlePacket(data, length, clientAddress, clientPort);
            } catch (Exception ex) {
                safeLog("[ERROR] Loi khi xu ly goi tin tu " + clientIp + ":" + clientPort + " - " + ex.getMessage());
            }
        } else {
            safeLog("[RECV] " + length + " bytes tu " + clientIp + ":" + clientPort + ": " + truncateLog(message, 50));
        }
    }

    public static boolean isPingMessage(String message) {
        return message != null && PING.equalsIgnoreCase(message.trim());
    }

    public void sendPong(InetAddress clientAddress, int clientPort) {
        try {
            byte[] pongBytes = PONG.getBytes(StandardCharsets.UTF_8);
            DatagramPacket pongPacket = new DatagramPacket(pongBytes, pongBytes.length, clientAddress, clientPort);
            sendPacket(pongPacket);
            safeLog("[SENT][PONG] " + pongBytes.length + " bytes toi " + clientAddress.getHostAddress() + ":" + clientPort);
        } catch (IOException e) {
            safeLog("[ERROR] Khong the gui phan hoi PONG toi " + clientAddress.getHostAddress() + ":" + clientPort + " - " + e.getMessage());
        }
    }

    public void sendPacket(DatagramPacket packet) throws IOException {
        DatagramSocket currentSocket = this.socket;
        if (currentSocket != null && !currentSocket.isClosed()) {
            currentSocket.send(packet);
        } else {
            throw new SocketException("Socket UDP da bi dong hoac chua khoi tao");
        }
    }

    public void sendTextResponse(InetAddress clientAddress, int clientPort, String responseText) throws IOException {
        byte[] bytes = responseText.getBytes(StandardCharsets.UTF_8);
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, clientAddress, clientPort);
        sendPacket(packet);
        safeLog("[SENT] " + bytes.length + " bytes toi " + clientAddress.getHostAddress() + ":" + clientPort);
    }

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

    private void safeTransaction(TransactionEvent event) {
        TransactionListener listener = this.transactionListener;
        if (listener == null) {
            return;
        }

        if (SwingUtilities.isEventDispatchThread()) {
            listener.onTransaction(event);
        } else {
            SwingUtilities.invokeLater(() -> listener.onTransaction(event));
        }
    }

    private static String truncateLog(String text, int maxLength) {
        if (text == null) return "";
        String clean = text.replace('\n', ' ').replace('\r', ' ');
        return clean.length() <= maxLength ? clean : clean.substring(0, maxLength) + "...";
    }
}
