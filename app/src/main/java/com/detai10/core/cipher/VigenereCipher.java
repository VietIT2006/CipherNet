package com.detai10.core.cipher;

/**
 * Thuật toán mã hóa và giải mã đa bảng thay thế Vigenère.
 * <p>
 * Tuân thủ nghiêm ngặt Quy tắc 4 & SRS FR-02 / EX-04:
 * - Chỉ dịch chuyển chữ cái tiếng Anh (A-Z, a-z) theo modulo 26.
 * - Bảo toàn nguyên vẹn số (0-9), khoảng trắng, dấu câu, ký hiệu đặc biệt, ký tự xuống dòng.
 * - Con trỏ khóa (keyIndex) KHÔNG được tăng bước khi gặp ký tự không phải chữ cái.
 * - Khóa rỗng hoặc không chứa chữ cái tiếng Anh sẽ ném {@link IllegalArgumentException}.
 */
public final class VigenereCipher {

    public static final String DEFAULT_KEY = "KEYSECRET";

    private VigenereCipher() {
        throw new UnsupportedOperationException("Lớp tiện ích mã hóa không thể khởi tạo thể hiện.");
    }

    /**
     * Chuẩn hóa khóa: chỉ giữ lại chữ cái tiếng Anh [A-Za-z] và chuyển về chữ hoa.
     *
     * @param key Chuỗi khóa đầu vào
     * @return Chuỗi khóa đã chuẩn hóa (chỉ gồm chữ hoa A-Z)
     * @throws IllegalArgumentException nếu khóa null, rỗng hoặc không chứa chữ cái nào
     */
    public static String normalizeKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Khóa Vigenère không được để trống!");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                sb.append(Character.toUpperCase(c));
            }
        }
        if (sb.length() == 0) {
            throw new IllegalArgumentException("Khóa Vigenère phải chứa ít nhất một chữ cái tiếng Anh (A-Z)!");
        }
        return sb.toString();
    }

    /**
     * Mã hóa chuỗi văn bản bằng thuật toán Vigenère với khóa chỉ định.
     */
    public static String encrypt(String plainText, String key) {
        return transform(plainText, key, true);
    }

    /**
     * Giải mã chuỗi văn bản đã mã hóa Vigenère về văn bản gốc ban đầu.
     */
    public static String decrypt(String cipherText, String key) {
        return transform(cipherText, key, false);
    }

    private static String transform(String text, String key, boolean encryptMode) {
        String validKey = normalizeKey(key);
        if (text == null || text.isEmpty()) {
            return text;
        }

        StringBuilder result = new StringBuilder(text.length());
        int keyIndex = 0;
        int keyLength = validKey.length();

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                result.append(shiftChar(c, 'A', validKey.charAt(keyIndex % keyLength) - 'A', encryptMode));
                keyIndex++;
            } else if (c >= 'a' && c <= 'z') {
                result.append(shiftChar(c, 'a', validKey.charAt(keyIndex % keyLength) - 'A', encryptMode));
                keyIndex++;
            } else {
                // Giữ nguyên ký tự số, khoảng trắng, dấu câu, tiếng Việt có dấu và không tăng keyIndex
                result.append(c);
            }
        }

        return result.toString();
    }

    private static char shiftChar(char c, char base, int shift, boolean encryptMode) {
        int offset = c - base;
        int shifted = encryptMode
                ? (offset + shift) % 26
                : (offset - shift + 26) % 26;
        return (char) (base + shifted);
    }
}
