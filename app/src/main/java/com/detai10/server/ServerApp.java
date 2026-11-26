package com.detai10.server;

import com.detai10.server.ui.ServerMainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Điểm khởi chạy chính (Main Entry Point) của ứng dụng UDP Server (SRV-01).
 */
public class ServerApp {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  VIGEN UDP SERVER SYSTEM - ĐỀ TÀI 10");
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("==================================================");

        // Thiết lập giao diện hệ thống (System Look and Feel)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            ServerMainFrame frame = new ServerMainFrame();
            frame.setVisible(true);
        });
    }
}
