package com.detai10.e2e;

import com.detai10.core.cipher.VigenereCipher;
import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.io.FileHelper;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.server.worker.UdpServerWorker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ kiểm thử tích hợp E2E bằng {@link DatagramSocket} thật cho Task VER-03.
 * <p>
 * Bao phủ đầy đủ 5 kịch bản cốt lõi:
 * 1. Bắn PING -> nhận PONG tức thì.
 * 2. Không có Server -> đặt timeout 3s (setSoTimeout(3000)) bắt SocketTimeoutException.
 * 3. Bắn DATA chế độ TEXT (tiếng Việt UTF-8 + ký tự '|') -> assert RESULT, file trên đĩa & DB.
 * 4. Bắn DATA chế độ BINARY (có và không có HELLO) -> assert RESULT, file DataStream & DB.
 * 5. Nhiều Client DatagramSocket thật bắn trùng tên file đồng thời + chặn Path Traversal '../'.
 */
class UdpEndToEndSuiteTest {

    private static final String SERVER_KEY = "KEYSECRET";

    @TempDir
    File tempStorageDir;

    private DatabaseHelper dbHelper;
    private UdpServerWorker serverWorker;
    private int serverPort;
    private InetAddress localhost;

    @BeforeEach
    void setUp() throws Exception {
        localhost = InetAddress.getByName("127.0.0.1");
        dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();

        serverWorker = new UdpServerWorker(0, SERVER_KEY, tempStorageDir, dbHelper, null, null);
        serverWorker.start();
        serverPort = serverWorker.getLocalPort();
    }

    @AfterEach
    void tearDown() {
        if (serverWorker != null) {
            serverWorker.stop();
        }
    }

    private String sendRawOverRealSocket(String rawMessage, int targetPort, int timeoutMs) throws IOException {
        try (DatagramSocket clientSocket = new DatagramSocket()) {
            clientSocket.setSoTimeout(timeoutMs);
            DatagramPacket sendPacket = UdpProtocol.createUdpPacket(rawMessage, localhost, targetPort);
            clientSocket.send(sendPacket);

            DatagramPacket receivePacket = UdpProtocol.createReceiveBufferPacket();
            clientSocket.receive(receivePacket);
            return UdpProtocol.extractPacketMessage(receivePacket);
        }
    }

    @Test
    @DisplayName("VER-03 [1]: Bắn gói PING qua DatagramSocket thật nhận phản hồi PONG")
    void testRealSocketPingPong() throws IOException {
        String response = sendRawOverRealSocket(UdpProtocol.createPingMessage(), serverPort, 3000);
        assertTrue(UdpProtocol.isPong(response));
        assertEquals("PONG", response);
    }

