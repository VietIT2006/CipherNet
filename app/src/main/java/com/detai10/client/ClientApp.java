package com.detai10.client;

import com.detai10.client.ui.ConnectFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Điểm khởi chạy chính (Main Entry Point) của ứng dụng Client.
 */
public class ClientApp {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  VIGEN UDP CLIENT SYSTEM - ĐỀ TÀI 10");
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("==================================================");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new ConnectFrame().setVisible(true);
        });
    }
}
