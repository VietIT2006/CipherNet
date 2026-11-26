package com.detai10.server;

import com.detai10.core.db.DatabaseHelper;
import com.detai10.core.db.HistoryRecord;
import com.detai10.core.model.FileMode;
import com.detai10.core.protocol.UdpProtocol;
import com.detai10.server.worker.UdpServerWorker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kiểm thử chịu tải nhẹ (Lightweight Stress / Load Test) cho Server UDP (VER-04).
 */
class UdpServerStressTest {

    private static final int TOTAL_PACKETS = 100;
    private static final int CONCURRENT_CLIENTS = 10;

    @TempDir
    File tempStorageDir;

    private DatabaseHelper dbHelper;
    private UdpServerWorker serverWorker;
    private int serverPort;

    @BeforeEach
    void setUp() throws Exception {
        dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();

        serverWorker = new UdpServerWorker(0, "VIGENSECRETKEY", tempStorageDir, dbHelper, null, null);
        serverWorker.start();
        serverPort = serverWorker.getLocalPort();
    }

    @AfterEach
    void tearDown() {
        if (serverWorker != null) {
            serverWorker.stop();
        }
    }

    @Test
    @DisplayName("Kiểm thử tải nhẹ: 100 gói UDP đồng thời từ 10 luồng Client - toàn vẹn CSDL và độ trễ thấp")
    void test100PacketsConcurrentLoad() throws Exception {
        ExecutorService clientPool = Executors.newFixedThreadPool(CONCURRENT_CLIENTS);
        CountDownLatch latch = new CountDownLatch(TOTAL_PACKETS);
        AtomicInteger successResponses = new AtomicInteger(0);
        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());

        long testStartTime = System.currentTimeMillis();

        for (int i = 1; i <= TOTAL_PACKETS; i++) {
            final int packetIndex = i;
            clientPool.submit(() -> {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setSoTimeout(3000);
                    InetAddress serverAddr = InetAddress.getByName("127.0.0.1");

                    boolean containsHello = (packetIndex % 2 == 0);
                    String content = containsHello
                            ? "Hello world from packet #" + packetIndex + " saying hello!"
                            : "Regular data packet #" + packetIndex + " with no keyword.";

                    FileMode mode = (packetIndex % 3 == 0) ? FileMode.BINARY : FileMode.TEXT;
                    String fileName = "stress_test_" + packetIndex + (mode == FileMode.BINARY ? ".bin" : ".txt");
                    String payload = UdpProtocol.createDataMessage(mode, fileName, content);

                    byte[] sendBytes = payload.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket sendPacket = new DatagramPacket(sendBytes, sendBytes.length, serverAddr, serverPort);

                    long packetStart = System.currentTimeMillis();
                    socket.send(sendPacket);

                    byte[] recvBytes = new byte[4096];
                    DatagramPacket recvPacket = new DatagramPacket(recvBytes, recvBytes.length);
                    socket.receive(recvPacket);

                    long latency = System.currentTimeMillis() - packetStart;
                    latencies.add(latency);

                    String response = new String(recvPacket.getData(), recvPacket.getOffset(), recvPacket.getLength(), StandardCharsets.UTF_8);
                    if (response.startsWith(UdpProtocol.PREFIX_RESULT + "|")) {
                        successResponses.incrementAndGet();
                    }
                } catch (Exception e) {
                    System.err.println("[STRESS_ERR] Loi gui nhan goi #" + packetIndex + ": " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        boolean completedInTime = latch.await(15, TimeUnit.SECONDS);
        long totalTestTime = System.currentTimeMillis() - testStartTime;
        clientPool.shutdown();

        assertTrue(completedInTime, "Toàn bộ 100 gói tin phải xử lý xong trong 15 giây");
        assertEquals(TOTAL_PACKETS, successResponses.get(), "Tất cả 100 gói tin phải nhận được phản hồi RESULT");

        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(TOTAL_PACKETS, historyList.size(), "Bảng lich_su_xu_ly phải lưu đủ 100 bản ghi");

        long minLatency = latencies.stream().mapToLong(Long::longValue).min().orElse(0);
        long maxLatency = latencies.stream().mapToLong(Long::longValue).max().orElse(0);
        double avgLatency = latencies.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double throughput = (TOTAL_PACKETS * 1000.0) / totalTestTime;

        System.out.println("==================================================");
        System.out.println("  KET QUA KIEM THU TAI NHE UDP SERVER (VER-04)   ");
        System.out.println("==================================================");
        System.out.println("  Tong so goi tin gui        : " + TOTAL_PACKETS);
        System.out.println("  Phan hoi thanh cong        : " + successResponses.get() + " / " + TOTAL_PACKETS);
        System.out.println("  So ban ghi CSDL da luu     : " + historyList.size() + " / " + TOTAL_PACKETS);
        System.out.println("  Tong thoi gian chay        : " + totalTestTime + " ms");
        System.out.println("  Thoi gian phan hoi nho nhat: " + minLatency + " ms");
        System.out.println("  Thoi gian phan hoi lon nhat: " + maxLatency + " ms");
        System.out.printf("  Thoi gian phan hoi trung binh: %.2f ms (Tieu chuan: < 100ms)\n", avgLatency);
        System.out.printf("  Thong luong (Throughput)   : %.2f req/s\n", throughput);
        System.out.println("==================================================");

        assertTrue(avgLatency < 100.0, "Độ trễ trung bình (" + avgLatency + "ms) phải nhỏ hơn 100ms");
    }
}