    @Test
    @DisplayName("VER-03 [2]: Không có Server -> DatagramSocket timeout 3s ném SocketTimeoutException")
    void testRealSocketPingTimeoutWhenServerDown() throws Exception {
        int closedPort;
        try (DatagramSocket probe = new DatagramSocket(0)) {
            closedPort = probe.getLocalPort();
        }
        serverWorker.stop();

        long start = System.currentTimeMillis();
        assertThrows(SocketTimeoutException.class,
                () -> sendRawOverRealSocket(UdpProtocol.createPingMessage(), closedPort, 3000));
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 2500, "Socket timeout 3s phải chờ ít nhất ~3000ms (thực tế: " + elapsed + "ms)");
    }

    @Test
    @DisplayName("VER-03 [3]: E2E chế độ TEXT (chứa ký tự '|', UTF-8, HELLO) -> Assert UDP + File + DB")
    void testRealSocketDataTextModeWithPipes() throws Exception {
        String content = "Xin chào! HELLO|Mạng|UDP và hello 2026!";
        String packet = UdpProtocol.createDataMessage(FileMode.TEXT, "e2e_text.txt", content);

        String rawResponse = sendRawOverRealSocket(packet, serverPort, 5000);
        UdpProtocol.ResponsePayload parsed = UdpProtocol.parseResponsePacket(rawResponse);

        assertTrue(parsed.isFound());
        assertEquals("OK", parsed.getStatus());
        assertEquals("Vi tri HELLO: [10, 28]", parsed.getMessage());

        // Kiểm chứng file mã hóa lưu trên đĩa (TEXT UTF-8)
        File savedFile = new File(tempStorageDir, "e2e_text.txt");
        assertTrue(savedFile.exists());
        String encryptedOnDisk = FileHelper.readFile(savedFile, FileMode.TEXT);
        assertNotEquals(content, encryptedOnDisk);
        assertEquals(content, VigenereCipher.decrypt(encryptedOnDisk, SERVER_KEY));

        // Kiểm chứng bản ghi trong CSDL (bảng lich_su_xu_ly)
        List<HistoryRecord> rows = dbHelper.getAllHistory();
        assertEquals(1, rows.size());
        HistoryRecord rec = rows.get(0);
        assertEquals("127.0.0.1", rec.getClientIp());
        assertEquals("e2e_text.txt", rec.getFileName());
        assertEquals("TEXT", rec.getIoMode());
        assertEquals(content, rec.getOriginalContent());
        assertEquals(encryptedOnDisk, rec.getEncryptedContent());
        assertEquals("[10, 28]", rec.getHelloPositions());
        assertNotNull(rec.getCreatedAt());
    }

    @Test
    @DisplayName("VER-03 [4]: E2E chế độ BINARY (có và không có HELLO) -> Assert UDP + DataStream + DB")
    void testRealSocketDataBinaryMode() throws Exception {
        String withHello = "Binary payload HELLO|DataStream 123";
        String respFound = sendRawOverRealSocket(
                UdpProtocol.createDataMessage(FileMode.BINARY, "e2e_bin_1.dat", withHello), serverPort, 5000);
        UdpProtocol.ResponsePayload parsedFound = UdpProtocol.parseResponsePacket(respFound);
        assertTrue(parsedFound.isFound());
        assertEquals("Vi tri HELLO: [15]", parsedFound.getMessage());

        File binFile1 = new File(tempStorageDir, "e2e_bin_1.dat");
        assertTrue(binFile1.exists());
        assertEquals(withHello, VigenereCipher.decrypt(FileHelper.readFile(binFile1, FileMode.BINARY), SERVER_KEY));

        String withoutHello = "Khong co tu khoa nao trong goi nhi phan";
        String respNotFound = sendRawOverRealSocket(
                UdpProtocol.createDataMessage(FileMode.BINARY, "e2e_bin_2.dat", withoutHello), serverPort, 5000);
        UdpProtocol.ResponsePayload parsedNotFound = UdpProtocol.parseResponsePacket(respNotFound);
        assertFalse(parsedNotFound.isFound());
        assertEquals("NOT_FOUND", parsedNotFound.getStatus());

        List<HistoryRecord> rows = dbHelper.getAllHistory();
        assertEquals(2, rows.size());
        assertEquals("BINARY", rows.get(0).getIoMode());
        assertEquals("BINARY", rows.get(1).getIoMode());
    }

    @Test
    @DisplayName("VER-03 [5]: Nhiều Client DatagramSocket thật bắn trùng tên file đồng thời + chặn '../'")
    void testRealSocketConcurrentSameFileAndPathTraversal() throws Exception {
        // Kiểm tra chặn gói tin giả mạo chứa Path Traversal ../
        String errResp = sendRawOverRealSocket("DATA|TEXT|../hack.txt|HELLO", serverPort, 3000);
        UdpProtocol.ResponsePayload errParsed = UdpProtocol.parseResponsePacket(errResp);
        assertTrue(errParsed.isError());

        // 8 luồng Client mở DatagramSocket độc lập gửi đồng thời vào cùng 1 tên file
        int clients = 8;
        ExecutorService pool = Executors.newFixedThreadPool(clients);
        CountDownLatch latch = new CountDownLatch(clients);
        AtomicInteger successCount = new AtomicInteger(0);
        java.util.List<Throwable> workerErrors = new java.util.concurrent.CopyOnWriteArrayList<>();

        for (int i = 0; i < clients; i++) {
            final int id = i;
            pool.submit(() -> {
                try {
                    String content = "Client#" + id + "|HELLO concurrent socket test";
                    String raw = UdpProtocol.createDataMessage(FileMode.TEXT, "shared_e2e.txt", content);
                    String resp = sendRawOverRealSocket(raw, serverPort, 5000);
                    UdpProtocol.ResponsePayload parsed = UdpProtocol.parseResponsePacket(resp);
                    if (parsed.isFound() && "Vi tri HELLO: [9]".equals(parsed.getMessage())) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    workerErrors.add(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(15, TimeUnit.SECONDS));
        pool.shutdown();

        assertTrue(workerErrors.isEmpty(), "Không được có ngoại lệ trên luồng Client: " + workerErrors);
        assertEquals(clients, successCount.get());
        assertEquals(clients, dbHelper.getAllHistory().size());
    }
}
