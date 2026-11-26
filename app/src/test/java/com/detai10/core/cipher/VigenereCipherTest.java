package com.detai10.core.cipher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VigenereCipherTest {

    @Test
    @DisplayName("Mã hóa và giải mã chuẩn xác chuỗi tiếng Anh thuần túy")
    void testPureLetters() {
        String plain = "ATTACKATDAWN";
        String key = "LEMON";
        String cipher = VigenereCipher.encrypt(plain, key);
        assertEquals("LXFOPVEFRNHR", cipher);

        String decrypted = VigenereCipher.decrypt(cipher, key);
        assertEquals(plain, decrypted);
    }

    @Test
    @DisplayName("Bảo toàn số, khoảng trắng, dấu câu và không tăng bước con trỏ khóa")
    void testPreserveNonLetters() {
        String plain = "Hello 123! How are you?\nLine 2: HELLO!";
        String key = "KEY";

        String cipher = VigenereCipher.encrypt(plain, key);
        String decrypted = VigenereCipher.decrypt(cipher, key);

        assertEquals(plain, decrypted);
        assertEquals(" 123! ", cipher.substring(5, 11));
        assertEquals("\n", cipher.substring(23, 24));
    }

    @Test
    @DisplayName("Bảo toàn phân biệt chữ hoa và chữ thường")
    void testPreserveCase() {
        String plain = "AbCdEfGh";
        String key = "XYZ";
        String cipher = VigenereCipher.encrypt(plain, key);
        String decrypted = VigenereCipher.decrypt(cipher, key);

        assertEquals(plain, decrypted);
    }

    @Test
    @DisplayName("EX-04: Khóa rỗng, null hoặc chỉ chứa số/ký hiệu ném IllegalArgumentException")
    void testInvalidKeyThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> VigenereCipher.normalizeKey(null));
        assertThrows(IllegalArgumentException.class, () -> VigenereCipher.normalizeKey("   "));
        assertThrows(IllegalArgumentException.class, () -> VigenereCipher.normalizeKey("12345!@#"));
        assertThrows(IllegalArgumentException.class, () -> VigenereCipher.encrypt("HELLO", ""));
        assertThrows(IllegalArgumentException.class, () -> VigenereCipher.decrypt("RIJVS", "999"));
    }

    @Test
    @DisplayName("Chuẩn hóa khóa lọc bỏ số/ký hiệu và chuyển thành chữ hoa")
    void testNormalizeKeyFiltersNonAsciiLetters() {
        assertEquals("SECRET", VigenereCipher.normalizeKey("se-c1r2e3t!"));
    }

    @Nested
    @DisplayName("Nhóm A — Kiểm thử mã hóa")
    class EncryptionTests {

        @Test
        @DisplayName("Mã hóa chuỗi chữ hoa và chữ thường")
        void testEncryptCases() {
            assertEquals("RIJVS", VigenereCipher.encrypt("HELLO", "KEY"));
            assertEquals("rijvs", VigenereCipher.encrypt("hello", "key"));
            assertEquals("Rijvs Uyvjn! 123", VigenereCipher.encrypt("Hello World! 123", "KEY"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi chứa ký tự đặc biệt, pipe, xuống dòng, tab và tiếng Việt")
        void testEncryptSpecialChars() {
            assertEquals("Rijvs|Uyvjn@#%&*()+=[]{}", VigenereCipher.encrypt("Hello|World@#%&*()+=[]{}", "KEY"));
            assertEquals("Rijvs\tUyvjn\nEzm", VigenereCipher.encrypt("Hello\tWorld\nAbc", "KEY"));
            String nonLetters = "12345 !@#$%^&*() \t\n | ăâđêôơưĂÂĐÊÔƠƯáàạảãéèẹẻẽ";
            assertEquals(nonLetters, VigenereCipher.encrypt(nonLetters, "KEY"));
        }
    }

    @Nested
    @DisplayName("Nhóm B — Kiểm thử giải mã")
    class DecryptionTests {

        @Test
        @DisplayName("Giải mã quay vòng đúng khi hiệu C_i - K_j bị âm")
        void testDecryptNegativeModuloWrapAround() {
            assertEquals("A", VigenereCipher.decrypt("Z", "Z"));
            assertEquals("B", VigenereCipher.decrypt("A", "Z"));
            assertEquals("b", VigenereCipher.decrypt("a", "z"));
        }

        @Test
        @DisplayName("Giải mã chuỗi hỗn hợp số, dấu câu, tab và xuống dòng")
        void testDecryptMixed() {
            assertEquals("Hello, 123 World!", VigenereCipher.decrypt("Rijvs, 123 Uyvjn!", "KEY"));
            assertEquals("Hello\tWorld\n|2026!", VigenereCipher.decrypt("Rijvs\tUyvjn\n|2026!", "KEY"));
        }
    }

    @Nested
    @DisplayName("Nhóm C — Kiểm thử bất biến con trỏ khóa & biên")
    class KeyPointerAndEdgeTests {

        @Test
        @DisplayName("Khoảng trắng, dấu câu, chữ số và xuống dòng không làm tăng con trỏ khóa")
        void testNonLettersDoNotAdvanceKeyPointer() {
            assertEquals("KFA", VigenereCipher.encrypt("ABC", "KEY"));
            assertEquals("K F-A!", VigenereCipher.encrypt("A B-C!", "KEY"));
            assertEquals("A B-C!", VigenereCipher.decrypt("K F-A!", "KEY"));

            String input = "A B-C! D1E\nF\tG|H";
            String expected = "K F-A! N1I\nD\tQ|L";
            assertEquals(expected, VigenereCipher.encrypt(input, "KEY"));
            assertEquals(input, VigenereCipher.decrypt(expected, "KEY"));

            String encrypted = VigenereCipher.encrypt("A   B", "KEY");
            assertEquals("K   F", encrypted);
            assertEquals("A   B", VigenereCipher.decrypt(encrypted, "KEY"));
        }

        @Test
        @DisplayName("Round-trip decrypt(encrypt(text)) và đầu vào null")
        void testRoundTripAndNullInput() {
            String key = "CipherNet2026!";
            String[] samples = {
                    "",
                    "ALLUPPERCASE",
                    "alllowercase",
                    "MixedCaseExample",
                    "HELLO World! 12345",
                    "DATA|BINARY|demo.bin|Say HELLO to UDP!",
                    "Line 1: HELLO\nLine 2:\t@#$%^&*()\nLine 3: 0123456789",
                    "Xin chào HELLO thế giới 2026 — Đề tài 10!"
            };
            for (String sample : samples) {
                assertEquals(sample, VigenereCipher.decrypt(VigenereCipher.encrypt(sample, key), key));
            }

            assertThrows(IllegalArgumentException.class, () -> VigenereCipher.encrypt(null, "KEY"));
            assertThrows(IllegalArgumentException.class, () -> VigenereCipher.decrypt(null, "KEY"));
        }
    }
}
