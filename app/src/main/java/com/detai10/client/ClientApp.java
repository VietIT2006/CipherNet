package com.detai10.client;

import javax.swing.SwingUtilities;

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
            // Khởi tạo màn hình kết nối ConnectFrame
            System.out.println("[INFO] Khởi tạo luồng giao diện Client...");
        });
    }
}
