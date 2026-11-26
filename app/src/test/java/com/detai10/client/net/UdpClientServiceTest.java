package com.detai10.client.net;

import com.detai10.core.db.DatabaseHelper;
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
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Kiểm thử tầng mạng UDP phía Client (UdpClientService) kết hợp E2E với UdpServerWorker.
 */
class UdpClientServiceTest {

    @TempDir
    File tempStorageDir;

    private DatabaseHelper dbHelper;
    private UdpServerWorker serverWorker;
    private UdpClientService clientService;
    private int serverPort;

    @BeforeEach
    void setUp() throws Exception {
        dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();
        serverWorker = new UdpServerWorker(0, "KEYSECRET", tempStorageDir, dbHelper, null, null);
        serverWorker.start();
        serverPort = serverWorker.getLocalPort();
        clientService = new UdpClientService();
    }

    @AfterEach
    void tearDown() {
        if (serverWorker != null) {
            serverWorker.stop();
        }
    }

    @Test
    @DisplayName("AC-01: PING tới Server đang chạy trả về true (nhận PONG)")
    void testPingSuccess() throws IOException {
        assertTrue(clientService.ping("127.0.0.1", serverPort));
    }

    @Test
    @DisplayName("AC-01 / EX-01: PING tới cổng không có Server ném SocketTimeoutException sau ~3s")
    void testPingTimeoutWhenServerStopped() throws Exception {
        int unusedPort;
        try (DatagramSocket tempSocket = new DatagramSocket(0)) {
            unusedPort = tempSocket.getLocalPort();
        }
        serverWorker.stop();

        long start = System.currentTimeMillis();
        assertThrows(SocketTimeoutException.class, () -> clientService.ping("127.0.0.1", unusedPort));
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 2500, "Timeout PING phải xấp xỉ 3000ms (thực tế: " + elapsed + "ms)");
    }

    @Test
    @DisplayName("AC-02 / AC-03: Gửi DATA chế độ TEXT và BINARY qua UdpClientService hoạt động E2E")
    void testSendDataTextAndBinaryE2E() throws IOException {
        UdpProtocol.ResponsePayload textResp = clientService.sendData(
                "127.0.0.1", serverPort, FileMode.TEXT, "e2e.txt", "Say HELLO and hello again!"
        );
        assertTrue(textResp.isFound());
        assertEquals("OK", textResp.getStatus());
        assertEquals("Vi tri HELLO: [4, 14]", textResp.getMessage());

        UdpProtocol.ResponsePayload binResp = clientService.sendData(
                "127.0.0.1", serverPort, FileMode.BINARY, "e2e.dat", "No greeting keyword here | pipe safe"
        );
        assertFalse(binResp.isFound());
        assertFalse(binResp.isError());
        assertEquals("NOT_FOUND", binResp.getStatus());
        assertEquals(2, dbHelper.getAllHistory().size());
    }

    @Test
    @DisplayName("Validate đầu vào phía Client trước khi gửi qua UDP (EX-03, NFR-02)")
    void testClientSideValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> clientService.ping("", 9876));
        assertThrows(IllegalArgumentException.class,
                () -> clientService.ping("127.0.0.1", 70000));
        assertThrows(IllegalArgumentException.class,
                () -> clientService.sendData("127.0.0.1", serverPort, FileMode.TEXT, "a.txt", "   "));
        assertThrows(IllegalArgumentException.class,
                () -> clientService.sendData("127.0.0.1", serverPort, FileMode.TEXT, "../bad.txt", "HELLO"));
        assertThrows(IllegalArgumentException.class,
                () -> clientService.sendData("127.0.0.1", serverPort, FileMode.TEXT, "big.txt", "A".repeat(30001)));
    }
}
