package com.detai10.server;

import javax.swing.SwingUtilities;

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
            // Khởi tạo giao diện Server khi có ServerMainFrame
            System.out.println("[INFO] Khởi tạo luồng giao diện Server...");
        });
    }
}
