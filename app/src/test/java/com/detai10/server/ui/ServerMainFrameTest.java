package com.detai10.server.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Bộ kiểm thử đơn vị cho giao diện Server (ServerMainFrame - SRV-01).
 * Tuân thủ headless-safe trên Linux CI.
 */
class ServerMainFrameTest {

    @Test
    @DisplayName("Khởi tạo ServerMainFrame với kích thước và trạng thái ban đầu chuẩn xác")
    void testInitialState() throws InterruptedException, InvocationTargetException {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử giao diện Swing trên môi trường headless");

        final ServerMainFrame[] frameHolder = new ServerMainFrame[1];
        SwingUtilities.invokeAndWait(() -> frameHolder[0] = new ServerMainFrame());

        ServerMainFrame frame = frameHolder[0];
        try {
            assertNotNull(frame, "ServerMainFrame phải được khởi tạo thành công");
            assertTrue(frame.getTitle().contains("Vigen UDP Server"));
            assertTrue(frame.getWidth() >= 800, "Chiều rộng tối thiểu phải từ 800px");
            assertTrue(frame.getHeight() >= 580, "Chiều cao tối thiểu phải từ 580px");
            assertFalse(frame.isServerRunning(), "Trạng thái ban đầu server phải là STOPPED");

            // Kiểm tra ghi nhật ký Console
            SwingUtilities.invokeAndWait(() -> frame.appendLog("Test event log message"));

            // Kiểm tra thêm dòng vào bảng Lịch sử
            SwingUtilities.invokeAndWait(() -> frame.addHistoryRow(
                    "127.0.0.1",
                    "sample.txt",
                    "TEXT",
                    "Hello World",
                    "Rovvy Gybvn",
                    "0"
            ));

            // Kiểm tra Start và Stop qua giao diện
            SwingUtilities.invokeAndWait(frame::startServer);
            SwingUtilities.invokeAndWait(frame::stopServer);

            // Kiểm tra xóa lịch sử
            SwingUtilities.invokeAndWait(frame::clearHistoryRows);

        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }

    @Test
    @DisplayName("Kiểm tra component đồ họa StatusBadge hoạt động đúng trạng thái")
    void testStatusBadge() {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử giao diện Swing trên môi trường headless");

        ServerMainFrame.StatusBadge badge = new ServerMainFrame.StatusBadge(false, 0);
        assertNotNull(badge);
        assertFalse(badge.isOnline());

        Dimension dim = badge.getPreferredSize();
        assertTrue(dim.width >= 140);
        assertTrue(dim.height >= 24);

        badge.setStatus(true, 9876);
        assertTrue(badge.isOnline());

        badge.setStatus(false, 0);
        assertFalse(badge.isOnline());
    }
}
