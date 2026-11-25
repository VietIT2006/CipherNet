package com.detai10.server.worker;

import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ kiểm thử đơn vị & tích hợp toàn diện cho UdpServerWorker (SRV-02 & SRV-04).
 */
class UdpServerWorkerTest {

    @Test
    @DisplayName("Nhận diện gói PING và phản hồi PONG tức thì với độ trễ < 50ms (Healthcheck)")
    void testPingPongResponseTimeUnder50ms() throws Exception {
        try (UdpServerWorker worker = new UdpServerWorker(0)) {
            worker.start();
            assertTrue(worker.isRunning(), "Server phải đang chạy sau khi start()");
            int serverPort = worker.getLocalPort();
            assertTrue(serverPort > 0, "Cổng thực tế phải lớn hơn 0");

            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(3000);

                byte[] pingBytes = "PING".getBytes(StandardCharsets.UTF_8);
                InetAddress serverAddr = InetAddress.getByName("127.0.0.1");
                DatagramPacket sendPacket = new DatagramPacket(pingBytes, pingBytes.length, serverAddr, serverPort);

                long startTime = System.currentTimeMillis();
                clientSocket.send(sendPacket);

                byte[] recvBuffer = new byte[1024];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                clientSocket.receive(recvPacket);
                long latency = System.currentTimeMillis() - startTime;

                String response = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);

                assertEquals("PONG", response, "Server phải phản hồi chính xác chuỗi PONG");
                assertTrue(latency < 50, "Thời gian phản hồi PING/PONG phải < 50ms (Thực tế: " + latency + "ms)");
            }
        }
    }

    @Test
    @DisplayName("Nhận diện PING không phân biệt chữ hoa, chữ thường và khoảng trắng")
    void testPingCaseInsensitive() throws Exception {
        try (UdpServerWorker worker = new UdpServerWorker(0)) {
            worker.start();
            int serverPort = worker.getLocalPort();

            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(2000);
                InetAddress serverAddr = InetAddress.getByName("127.0.0.1");

                String[] pingVariants = {"ping", "Ping", "  PiNg  ", "pInG"};
                for (String variant : pingVariants) {
                    byte[] bytes = variant.getBytes(StandardCharsets.UTF_8);
                    clientSocket.send(new DatagramPacket(bytes, bytes.length, serverAddr, serverPort));

                    byte[] recvBuffer = new byte[1024];
                    DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                    clientSocket.receive(recvPacket);

                    String res = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);
                    assertEquals("PONG", res, "Biến thể '" + variant + "' phải được nhận diện là PING và phản hồi PONG");
                }
            }
        }
    }

    @Test
    @DisplayName("Khởi động và dừng Server mượt mà nhiều lần không bị rò rỉ socket/port")
    void testStartAndStopMultipleTimesWithoutLeak() throws Exception {
        int testPort = 19876;
        for (int i = 0; i < 3; i++) {
            UdpServerWorker worker = new UdpServerWorker(testPort);
            try {
                worker.start();
                assertTrue(worker.isRunning(), "Lần " + (i + 1) + ": Server phải chạy");
                assertEquals(testPort, worker.getLocalPort());
            } finally {
                worker.stop();
                assertFalse(worker.isRunning(), "Lần " + (i + 1) + ": Server phải dừng");
            }
            Thread.sleep(50);
        }
    }

    @Test
    @DisplayName("Ghi vết nhật ký sự kiện thông qua LogListener")
    void testLogListenerReceivesMessages() throws Exception {
        List<String> logs = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch logLatch = new CountDownLatch(1);

        try (UdpServerWorker worker = new UdpServerWorker(0, msg -> {
            logs.add(msg);
            if (msg.contains("da khoi dong")) {
                logLatch.countDown();
            }
        })) {
            worker.start();
            assertTrue(logLatch.await(2, TimeUnit.SECONDS), "Phải nhận được log khởi động");
            assertFalse(logs.isEmpty(), "Danh sách log không được rỗng");
            assertTrue(logs.get(0).contains("da khoi dong"));
        }
    }

    @Test
    @DisplayName("Chuyển giao gói tin không phải PING cho PacketHandler tùy biến")
    void testCustomPacketHandler() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean handlerInvoked = new AtomicBoolean(false);

        UdpServerWorker.PacketHandler handler = (data, length, clientAddr, clientPort) -> {
            String content = new String(data, 0, length, StandardCharsets.UTF_8);
            if ("CUSTOM_PAYLOAD".equals(content)) {
                handlerInvoked.set(true);
                latch.countDown();
            }
        };

        try (UdpServerWorker worker = new UdpServerWorker(0, null, handler)) {
            worker.start();
            int serverPort = worker.getLocalPort();

            try (DatagramSocket clientSocket = new DatagramSocket()) {
                byte[] bytes = "CUSTOM_PAYLOAD".getBytes(StandardCharsets.UTF_8);
                clientSocket.send(new DatagramPacket(bytes, bytes.length, InetAddress.getByName("127.0.0.1"), serverPort));

                assertTrue(latch.await(2, TimeUnit.SECONDS), "PacketHandler phải được gọi trong 2s");
                assertTrue(handlerInvoked.get());
            }
        }
    }

    @Test
    @DisplayName("Dừng server trong khi socket đang chờ receive() không bị treo (Graceful shutdown)")
    void testStopWhileBlockedInReceive() throws Exception {
        UdpServerWorker worker = new UdpServerWorker(0);
        worker.start();
        assertTrue(worker.isRunning());

        Thread.sleep(100);

        long startStop = System.currentTimeMillis();
        worker.stop();
        long stopDuration = System.currentTimeMillis() - startStop;

        assertFalse(worker.isRunning(), "Server phải dừng hoàn toàn");
        assertTrue(stopDuration < 1000, "Thời gian dừng phải mượt mà < 1s (Thực tế: " + stopDuration + "ms)");
    }

    @Test
    @DisplayName("Gửi DATA chứa từ khóa HELLO -> Lưu CSDL và nhận RESULT|FOUND|<positions> (SRV-04)")
    void testDataWithHelloAndDbSave(@TempDir File tempDir) throws Exception {
        DatabaseHelper dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();

        try (UdpServerWorker worker = new UdpServerWorker(0, "SECRETKEY", tempDir, dbHelper, null, null)) {
            worker.start();
            int serverPort = worker.getLocalPort();

            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(3000);
                String originalContent = "Xin chao, hello Vietnam! HELLO world.";
                String message = UdpProtocol.createDataMessage(FileMode.TEXT, "test_hello.txt", originalContent);

                byte[] sendBytes = message.getBytes(StandardCharsets.UTF_8);
                clientSocket.send(new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getByName("127.0.0.1"), serverPort));

                byte[] recvBuffer = new byte[4096];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                clientSocket.receive(recvPacket);

                String response = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);
                assertTrue(response.startsWith("RESULT|FOUND|"), "Phản hồi phải là RESULT|FOUND|");
                assertTrue(response.contains("10, 25"), "Phải chứa các vị trí xuất hiện của hello: 10, 25");

                List<HistoryRecord> historyList = dbHelper.getAllHistory();
                assertEquals(1, historyList.size(), "CSDL phải có đúng 1 bản ghi");
                assertEquals("test_hello.txt", historyList.get(0).getFileName());
                assertEquals("TEXT", historyList.get(0).getFileMode());
                assertEquals(originalContent, historyList.get(0).getOriginalContent());
                assertNotNull(historyList.get(0).getEncryptedContent());
                assertEquals("10, 25", historyList.get(0).getHelloPositions());
            }
        }
    }

    @Test
    @DisplayName("Gửi DATA không chứa từ khóa HELLO -> Lưu CSDL và nhận RESULT|NOT_FOUND|... (SRV-04)")
    void testDataWithoutHelloAndDbSave(@TempDir File tempDir) throws Exception {
        DatabaseHelper dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();

        try (UdpServerWorker worker = new UdpServerWorker(0, "SECRETKEY", tempDir, dbHelper, null, null)) {
            worker.start();
            int serverPort = worker.getLocalPort();

            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(3000);
                String originalContent = "Chuc mung mot ngay lam viec tot lanh!";
                String message = UdpProtocol.createDataMessage(FileMode.BINARY, "data_binary.dat", originalContent);

                byte[] sendBytes = message.getBytes(StandardCharsets.UTF_8);
                clientSocket.send(new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getByName("127.0.0.1"), serverPort));

                byte[] recvBuffer = new byte[4096];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                clientSocket.receive(recvPacket);

                String response = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);
                assertEquals(UdpProtocol.createResultNotFoundMessage(), response);

                List<HistoryRecord> historyList = dbHelper.getAllHistory();
                assertEquals(1, historyList.size());
                assertEquals("NOT_FOUND", historyList.get(0).getHelloPositions());
            }
        }
    }

    @Test
    @DisplayName("Gửi DATA chứa ký tự pipe '|' trong nội dung -> Xử lý an toàn không vỡ gói tin (SRV-04)")
    void testDataContentWithPipes(@TempDir File tempDir) throws Exception {
        DatabaseHelper dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();

        try (UdpServerWorker worker = new UdpServerWorker(0, "SECRETKEY", tempDir, dbHelper, null, null)) {
            worker.start();
            int serverPort = worker.getLocalPort();

            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(3000);
                String contentWithPipes = "COT1|COT2|COT3|Gia tri chua tu HELLO o day|Ket thuc";
                String message = UdpProtocol.createDataMessage(FileMode.TEXT, "pipes.txt", contentWithPipes);

                byte[] sendBytes = message.getBytes(StandardCharsets.UTF_8);
                clientSocket.send(new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getByName("127.0.0.1"), serverPort));

                byte[] recvBuffer = new byte[4096];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                clientSocket.receive(recvPacket);

                String response = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);
                assertTrue(response.startsWith("RESULT|FOUND|"));

                List<HistoryRecord> historyList = dbHelper.getAllHistory();
                assertEquals(1, historyList.size());
                assertEquals(contentWithPipes, historyList.get(0).getOriginalContent());
            }
        }
    }

    @Test
    @DisplayName("Gửi gói tin sai định dạng -> Nhận phản hồi ERROR|<thông_điệp>")
    void testInvalidPacketFormat() throws Exception {
        try (UdpServerWorker worker = new UdpServerWorker(0)) {
            worker.start();
            int serverPort = worker.getLocalPort();

            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(3000);
                byte[] sendBytes = "DATA|TEXT_THIEU_THAM_SO".getBytes(StandardCharsets.UTF_8);
                clientSocket.send(new DatagramPacket(sendBytes, sendBytes.length, InetAddress.getByName("127.0.0.1"), serverPort));

                byte[] recvBuffer = new byte[4096];
                DatagramPacket recvPacket = new DatagramPacket(recvBuffer, recvBuffer.length);
                clientSocket.receive(recvPacket);

                String response = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);
                assertTrue(response.startsWith("ERROR|"), "Server phải phản hồi gói ERROR|");
            }
        }
    }
}
