package com.detai10.core.model;

/**
 * Định nghĩa 2 chế độ lưu trữ và đọc/ghi file trên hệ thống:
 * - TEXT: Văn bản thuần túy chuẩn UTF-8 (BufferedWriter / BufferedReader).
 * - BINARY: Nhị phân có đệm định kiểu (DataOutputStream / DataInputStream với writeUTF/readUTF).
 */
public enum FileMode {
    TEXT,
    BINARY;

    /**
     * Chuyển đổi an toàn từ chuỗi sang enum FileMode.
     * Mặc định trả về TEXT nếu chuỗi không hợp lệ hoặc rỗng.
     */
    public static FileMode fromString(String modeStr) {
        if (modeStr == null) return TEXT;
        try {
            return FileMode.valueOf(modeStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return TEXT;
        }
    }
}
