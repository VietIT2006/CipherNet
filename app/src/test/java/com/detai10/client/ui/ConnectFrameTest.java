package com.detai10.client.ui;

import com.detai10.client.net.UdpClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.SwingUtilities;
import java.awt.GraphicsEnvironment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Kiểm thử đơn vị cho màn hình kết nối ConnectFrame (UI-01), tuân thủ headless-safe.
 */
class ConnectFrameTest {

    @Test
    @DisplayName("UI-01: Khởi tạo ConnectFrame với kích thước 420x260, không resize và nút mặc định")
    void testInitialState() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử Swing trên môi trường headless");

        final ConnectFrame[] holder = new ConnectFrame[1];
        SwingUtilities.invokeAndWait(() -> holder[0] = new ConnectFrame(new UdpClientService()));
        ConnectFrame frame = holder[0];
        try {
            assertTrue(frame.getTitle().contains("Vigen UDP Client"));
            assertEquals(420, frame.getWidth());
            assertEquals(260, frame.getHeight());
            assertFalse(frame.isResizable());
            assertNotNull(frame.getRootPane().getDefaultButton());
        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }
}
