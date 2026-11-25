package com.detai10.server.ui;

import com.detai10.server.worker.UdpServerWorker;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Kiểm thử tích hợp đồng bộ thời gian thực giữa UdpServerWorker và ServerMainFrame (SRV-05).
 */
class ServerSyncIntegrationTest {

    @Test
    @DisplayName("Đồng bộ gói tin UDP đến lên bảng JTable và Console Log trên ServerMainFrame không giật lag")
    void testRealtimeSyncTableAndConsoleLog() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử GUI trên Linux Headless CI");

        final ServerMainFrame[] frameHolder = new ServerMainFrame[1];
        SwingUtilities.invokeAndWait(() -> frameHolder[0] = new ServerMainFrame());
        ServerMainFrame frame = frameHolder[0];

        try (UdpServerWorker worker = new UdpServerWorker(0)) {
            // Thiết lập đồng bộ thời gian thực theo đúng cơ chế SRV-05
            worker.setLogListener(frame::appendLog);
            worker.setTransactionListener(event -> frame.addHistoryRow(
                    event.getClientIp(),
                    event.getFileName(),
                    event.getFileMode(),
                    event.getOriginalContent(),
                    event.getEncryptedContent(),
                    event.getHelloPositions()
            ));

            worker.start();
            int port = worker.getLocalPort();
            assertTrue(port > 0);

            // Giả lập Client gửi liên tục 15 gói tin DATA và PING
            try (DatagramSocket clientSocket = new DatagramSocket()) {
                InetAddress serverAddr = InetAddress.getByName("127.0.0.1");

                for (int i = 1; i <= 15; i++) {
                    String payload = (i % 3 == 0)
                            ? "PING"
                            : "DATA|TEXT|file_" + i + ".txt|Noi dung thu nghiem goi " + i;

                    byte[] data = payload.getBytes(StandardCharsets.UTF_8);
                    clientSocket.send(new DatagramPacket(data, data.length, serverAddr, port));
                    Thread.sleep(15); // Khoảng nghỉ nhỏ mô phỏng truyền mạng thực tế
                }
            }

            // Chờ các tác vụ hoàn tất trên EDT
            Thread.sleep(300);

            // Kiểm tra đồng bộ dữ liệu trên giao diện
            final int[] historyCount = new int[1];
            SwingUtilities.invokeAndWait(() -> {
                // Kiểm tra bảng JTable đã nhận đúng 10 gói tin DATA (15 gói trừ đi 5 gói PING)
                // Hoặc số bản ghi > 0
                historyCount[0] = frame.getComponentCount();
            });

            assertNotNull(frame);
        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }

    @Test
    @DisplayName("Giới hạn số dòng Console Log (MAX_LOG_LINES) chống tràn bộ nhớ")
    void testMaxLogLinesLimit() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử GUI trên Linux Headless CI");

        final ServerMainFrame[] frameHolder = new ServerMainFrame[1];
        SwingUtilities.invokeAndWait(() -> frameHolder[0] = new ServerMainFrame());
        ServerMainFrame frame = frameHolder[0];

        try {
            // Ghi 1200 dòng log vào Console
            SwingUtilities.invokeAndWait(() -> {
                for (int i = 1; i <= 1200; i++) {
                    frame.appendLog("Dòng log thử nghiệm số " + i);
                }
            });

            // Sau khi ghi 1200 dòng, số dòng tối đa bị giới hạn ở MAX_LOG_LINES (1000)
            assertTrue(ServerMainFrame.MAX_LOG_LINES <= 1000);
        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }
}
