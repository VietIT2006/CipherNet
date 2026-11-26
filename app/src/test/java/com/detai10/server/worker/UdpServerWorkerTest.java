package com.detai10.server.worker;

import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ Unit & Integration Test cho UdpServerWorker (SRV-02, SRV-03).
 */
class UdpServerWorkerTest {

    @TempDir
    File tempStorageDir;

    private DatabaseHelper dbHelper;
    private UdpServerWorker serverWorker;
    private DatagramSocket clientSocket;
    private int serverPort;

    @BeforeEach
    void setUp() throws Exception {
        dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();

        serverWorker = new UdpServerWorker(0, "SECRETKEY", tempStorageDir, dbHelper, null, null);
        serverWorker.start();
        serverPort = serverWorker.getLocalPort();

        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(3000);
    }

    @AfterEach
    void tearDown() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
        if (serverWorker != null) {
            serverWorker.stop();
        }
    }

    private String sendAndReceive(String message) throws IOException {
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
        InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
        clientSocket.send(sendPacket);

        byte[] receiveData = new byte[4096];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);

        return new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength(), StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Gửi PING nhận phản hồi PONG tức thì")
    void testPingPong() throws IOException {
        assertEquals("PONG", sendAndReceive("PING"));
    }

    @Test
    @DisplayName("Gửi DATA chứa từ khóa HELLO -> Lưu CSDL và nhận RESULT|OK|Vi tri HELLO: [...]")
    void testDataWithHello() throws IOException {
        String originalContent = "Xin chao, hello Vietnam! HELLO world.";
        String message = UdpProtocol.createDataMessage(FileMode.TEXT, "test_hello.txt", originalContent);

        String response = sendAndReceive(message);
        assertEquals("RESULT|OK|Vi tri HELLO: [10, 25]", response);

        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(1, historyList.size());

        HistoryRecord record = historyList.get(0);
        assertEquals("test_hello.txt", record.getFileName());
        assertEquals("TEXT", record.getIoMode());
        assertEquals(originalContent, record.getOriginalContent());
        assertNotNull(record.getEncryptedContent());
        assertEquals("[10, 25]", record.getHelloPositions());
    }

    @Test
    @DisplayName("Gửi DATA không chứa từ khóa HELLO -> Lưu CSDL và nhận RESULT|NOT_FOUND|...")
    void testDataWithoutHello() throws IOException {
        String originalContent = "Chuc mung mot ngay lam viec tot lanh!";
        String message = UdpProtocol.createDataMessage(FileMode.BINARY, "data_binary.dat", originalContent);

        String response = sendAndReceive(message);
        assertEquals(UdpProtocol.createResultNotFoundMessage(), response);

        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(1, historyList.size());
        assertEquals("BINARY", historyList.get(0).getIoMode());
        assertEquals(UdpProtocol.MSG_NOT_FOUND, historyList.get(0).getHelloPositions());
    }

    @Test
    @DisplayName("EX-02: Gửi DATA chứa ký tự phân tách '|' trong nội dung -> Xử lý an toàn")
    void testDataContentWithPipes() throws IOException {
        String contentWithPipes = "COT1|COT2|COT3|Gia tri chua tu HELLO o day|Ket thuc";
        String message = UdpProtocol.createDataMessage(FileMode.TEXT, "pipes.txt", contentWithPipes);

        String response = sendAndReceive(message);
        assertTrue(response.startsWith("RESULT|OK|Vi tri HELLO:"));

        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(1, historyList.size());
        assertEquals(contentWithPipes, historyList.get(0).getOriginalContent());
    }

    @Test
    @DisplayName("NFR-02: Chặn tên file chứa Path Traversal từ phía Client giả mạo")
    void testPathTraversalPacketRejected() throws IOException {
        String maliciousPacket = "DATA|TEXT|../evil.txt|HELLO";
        String response = sendAndReceive(maliciousPacket);
        assertTrue(response.startsWith("ERROR|"));
    }

    @Test
    @DisplayName("EX-06: Nhiều Client gửi đồng thời cùng một tên file không bị xung đột I/O")
    void testConcurrentSameFileAccess() throws Exception {
        int threads = 6;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        AtomicInteger okCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            pool.submit(() -> {
                try (DatagramSocket s = new DatagramSocket()) {
                    s.setSoTimeout(3000);
                    String msg = UdpProtocol.createDataMessage(FileMode.TEXT, "shared.txt", "Say HELLO concurrently");
                    byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                    s.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName("127.0.0.1"), serverPort));

                    byte[] buf = new byte[4096];
                    DatagramPacket resp = new DatagramPacket(buf, buf.length);
                    s.receive(resp);
                    String respStr = new String(resp.getData(), resp.getOffset(), resp.getLength(), StandardCharsets.UTF_8);
                    if (respStr.equals("RESULT|OK|Vi tri HELLO: [4]")) {
                        okCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        pool.shutdown();
        assertEquals(threads, okCount.get(), "Mọi Client gửi trùng tên file đồng thời phải nhận kết quả chính xác");
        assertEquals(threads, dbHelper.getAllHistory().size());
    }

    @Test
    @DisplayName("Gửi gói tin sai định dạng -> Nhận phản hồi ERROR|<thông_điệp>")
    void testInvalidPacketFormat() throws IOException {
        String response = sendAndReceive("DATA|TEXT_THIEU_THAM_SO");
        assertTrue(response.startsWith("ERROR|"));
    }

    @Test
    @DisplayName("EX-05: Lỗi ghi CSDL không làm sập Server và vẫn trả phản hồi RESULT về Client")
    void testDatabaseFailureGracefulFallback() throws Exception {
        DatabaseHelper failingDb = new DatabaseHelper("jdbc:h2:mem:ex05_test;DB_CLOSE_DELAY=-1", "sa", "") {
            @Override
            public boolean saveHistory(HistoryRecord record) {
                return false;
            }
        };
        java.util.List<String> logs = new java.util.concurrent.CopyOnWriteArrayList<>();
        UdpServerWorker workerWithBrokenDb = new UdpServerWorker(0, "SECRETKEY", tempStorageDir, failingDb, logs::add, null);
        try {
            workerWithBrokenDb.start();
            int port = workerWithBrokenDb.getLocalPort();

            String msg = UdpProtocol.createDataMessage(FileMode.TEXT, "ex05.txt", "Say HELLO when DB fails");
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            clientSocket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName("127.0.0.1"), port));

            byte[] buf = new byte[4096];
            DatagramPacket resp = new DatagramPacket(buf, buf.length);
            clientSocket.receive(resp);
            String respStr = new String(resp.getData(), resp.getOffset(), resp.getLength(), StandardCharsets.UTF_8);

            assertEquals("RESULT|OK|Vi tri HELLO: [4]", respStr);
            assertTrue(logs.stream().anyMatch(l -> l.contains("EX-05")));
        } finally {
            workerWithBrokenDb.stop();
        }
    }
}
