package com.detai10.core.cipher;

import java.util.Locale;

/**
 * Thuật toán mã hóa & giải mã đa bảng thay thế Vigenère.
 * <p>
 * Tuân thủ nghiêm ngặt Quy tắc 4 (Quy chuẩn dự án):
 * - Chỉ dịch chuyển chữ cái tiếng Anh (A-Z, a-z).
 * - Bảo toàn nguyên vẹn số (0-9), khoảng trắng, dấu câu, ký hiệu đặc biệt, ký tự xuống dòng.
 * - Con trỏ khóa (key index) KHÔNG được tăng bước khi gặp ký tự không phải chữ cái.
 * - Khóa tự động chuẩn hóa chỉ giữ lại các chữ cái và chuyển thành chữ hoa.
 */
public final class VigenereCipher {

    public static final String DEFAULT_KEY = "KEYSECRET";

    private VigenereCipher() {
        throw new UnsupportedOperationException("Lớp tiện ích mã hóa không thể khởi tạo thể hiện.");
    }

    /**
     * Chuẩn hóa khóa: chỉ giữ lại chữ cái và chuyển về chữ hoa.
     */
    public static String normalizeKey(String key) {
        if (key == null) {
            return DEFAULT_KEY;
        }
        StringBuilder sb = new StringBuilder();
        for (char c : key.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.length() > 0 ? sb.toString() : DEFAULT_KEY;
    }

    /**
     * Mã hóa chuỗi văn bản bằng thuật toán Vigenère với khóa chỉ định.
     */
    public static String encrypt(String plainText, String key) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }

        String validKey = normalizeKey(key);
        StringBuilder cipherText = new StringBuilder(plainText.length());
        int keyIndex = 0;
        int keyLength = validKey.length();

        for (int i = 0; i < plainText.length(); i++) {
            char c = plainText.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                int shift = validKey.charAt(keyIndex % keyLength) - 'A';
                char encrypted = (char) ('A' + (c - 'A' + shift) % 26);
                cipherText.append(encrypted);
                keyIndex++;
            } else if (c >= 'a' && c <= 'z') {
                int shift = validKey.charAt(keyIndex % keyLength) - 'A';
                char encrypted = (char) ('a' + (c - 'a' + shift) % 26);
                cipherText.append(encrypted);
                keyIndex++;
            } else {
                // Ký tự số, khoảng trắng, ký hiệu đặc biệt -> Giữ nguyên, không tăng keyIndex
                cipherText.append(c);
            }
        }

        return cipherText.toString();
    }

    /**
     * Giải mã chuỗi văn bản đã mã hóa Vigenère về văn bản gốc ban đầu.
     */
    public static String decrypt(String cipherText, String key) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }

        String validKey = normalizeKey(key);
        StringBuilder plainText = new StringBuilder(cipherText.length());
        int keyIndex = 0;
        int keyLength = validKey.length();

        for (int i = 0; i < cipherText.length(); i++) {
            char c = cipherText.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                int shift = validKey.charAt(keyIndex % keyLength) - 'A';
                char decrypted = (char) ('A' + (c - 'A' - shift + 26) % 26);
                plainText.append(decrypted);
                keyIndex++;
            } else if (c >= 'a' && c <= 'z') {
                int shift = validKey.charAt(keyIndex % keyLength) - 'A';
                char decrypted = (char) ('a' + (c - 'a' - shift + 26) % 26);
                plainText.append(decrypted);
                keyIndex++;
            } else {
                plainText.append(c);
            }
        }

        return plainText.toString();
    }
}
