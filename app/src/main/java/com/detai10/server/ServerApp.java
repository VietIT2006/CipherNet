package com.detai10.server;

import com.detai10.server.ui.ServerMainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Điểm khởi chạy chính (Main Entry Point) của ứng dụng Server.
 */
public class ServerApp {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  VIGEN UDP SERVER SYSTEM - ĐỀ TÀI 10");
        System.out.println("  Java Version: " + System.getProperty("java.version"));
        System.out.println("==================================================");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new ServerMainFrame().setVisible(true);
        });
    }
}
