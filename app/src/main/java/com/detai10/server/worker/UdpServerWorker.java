package com.detai10.server.worker;

import com.detai10.core.cipher.VigenereCipher;
import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.io.FileHelper;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.core.search.KeywordFinder;

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
 * Worker Thread chuyên trách lắng nghe và xử lý các gói tin UDP từ Client (SRV-02, SRV-04).
 * <p>
 * Quy trình xử lý gói tin DATA:
 * 1. Bóc tách an toàn gói DATA|&lt;mode&gt;|&lt;fileName&gt;|&lt;content&gt;
 * 2. Mã hóa nội dung văn bản bằng Vigenère Cipher với serverKey
 * 3. Ghi file mã hóa xuống thư mục server_storage/ theo chế độ TEXT hoặc BINARY
 * 4. Đọc ngược lại từ file để xác nhận toàn vẹn (Round-trip verification)
 * 5. Giải mã và quét tìm tất cả vị trí xuất hiện của từ khóa "HELLO"
 * 6. Lưu toàn bộ giao dịch vào CSDL qua DatabaseHelper.saveHistory(record) (100% PreparedStatement)
 * 7. Đóng gói và phát gói phản hồi UDP về lại địa chỉ IP & Port của Client:
 *    - Tìm thấy: RESULT|FOUND|&lt;danh_sách_vị_trí&gt;
 *    - Không tìm thấy: RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO
 *    - Gặp lỗi: ERROR|&lt;thông_điệp_lỗi&gt;
 */
public class UdpServerWorker implements Runnable {

    private final int port;
    private volatile String cipherKey;
    private final File storageDir;
    private final DatabaseHelper dbHelper;
    private final ExecutorService requestPool;
    private final Consumer<String> logListener;
    private final Consumer<HistoryRecord> recordListener;

    private volatile DatagramSocket socket;
    private volatile boolean running = false;
    private Thread workerThread;

    public UdpServerWorker(int port, String cipherKey, File storageDir, DatabaseHelper dbHelper,
                           Consumer<String> logListener, Consumer<HistoryRecord> recordListener) {
        this.port = port;
        this.cipherKey = cipherKey != null && !cipherKey.trim().isEmpty() ? cipherKey.trim() : VigenereCipher.DEFAULT_KEY;
        this.storageDir = storageDir != null ? storageDir : new File("server_storage");
        this.dbHelper = dbHelper != null ? dbHelper : DatabaseHelper.getInstance();
        this.logListener = logListener;
        this.recordListener = recordListener;
        this.requestPool = Executors.newFixedThreadPool(10);
    }

    public synchronized void start() throws SocketException {
        if (running) {
            return;
        }
        socket = new DatagramSocket(port);
        running = true;
        workerThread = new Thread(this, "UdpServerWorker-Port-" + port);
        workerThread.setDaemon(true);
        workerThread.start();
        log("[INFO] Server UDP Worker da khoi dong tren port " + getLocalPort());
    }

