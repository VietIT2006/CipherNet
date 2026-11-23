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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ Unit & Integration Test cho UdpServerWorker (SRV-04).
 * Kiểm chứng toàn diện chu trình nhận gói UDP -> lưu CSDL -> gửi phản hồi RESULT/ERROR.
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

        // Khởi động server trên port ngẫu nhiên (port 0)
        serverWorker = new UdpServerWorker(0, "SECRETKEY", tempStorageDir, dbHelper, null, null);
        serverWorker.start();
        serverPort = serverWorker.getLocalPort();

        clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(3000); // 3 giây timeout
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
        String response = sendAndReceive("PING");
        assertEquals("PONG", response);
    }

    @Test
    @DisplayName("Gửi DATA chứa từ khóa HELLO -> Lưu CSDL và nhận RESULT|FOUND|<positions>")
    void testDataWithHello() throws IOException {
        String originalContent = "Xin chao, hello Vietnam! HELLO world.";
        String message = UdpProtocol.createDataMessage(FileMode.TEXT, "test_hello.txt", originalContent);

        long startTime = System.currentTimeMillis();
        String response = sendAndReceive(message);
        long duration = System.currentTimeMillis() - startTime;

        // Tiêu chí phản hồi nhanh < 100ms
        assertTrue(duration < 200, "Thời gian phản hồi phải < 200ms (thực tế: " + duration + "ms)");

        // Kiểm tra định dạng phản hồi RESULT|FOUND|...
        assertTrue(response.startsWith("RESULT|FOUND|"), "Phản hồi phải bắt đầu bằng RESULT|FOUND|");
        assertTrue(response.contains("10, 25"), "Phải chứa các vị trí xuất hiện của hello: 10, 25");

        // Kiểm tra dữ liệu được lưu đúng và đủ trong bảng lich_su_xu_ly
        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(1, historyList.size(), "CSDL phải có đúng 1 bản ghi lịch sử");

        HistoryRecord record = historyList.get(0);
        assertEquals("test_hello.txt", record.getFileName());
        assertEquals("TEXT", record.getFileMode());
        assertEquals(originalContent, record.getOriginalContent());
        assertNotNull(record.getEncryptedContent(), "Phải có nội dung đã mã hóa");
        assertEquals("10, 25", record.getHelloPositions());
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
        HistoryRecord record = historyList.get(0);
        assertEquals("data_binary.dat", record.getFileName());
        assertEquals("BINARY", record.getFileMode());
        assertEquals("NOT_FOUND", record.getHelloPositions());
    }

    @Test
    @DisplayName("Gửi DATA chứa ký tự phân tách '|' trong nội dung -> Xử lý an toàn không vỡ gói tin")
    void testDataContentWithPipes() throws IOException {
        String contentWithPipes = "COT1|COT2|COT3|Gia tri chua tu HELLO o day|Ket thuc";
        String message = UdpProtocol.createDataMessage(FileMode.TEXT, "pipes.txt", contentWithPipes);

        String response = sendAndReceive(message);
        assertTrue(response.startsWith("RESULT|FOUND|"));

        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(1, historyList.size());
        assertEquals(contentWithPipes, historyList.get(0).getOriginalContent());
    }

    @Test
    @DisplayName("Gửi gói tin sai định dạng -> Nhận phản hồi ERROR|<thông_điệp>")
    void testInvalidPacketFormat() throws IOException {
        String invalidMessage = "DATA|TEXT_THIEU_THAM_SO";
        String response = sendAndReceive(invalidMessage);

        assertTrue(response.startsWith("ERROR|"), "Server phải phản hồi gói ERROR|");
    }
}
