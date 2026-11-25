package com.detai10.core.cipher;

/**
 * Bộ máy mã hóa và giải mã theo thuật toán Vigenère thuộc tầng Core (Headless).
 *
 * <p>Quy ước thuật toán:
 * <ul>
 *   <li>Chỉ biến đổi các chữ cái ASCII tiếng Anh ({@code A-Z}, {@code a-z}) và bảo toàn nguyên trạng
 *       chữ hoa/chữ thường.</li>
 *   <li>Mọi ký tự không phải chữ cái ASCII (chữ số, khoảng trắng, dấu câu, ký tự đặc biệt,
 *       {@code \n}, {@code \t}, ký tự Unicode có dấu) được giữ nguyên và không làm tăng con trỏ khóa.</li>
 *   <li>Khóa được chuẩn hóa bằng cách lọc chỉ giữ lại chữ cái ASCII ({@code A-Z}, {@code a-z}),
 *       không phân biệt hoa/thường khi tính bước dịch {@code 0..25}.</li>
 * </ul>
 */
public final class VigenereCipher {

    /** Khóa bí mật mặc định theo đặc tả đồ án */
    public static final String DEFAULT_KEY = "KEYSECRET";

    /** Số lượng ký tự trong bảng chữ cái tiếng Anh (A-Z). */
    private static final int ALPHABET_SIZE = 26;

    private VigenereCipher() {
        // Lớp tiện ích tĩnh, không cho phép khởi tạo.
    }

    /**
     * Mã hóa văn bản thuần (plaintext) theo công thức Vigenère: {@code C_i = (P_i + K_j) mod 26}.
     *
     * @param plaintext chuỗi văn bản gốc cần mã hóa (không được {@code null}).
     * @param key       khóa mã hóa chứa ít nhất một chữ cái ASCII ({@code A-Z}, {@code a-z}).
     * @return chuỗi bản mã đã bảo toàn chữ hoa/thường và mọi ký tự không phải chữ cái ASCII.
     * @throws IllegalArgumentException nếu {@code plaintext} là {@code null} hoặc {@code key}
     *                                  không hợp lệ (null, rỗng hoặc không có chữ cái ASCII).
     */
    public static String encrypt(String plaintext, String key) {
        return transform(plaintext, key, true);
    }

    /**
     * Giải mã bản mã (ciphertext) theo công thức Vigenère: {@code P_i = (C_i - K_j + 26) mod 26}.
     *
     * @param ciphertext chuỗi bản mã cần giải mã (không được {@code null}).
     * @param key        khóa giải mã chứa ít nhất một chữ cái ASCII ({@code A-Z}, {@code a-z}).
     * @return chuỗi văn bản gốc sau khi giải mã.
     * @throws IllegalArgumentException nếu {@code ciphertext} là {@code null} hoặc {@code key}
     *                                  không hợp lệ (null, rỗng hoặc không có chữ cái ASCII).
     */
    public static String decrypt(String ciphertext, String key) {
        return transform(ciphertext, key, false);
    }

    /**
     * Thực thi vòng lặp biến đổi ký tự chung cho cả mã hóa và giải mã.
     */
    private static String transform(String input, String key, boolean encrypt) {
        int[] keyShifts = extractKeyShifts(key);
        if (input == null) {
            throw new IllegalArgumentException("Văn bản đầu vào không được null.");
        }
        if (input.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder(input.length());
        int keyIndex = 0;

        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (isAsciiUpper(ch)) {
                int shift = keyShifts[keyIndex % keyShifts.length];
                result.append(shiftChar(ch, 'A', shift, encrypt));
                keyIndex++;
            } else if (isAsciiLower(ch)) {
                int shift = keyShifts[keyIndex % keyShifts.length];
                result.append(shiftChar(ch, 'a', shift, encrypt));
                keyIndex++;
            } else {
                // Giữ nguyên ký tự không phải chữ cái ASCII và KHÔNG tăng con trỏ khóa.
                result.append(ch);
            }
        }

        return result.toString();
    }

    /**
     * Dịch một ký tự chữ cái ASCII trong hệ quy chiếu 0..25, bảo toàn chữ hoa/thường theo {@code base}.
     */
    private static char shiftChar(char ch, char base, int shift, boolean encrypt) {
        int value = ch - base;
        int shifted = encrypt
                ? (value + shift) % ALPHABET_SIZE
                // Cộng ALPHABET_SIZE trước khi mod 26 để tránh kết quả âm trong Java.
                : (value - shift + ALPHABET_SIZE) % ALPHABET_SIZE;
        return (char) (base + shifted);
    }

    /**
     * Chuẩn hóa khóa: chỉ giữ các chữ cái ASCII ({@code A-Z}, {@code a-z}) và quy đổi sang bước dịch 0..25.
     * Ném {@link IllegalArgumentException} nếu khóa là {@code null} hoặc không còn chữ cái hợp lệ.
     */
    private static int[] extractKeyShifts(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Khóa Vigenère không được null.");
        }

        int[] buffer = new int[key.length()];
        int count = 0;

        for (int i = 0; i < key.length(); i++) {
            char ch = key.charAt(i);
            if (isAsciiUpper(ch)) {
                buffer[count++] = ch - 'A';
            } else if (isAsciiLower(ch)) {
                buffer[count++] = ch - 'a';
            }
        }

        if (count == 0) {
            throw new IllegalArgumentException(
                    "Khóa Vigenère không hợp lệ: phải chứa ít nhất một chữ cái tiếng Anh (A-Z, a-z).");
        }

        int[] shifts = new int[count];
        System.arraycopy(buffer, 0, shifts, 0, count);
        return shifts;
    }

    private static boolean isAsciiUpper(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }

    private static boolean isAsciiLower(char ch) {
        return ch >= 'a' && ch <= 'z';
    }
}
