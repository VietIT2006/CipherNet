package com.detai10.server.worker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
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
 * Bộ kiểm thử đơn vị toàn diện cho luồng nền lắng nghe UDP (UdpServerWorker - SRV-02).
 */
class UdpServerWorkerTest {

    @Test
    @DisplayName("Nhận diện gói PING và phản hồi PONG tức thì với độ trễ < 50ms (Healthcheck)")
    void testPingPongResponseTimeUnder50ms() throws Exception {
        // Khởi động server trên cổng ngẫu nhiên 0
        try (UdpServerWorker worker = new UdpServerWorker(0)) {
            worker.start();
            assertTrue(worker.isRunning(), "Server phải đang chạy sau khi start()");
            int serverPort = worker.getLocalPort();
            assertTrue(serverPort > 0, "Cổng thực tế phải lớn hơn 0");

            // Client gửi gói tin PING
            try (DatagramSocket clientSocket = new DatagramSocket()) {
                clientSocket.setSoTimeout(3000); // Timeout 3 giây

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
        // Dùng cổng cố định để xác nhận không bị BindException khi stop và start lại
        int testPort = 19876;
        for (int i = 0; i < 5; i++) {
            UdpServerWorker worker = new UdpServerWorker(testPort);
            try {
                worker.start();
                assertTrue(worker.isRunning(), "Lần " + (i + 1) + ": Server phải chạy");
                assertEquals(testPort, worker.getLocalPort());
            } finally {
                worker.stop();
                assertFalse(worker.isRunning(), "Lần " + (i + 1) + ": Server phải dừng");
            }
            // Khoảng nghỉ nhỏ để OS thu hồi port hoàn toàn
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

        // Cho worker thread bước vào vòng lặp receive()
        Thread.sleep(100);

        // Dừng máy chủ
        long startStop = System.currentTimeMillis();
        worker.stop();
        long stopDuration = System.currentTimeMillis() - startStop;

        assertFalse(worker.isRunning(), "Server phải dừng hoàn toàn");
        assertTrue(stopDuration < 1000, "Thời gian dừng phải mượt mà < 1s (Thực tế: " + stopDuration + "ms)");
    }
}
