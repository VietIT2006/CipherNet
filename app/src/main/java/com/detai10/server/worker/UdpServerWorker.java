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
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Worker Thread chuyên trách lắng nghe và xử lý các gói tin UDP từ Client (SRV-02, SRV-03).
 * <p>
 * Đảm bảo các quy tắc SRS:
 * - BR-01: Bắt buộc đọc ngược bản mã từ file trên đĩa rồi mới giải mã để kiểm chứng I/O.
 * - NFR-02: Chuẩn hóa và xác thực đường dẫn file nằm trong thư mục {@code storageDir}.
 * - EX-06: Đồng bộ hóa thao tác ghi/đọc trên cùng một tên file khi nhiều Client gửi đồng thời.
 * - EX-05: Xử lý lỗi CSDL hoặc I/O an toàn, ghi log và phản hồi {@code ERROR|...} không sập Server.
 */
public class UdpServerWorker implements Runnable {

    private final int port;
    private volatile String cipherKey;
    private final File storageDir;
    private final DatabaseHelper dbHelper;
    private final ExecutorService requestPool;
    private final Consumer<String> logListener;
    private final Consumer<HistoryRecord> recordListener;
    private final ConcurrentHashMap<String, Object> fileLocks = new ConcurrentHashMap<>();

    private volatile DatagramSocket socket;
    private volatile boolean running = false;
    private Thread workerThread;

    public UdpServerWorker(int port, String cipherKey, File storageDir, DatabaseHelper dbHelper,
                           Consumer<String> logListener, Consumer<HistoryRecord> recordListener) {
        this.port = port;
        this.cipherKey = VigenereCipher.normalizeKey(
                cipherKey != null && !cipherKey.trim().isEmpty() ? cipherKey : VigenereCipher.DEFAULT_KEY);
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
        if (!storageDir.exists()) {
            storageDir.mkdirs();
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
        this.cipherKey = VigenereCipher.normalizeKey(cipherKey);
    }

    @Override
    public void run() {
        byte[] buffer = new byte[UdpProtocol.BUFFER_SIZE];

        while (running && socket != null && !socket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

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

            log("[WARN] Nhan goi tin khong hop le tu " + clientIp + ":" + clientPort);
            sendResponse(UdpProtocol.createErrorMessage("Dinh dang goi tin khong hop le"), clientAddress, clientPort);

        } catch (Exception e) {
            log("[ERROR] Ngoai le khi xu ly goi tin tu " + clientIp + ": " + e.getMessage());
            sendResponse(UdpProtocol.createErrorMessage(e.getMessage()), clientAddress, clientPort);
        }
    }

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
            String currentKey = this.cipherKey;
            String encryptedContent = VigenereCipher.encrypt(originalContent, currentKey);

            File targetFile = resolveSafeStorageFile(fileName);
            String readEncrypted;
            // EX-06: Đồng bộ hóa thao tác ghi/đọc trên cùng một tên file khi nhiều Client gửi đồng thời
            synchronized (getFileLock(fileName)) {
                FileHelper.writeFile(targetFile, encryptedContent, mode);
                // BR-01: Đọc ngược lại bản mã từ file trên đĩa trước khi giải mã
                readEncrypted = FileHelper.readFile(targetFile, mode);
            }

            String decryptedContent = VigenereCipher.decrypt(readEncrypted, currentKey);
            List<Integer> helloPositionsList = KeywordFinder.findHelloIndices(decryptedContent);
            String positionsDisplay = helloPositionsList.isEmpty()
                    ? UdpProtocol.MSG_NOT_FOUND
                    : "[" + KeywordFinder.formatPositions(helloPositionsList) + "]";

            HistoryRecord record = new HistoryRecord(
                    clientIp,
                    fileName,
                    mode.name(),
                    originalContent,
                    encryptedContent,
                    positionsDisplay
            );

            boolean dbSaved = dbHelper.saveHistory(record);
            if (dbSaved) {
                log("[DB] Da luu ban ghi ID=" + record.getId() + " (" + mode.name() + " / " + fileName + ") tu " + clientIp);
            } else {
                log("[WARN] Loi luu CSDL (EX-05) cho client " + clientIp + " nhung van tiep tuc phan hoi ket qua UDP.");
            }
            if (recordListener != null) {
                recordListener.accept(record);
            }

            String responseMessage = UdpProtocol.createResultMessage(helloPositionsList);
            sendResponse(responseMessage, clientAddress, clientPort);
            log("[SUCCESS] Da phan hoi toi " + clientIp + ":" + clientPort + " -> " + responseMessage);

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

    private File resolveSafeStorageFile(String safeFileName) throws IOException {
        Path baseDir = storageDir.toPath().toAbsolutePath().normalize();
        Path resolved = baseDir.resolve(safeFileName).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new IOException("Duong dan file khong hop le ngoai thu muc luu tru: " + safeFileName);
        }
        return resolved.toFile();
    }

    private Object getFileLock(String fileName) {
        return fileLocks.computeIfAbsent(fileName, k -> new Object());
    }

    public void sendResponse(String message, InetAddress targetAddress, int targetPort) {
        if (socket == null || socket.isClosed()) {
            return;
        }
        try {
            DatagramPacket sendPacket = UdpProtocol.createUdpPacket(message, targetAddress, targetPort);
            socket.send(sendPacket);
        } catch (IOException | IllegalArgumentException e) {
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