    public synchronized void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        if (workerThread != null) {
            workerThread.interrupt();
        }
        requestPool.shutdown();
        try {
            if (!requestPool.awaitTermination(1, TimeUnit.SECONDS)) {
                requestPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            requestPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log("[INFO] Server UDP Worker da dung.");
    }

    public boolean isRunning() {
        return running && socket != null && !socket.isClosed();
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

    @Override
    public void run() {
        byte[] buffer = new byte[UdpProtocol.BUFFER_SIZE];

        while (running && socket != null && !socket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                // Sao chép byte thực tế nhận được để xử lý trong thread pool
                int length = packet.getLength();
                byte[] dataCopy = new byte[length];
                System.arraycopy(packet.getData(), packet.getOffset(), dataCopy, 0, length);
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                requestPool.submit(() -> handlePacket(dataCopy, clientAddress, clientPort));

            } catch (SocketException e) {
                if (!running) {
                    break;
                }
                log("[WARN] Socket dong hoac gap loi: " + e.getMessage());
            } catch (IOException e) {
                if (running) {
                    log("[ERROR] Loi khi nhan goi UDP: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Phân loại và xử lý từng gói tin nhận được từ Client.
     */
    public void handlePacket(byte[] data, InetAddress clientAddress, int clientPort) {
        String rawMessage = new String(data, StandardCharsets.UTF_8);
        String clientIp = clientAddress.getHostAddress();

        try {
            if (UdpProtocol.isPing(rawMessage)) {
                log("[PING] Nhan ping tu " + clientIp + ":" + clientPort + " -> Phan hoi PONG");
                sendResponse(UdpProtocol.PONG, clientAddress, clientPort);
                return;
            }

            if (rawMessage.startsWith(UdpProtocol.PREFIX_DATA + "|")) {
                processDataPacket(rawMessage, clientIp, clientAddress, clientPort);
                return;
            }

            // Gói tin không xác định
            log("[WARN] Nhan goi tin khong hop le tu " + clientIp + ":" + clientPort);
            sendResponse(UdpProtocol.createErrorMessage("Dinh dang goi tin khong hop le"), clientAddress, clientPort);

        } catch (Exception e) {
            log("[ERROR] Ngoai le khi xu ly goi tin tu " + clientIp + ": " + e.getMessage());
            sendResponse(UdpProtocol.createErrorMessage(e.getMessage()), clientAddress, clientPort);
        }
    }

    /**
     * Tích hợp xử lý trọn vẹn gói tin DATA (SRV-03 / SRV-04):
     * Bóc tách -> Mã hóa -> Lưu file -> Tìm HELLO -> Ghi CSDL -> Gửi phản hồi RESULT/ERROR.
     */
    private void processDataPacket(String rawMessage, String clientIp, InetAddress clientAddress, int clientPort) {
        UdpProtocol.DataPayload payload;
        try {
            payload = UdpProtocol.parseDataPacket(rawMessage);
        } catch (IllegalArgumentException e) {
            log("[ERROR] Loi parse DATA packet tu " + clientIp + ": " + e.getMessage());
            sendResponse(UdpProtocol.createErrorMessage(e.getMessage()), clientAddress, clientPort);
            return;
        }

        FileMode mode = payload.getMode();
        String fileName = payload.getFileName();
        String originalContent = payload.getContent();

        try {
            // 1. Mã hóa văn bản với Server Key
            String currentKey = this.cipherKey;
            String encryptedContent = VigenereCipher.encrypt(originalContent, currentKey);

            // 2. Lưu file mã hóa theo chế độ TEXT hoặc BINARY
            File targetFile = new File(storageDir, fileName);
            FileHelper.writeFile(targetFile, encryptedContent, mode);

            // 3. Đọc lại file kiểm chứng (Round-trip) và giải mã để xác thực
            String readEncrypted = FileHelper.readFile(targetFile, mode);
            String decryptedContent = VigenereCipher.decrypt(readEncrypted, currentKey);

            // 4. Tìm kiếm từ khóa "HELLO" trong nội dung đã giải mã (hoặc nguyên bản)
            List<Integer> helloPositionsList = KeywordFinder.findHelloPositions(decryptedContent);
            String positionsStr = KeywordFinder.formatPositions(helloPositionsList);

            // 5. Lưu bản ghi lịch sử vào CSDL an toàn qua PreparedStatement (SRV-04 / CORE-06)
            HistoryRecord record = new HistoryRecord(
                    clientIp,
                    fileName,
                    mode.name(),
                    originalContent,
                    encryptedContent,
                    positionsStr
            );
            boolean dbSaved = dbHelper.saveHistory(record);
            if (dbSaved) {
                log("[DB] Da luu ban ghi lich su ID=" + record.getId() + " cho client " + clientIp);
                if (recordListener != null) {
                    recordListener.accept(record);
                }
            } else {
                log("[WARN] Khong the luu ban ghi vao CSDL cho client " + clientIp);
            }

            // 6. Đóng gói bản tin phản hồi RESULT (SRV-04)
            String responseMessage;
            if (helloPositionsList.isEmpty()) {
                responseMessage = UdpProtocol.createResultNotFoundMessage();
            } else {
                responseMessage = UdpProtocol.createResultFoundMessage(positionsStr);
            }

            // 7. Gửi gói tin phản hồi về Client
            sendResponse(responseMessage, clientAddress, clientPort);
            log("[SUCCESS] Da phan hoi goi tin ve " + clientIp + ":" + clientPort + " -> " + responseMessage);

        } catch (IOException e) {
            log("[ERROR] Loi I/O khi xu ly file " + fileName + ": " + e.getMessage());
            sendResponse(UdpProtocol.createErrorMessage("Loi ghi/doc file tren Server: " + e.getMessage()),
                    clientAddress, clientPort);
        } catch (Exception e) {
            log("[ERROR] Loi xu ly nghiep vu cho client " + clientIp + ": " + e.getMessage());
            sendResponse(UdpProtocol.createErrorMessage("Loi may chu: " + e.getMessage()),
                    clientAddress, clientPort);
        }
    }

    /**
     * Gửi gói tin UDP chứa phản hồi UTF-8 về địa chỉ IP và Port của Client.
     */
    public void sendResponse(String message, InetAddress targetAddress, int targetPort) {
        if (socket == null || socket.isClosed()) {
            return;
        }
        try {
            byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
            DatagramPacket sendPacket = new DatagramPacket(bytes, bytes.length, targetAddress, targetPort);
            socket.send(sendPacket);
        } catch (IOException e) {
            log("[ERROR] Khong the gui phan hoi ve " + targetAddress.getHostAddress() + ":" + targetPort + " - " + e.getMessage());
        }
    }

    private void log(String message) {
        if (logListener != null) {
            logListener.accept(message);
        } else {
            System.out.println(message);
        }
    }
}
