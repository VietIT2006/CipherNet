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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Worker Thread chuyên trách lắng nghe và xử lý các gói tin UDP từ Client (SRV-02, SRV-03, SRV-04).
 * <p>
 * Đảm bảo các quy tắc SRS:
 * - BR-01: Bắt buộc đọc ngược bản mã từ file trên đĩa rồi mới giải mã để kiểm chứng I/O.
 * - NFR-02: Chuẩn hóa và xác thực đường dẫn file nằm trong thư mục {@code storageDir}.
 * - EX-06: Đồng bộ hóa thao tác ghi/đọc trên cùng một tên file khi nhiều Client gửi đồng thời.
 * - EX-05: Xử lý lỗi CSDL an toàn (ghi log cảnh báo và tiếp tục phản hồi UDP không sập Server);
 *          lỗi I/O hoặc định dạng gói tin phản hồi {@code ERROR|...}.
 *
 * @author NguyenTuanNhut (SRV-04: Tich hop luu CSDL va phan hoi RESULT/ERROR)
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

        String fileName = payload.getFileName();
        try {
            ProcessingResult result = executeCorePipeline(payload);
            log("[PIPELINE] Hoan tat Cipher -> File I/O -> Search cho "
                    + result.getFileName() + " (" + result.getMode() + ") trong " + result.getElapsedMs() + " ms");

            HistoryRecord record = new HistoryRecord(
                    clientIp,
                    result.getFileName(),
                    result.getMode().name(),
                    result.getOriginalContent(),
                    result.getEncryptedContent(),
                    result.getPositionsDisplay()
            );

            boolean dbSaved = dbHelper.saveHistory(record);
            if (dbSaved) {
                log("[DB] Da luu ban ghi ID=" + record.getId() + " (" + result.getMode().name()
                        + " / " + result.getFileName() + ") tu " + clientIp);
            } else {
                log("[WARN] Loi luu CSDL (EX-05) cho client " + clientIp + " nhung van tiep tuc phan hoi ket qua UDP.");
            }
            if (recordListener != null) {
                recordListener.accept(record);
            }

            String responseMessage = UdpProtocol.createResultMessage(result.getHelloPositions());
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

    /**
     * Thực thi trọn vẹn Core Processing Pipeline (SRV-03):
     * Mã hóa Vigenère -> Ghi file vào {@code storageDir} -> Đọc lại từ đĩa (BR-01)
     * -> Kiểm chứng toàn vẹn I/O -> Giải mã -> Tìm tất cả vị trí từ khóa "HELLO".
     *
     * @param payload dữ liệu gói {@code DATA} đã phân tách an toàn.
     * @return đối tượng {@link ProcessingResult} trung gian phục vụ lưu CSDL và phản hồi UDP.
     * @throws IllegalArgumentException nếu payload null hoặc tên file không hợp lệ (NFR-02).
     * @throws IOException              nếu xảy ra lỗi đọc/ghi file hoặc dữ liệu kiểm chứng không khớp.
     */
    public ProcessingResult executeCorePipeline(UdpProtocol.DataPayload payload) throws IOException {
        if (payload == null) {
            throw new IllegalArgumentException("Payload DATA khong duoc null");
        }
        return executeCorePipeline(payload.getMode(), payload.getFileName(), payload.getContent());
    }

    /**
     * Thực thi Core Processing Pipeline (SRV-03) từ các tham số bóc tách của gói {@code DATA}.
     *
     * @param mode            chế độ lưu trữ ({@link FileMode#TEXT} hoặc {@link FileMode#BINARY}).
     * @param fileName        tên file đích trong thư mục lưu trữ trên Server.
     * @param originalContent nội dung gốc từ Client.
     * @return kết quả trung gian {@link ProcessingResult}.
     * @throws IllegalArgumentException nếu tên file không hợp lệ (NFR-02).
     * @throws IOException              nếu lỗi I/O hoặc round-trip verification thất bại.
     */
    public ProcessingResult executeCorePipeline(FileMode mode, String fileName, String originalContent) throws IOException {
        long startNanos = System.nanoTime();
        FileMode safeMode = mode != null ? mode : FileMode.TEXT;
        String safeFileName = UdpProtocol.sanitizeFileName(fileName);
        String safeContent = originalContent != null ? originalContent : "";

        // Bước 1: Mã hóa nội dung bằng Key hiện tại trên Server
        String currentKey = this.cipherKey;
        String encryptedContent = VigenereCipher.encrypt(safeContent, currentKey);

        // Bước 2: Ghi file vào storageDir (server_storage/) và đọc ngược lại từ đĩa để kiểm chứng I/O (BR-01, EX-06)
        File targetFile = resolveSafeStorageFile(safeFileName);
        String readEncrypted;
        synchronized (getFileLock(safeFileName)) {
            FileHelper.writeFile(targetFile, encryptedContent, safeMode);
            readEncrypted = FileHelper.readFile(targetFile, safeMode);
        }

        // Bước 3: Kiểm chứng toàn vẹn Round-trip File I/O
        if (!encryptedContent.equals(readEncrypted)) {
            throw new IOException("Kiem chung toan ven File I/O that bai cho file '"
                    + safeFileName + "': du lieu doc lai khong khop ban ma da ghi.");
        }

        // Bước 4: Giải mã dữ liệu vừa đọc từ đĩa và đối chiếu lại với nội dung gốc
        String decryptedContent = VigenereCipher.decrypt(readEncrypted, currentKey);
        if (!safeContent.equals(decryptedContent)) {
            throw new IOException("Kiem chung toan ven giai ma Vigenere that bai cho file '"
                    + safeFileName + "': ban giai ma khong khop noi dung goc.");
        }

        // Bước 5: Tìm tất cả vị trí xuất hiện của từ khóa "HELLO"
        List<Integer> helloPositions = KeywordFinder.findPositions(decryptedContent);
        String positionsDisplay = helloPositions.isEmpty()
                ? UdpProtocol.MSG_NOT_FOUND
                : "[" + KeywordFinder.formatPositions(helloPositions) + "]";

        long elapsedMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
        return new ProcessingResult(
                safeMode,
                safeFileName,
                targetFile,
                safeContent,
                encryptedContent,
                readEncrypted,
                decryptedContent,
                helloPositions,
                positionsDisplay,
                elapsedMs
        );
    }

    /**
     * Đối tượng kết quả trung gian bất biến của Core Processing Pipeline (SRV-03),
     * dùng để bàn giao dữ liệu đã kiểm chứng toàn vẹn cho khâu lưu CSDL và phản hồi UDP (SRV-04).
     */
    public static final class ProcessingResult {
        private final FileMode mode;
        private final String fileName;
        private final File savedFile;
        private final String originalContent;
        private final String encryptedContent;
        private final String readBackEncryptedContent;
        private final String decryptedContent;
        private final List<Integer> helloPositions;
        private final String positionsDisplay;
        private final long elapsedMs;

        public ProcessingResult(FileMode mode, String fileName, File savedFile,
                                String originalContent, String encryptedContent,
                                String readBackEncryptedContent, String decryptedContent,
                                List<Integer> helloPositions, String positionsDisplay, long elapsedMs) {
            this.mode = mode;
            this.fileName = fileName;
            this.savedFile = savedFile;
            this.originalContent = originalContent;
            this.encryptedContent = encryptedContent;
            this.readBackEncryptedContent = readBackEncryptedContent;
            this.decryptedContent = decryptedContent;
            this.helloPositions = helloPositions == null
                    ? Collections.emptyList()
                    : Collections.unmodifiableList(new ArrayList<>(helloPositions));
            this.positionsDisplay = positionsDisplay;
            this.elapsedMs = elapsedMs;
        }

        public FileMode getMode() {
            return mode;
        }

        public String getFileName() {
            return fileName;
        }

        public File getSavedFile() {
            return savedFile;
        }

        public String getOriginalContent() {
            return originalContent;
        }

        public String getEncryptedContent() {
            return encryptedContent;
        }

        public String getReadBackEncryptedContent() {
            return readBackEncryptedContent;
        }

        public String getDecryptedContent() {
            return decryptedContent;
        }

        public List<Integer> getHelloPositions() {
            return helloPositions;
        }

        public String getPositionsDisplay() {
            return positionsDisplay;
        }

        public long getElapsedMs() {
            return elapsedMs;
        }

        public boolean isIntegrityVerified() {
            return Objects.equals(encryptedContent, readBackEncryptedContent)
                    && Objects.equals(originalContent, decryptedContent);
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
