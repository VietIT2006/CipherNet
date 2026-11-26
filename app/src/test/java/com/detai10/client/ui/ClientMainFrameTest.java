package com.detai10.client.ui;

import com.detai10.client.net.UdpClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * Kiểm thử đơn vị cho ClientMainFrame (UI-02, UI-03), tuân thủ headless-safe.
 */
class ClientMainFrameTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Sửa nội dung sau khi highlight phải xóa highlight cũ và khóa input khi đang gửi")
    void testClearsStaleHighlightsOnEditAndLocksInputsWhileSending() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử Swing trên môi trường headless");

        final ClientMainFrame[] holder = new ClientMainFrame[1];
        SwingUtilities.invokeAndWait(() -> holder[0] = new ClientMainFrame("127.0.0.1", 9876, new UdpClientService()));
        ClientMainFrame frame = holder[0];
        try {
            JTextArea contentArea = findFirstEditableTextArea(frame.getContentPane());
            JTextField fileNameField = findFirstTextField(frame.getContentPane());
            assertNotNull(contentArea);
            assertNotNull(fileNameField);

            SwingUtilities.invokeAndWait(() -> {
                contentArea.setText("HELLO world");
                frame.highlightHelloKeywords("HELLO world");
                assertEquals(1, contentArea.getHighlighter().getHighlights().length);

                // Khi người dùng chỉnh sửa nội dung, highlight cũ phải bị xóa ngay
                contentArea.setText("HELLO world edited");
                assertEquals(0, contentArea.getHighlighter().getHighlights().length);

                // Khi đang gửi UDP, khóa vùng nhập nội dung & tên file để tránh race highlight
                frame.setSendingState(true);
                assertFalse(contentArea.isEditable());
                assertFalse(fileNameField.isEnabled());

                frame.setSendingState(false);
                assertTrue(contentArea.isEditable());
                assertTrue(fileNameField.isEnabled());
            });
        } finally {
            SwingUtilities.invokeAndWait(frame::dispose);
        }
    }

    @Test
    @DisplayName("Kiểm tra giới hạn kích thước file trước khi nạp lên Swing EDT")
    void testSelectedFileSizeGuard() throws Exception {
        assumeFalse(GraphicsEnvironment.isHeadless(), "Bỏ qua kiểm thử Swing trên môi trường headless");

        File smallFile = tempDir.resolve("small.txt").toFile();
        Files.writeString(smallFile.toPath(), "HELLO");
        File hugeFile = tempDir.resolve("huge.txt").toFile();
        Files.writeString(hugeFile.toPath(), "A".repeat(35000));

        assertTrue(ClientMainFrame.isFileSizeAllowed(smallFile));
        assertFalse(ClientMainFrame.isFileSizeAllowed(hugeFile));
    }

    private static JTextArea findFirstEditableTextArea(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JTextArea ta && ta.isEditable()) {
                return ta;
            }
            if (c instanceof Container child) {
                JTextArea found = findFirstEditableTextArea(child);
                if (found != null) return found;
            }
        }
        return null;
    }

    private static JTextField findFirstTextField(Container root) {
        for (Component c : root.getComponents()) {
            if (c instanceof JTextField tf) {
                return tf;
            }
            if (c instanceof Container child) {
                JTextField found = findFirstTextField(child);
                if (found != null) return found;
            }
        }
        return null;
    }
}
