package com.detai10.server.ui;

import com.detai10.core.db.DatabaseHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.InvocationTargetException;

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
    @DisplayName("Khởi tạo ServerMainFrame với kích thước, trạng thái ban đầu và thao tác log/lịch sử chuẩn xác")
    void testInitialStateAndHelpers() throws InterruptedException, InvocationTargetException {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử giao diện Swing trên môi trường headless");

        DatabaseHelper.setInstance(DatabaseHelper.createInMemoryHelper());
        final ServerMainFrame[] frameHolder = new ServerMainFrame[1];
        SwingUtilities.invokeAndWait(() -> frameHolder[0] = new ServerMainFrame());

        ServerMainFrame frame = frameHolder[0];
        try {
            assertNotNull(frame, "ServerMainFrame phải được khởi tạo thành công");
            assertTrue(frame.getTitle().contains("Vigen UDP Server"));
            assertTrue(frame.getWidth() >= 800, "Chiều rộng tối thiểu phải từ 800px");
            assertTrue(frame.getHeight() >= 520, "Chiều cao tối thiểu phải từ 520px");
            assertFalse(frame.isServerRunning(), "Trạng thái ban đầu server phải là OFFLINE");

            SwingUtilities.invokeAndWait(() -> {
                frame.appendLog("Test event log message");
                frame.clearHistoryRows();
            });
        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }
}
