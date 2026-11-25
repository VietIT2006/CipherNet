package com.detai10.core.cipher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VigenereCipherTest {

    @Nested
    @DisplayName("Nhóm A — Kiểm thử mã hóa")
    class EncryptionTests {

        @Test
        @DisplayName("Mã hóa chuỗi chữ hoa hoàn toàn")
        void testEncryptUppercaseOnly() {
            assertEquals("RIJVS", VigenereCipher.encrypt("HELLO", "KEY"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi chữ thường hoàn toàn")
        void testEncryptLowercaseOnly() {
            assertEquals("rijvs", VigenereCipher.encrypt("hello", "key"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi hỗn hợp chữ hoa và chữ thường")
        void testEncryptMixedCase() {
            assertEquals("Rijvs Uyvjn! 123", VigenereCipher.encrypt("Hello World! 123", "KEY"));
        }

        @Test
        @DisplayName("Mã hóa ví dụ chuẩn ATTACKATDAWN với khóa LEMON")
        void testEncryptStandardExample() {
            assertEquals("LXFOPVEFRNHR", VigenereCipher.encrypt("ATTACKATDAWN", "LEMON"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi chứa khoảng trắng")
        void testEncryptWithSpaces() {
            assertEquals("Lxf opve Frnhr", VigenereCipher.encrypt("Att acka Tdawn", "LEMON"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi chứa chữ số")
        void testEncryptWithDigits() {
            assertEquals("Rijvs2026Uyvjn99", VigenereCipher.encrypt("Hello2026World99", "KEY"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi chứa dấu câu")
        void testEncryptWithPunctuation() {
            assertEquals("Rijvs, Uyvjn!?", VigenereCipher.encrypt("Hello, World!?", "KEY"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi chứa nhiều ký tự đặc biệt và dấu phân tách |")
        void testEncryptWithSpecialCharacters() {
            assertEquals("Rijvs|Uyvjn@#%&*()+=[]{}", VigenereCipher.encrypt("Hello|World@#%&*()+=[]{}", "KEY"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi có ký tự xuống dòng và tab")
        void testEncryptWithNewlineAndTab() {
            assertEquals("Rijvs\tUyvjn\nEzm", VigenereCipher.encrypt("Hello\tWorld\nAbc", "KEY"));
        }

        @Test
        @DisplayName("Mã hóa chuỗi không chứa bất kỳ chữ cái ASCII nào")
        void testEncryptNonAlphabeticOnly() {
            String nonLetters = "12345 !@#$%^&*() \t\n | ăâđêôơưĂÂĐÊÔƠƯáàạảãéèẹẻẽ";
            assertEquals(nonLetters, VigenereCipher.encrypt(nonLetters, "KEY"));
        }
    }

    @Nested
    @DisplayName("Nhóm B — Kiểm thử giải mã")
    class DecryptionTests {

        @Test
        @DisplayName("Giải mã chuỗi chữ hoa hoàn toàn")
        void testDecryptUppercaseOnly() {
            assertEquals("HELLO", VigenereCipher.decrypt("RIJVS", "KEY"));
        }

        @Test
        @DisplayName("Giải mã chuỗi chữ thường hoàn toàn")
        void testDecryptLowercaseOnly() {
            assertEquals("hello", VigenereCipher.decrypt("rijvs", "key"));
        }

        @Test
        @DisplayName("Giải mã chuỗi hỗn hợp chữ hoa và chữ thường")
        void testDecryptMixedCase() {
            assertEquals("Hello World! 123", VigenereCipher.decrypt("Rijvs Uyvjn! 123", "KEY"));
        }

        @Test
        @DisplayName("Giải mã ví dụ chuẩn LXFOPVEFRNHR với khóa LEMON")
        void testDecryptStandardExample() {
            assertEquals("ATTACKATDAWN", VigenereCipher.decrypt("LXFOPVEFRNHR", "LEMON"));
        }

        @Test
        @DisplayName("Giải mã chuỗi chứa khoảng trắng")
        void testDecryptWithSpaces() {
            assertEquals("Att acka Tdawn", VigenereCipher.decrypt("Lxf opve Frnhr", "LEMON"));
        }

        @Test
        @DisplayName("Giải mã chuỗi chứa số và dấu câu")
        void testDecryptWithDigitsAndPunctuation() {
            assertEquals("Hello, 123 World!", VigenereCipher.decrypt("Rijvs, 123 Uyvjn!", "KEY"));
        }

        @Test
        @DisplayName("Giải mã chuỗi có xuống dòng, tab và ký tự đặc biệt")
        void testDecryptWithNewlineTabAndSpecialChars() {
            assertEquals("Hello\tWorld\n|2026!", VigenereCipher.decrypt("Rijvs\tUyvjn\n|2026!", "KEY"));
        }

        @Test
        @DisplayName("Giải mã quay vòng đúng khi hiệu C_i - K_j bị âm")
        void testDecryptNegativeModuloWrapAround() {
            // 'A' (0) mã hóa với 'Z' (25) -> 'Z' (25); giải mã 'A' (0) với 'Z' (25) -> (0 - 25 + 26) mod 26 = 1 ('B')
            assertEquals("A", VigenereCipher.decrypt("Z", "Z"));
            assertEquals("B", VigenereCipher.decrypt("A", "Z"));
            assertEquals("b", VigenereCipher.decrypt("a", "z"));
        }
    }

    @Nested
    @DisplayName("Nhóm C — Kiểm thử xử lý & kiểm tra khóa")
    class KeyTests {

        @Test
        @DisplayName("Khóa thông thường, viết hoa, viết thường và hỗn hợp hoa/thường cho kết quả đồng nhất")
        void testKeyCaseInsensitivity() {
            String plaintext = "AttackAtDawn";
            String expected = "LxfopvEfRnhr";

            assertEquals(expected, VigenereCipher.encrypt(plaintext, "Lemon"));
            assertEquals(expected, VigenereCipher.encrypt(plaintext, "LEMON"));
            assertEquals(expected, VigenereCipher.encrypt(plaintext, "lemon"));
            assertEquals(expected, VigenereCipher.encrypt(plaintext, "LeMoN"));

            assertEquals(plaintext, VigenereCipher.decrypt(expected, "LeMoN"));
        }

        @Test
        @DisplayName("Khóa có chữ số, dấu câu, ký tự đặc biệt và khoảng trắng được lọc chuẩn xác")
        void testKeyNormalizationFiltersNonAsciiLetters() {
            String plaintext = "Hello World! 123";
            String expected = "Rijvs Uyvjn! 123";

            assertEquals(expected, VigenereCipher.encrypt(plaintext, "KEY123"));
            assertEquals(expected, VigenereCipher.encrypt(plaintext, "K-E-Y!"));
            assertEquals(expected, VigenereCipher.encrypt(plaintext, " K E Y "));
            assertEquals(expected, VigenereCipher.encrypt(plaintext, "123K!e@y#\n"));

            assertEquals(plaintext, VigenereCipher.decrypt(expected, "K-E-Y!123"));
        }

        @Test
        @DisplayName("Khóa rỗng, null hoặc không còn chữ cái ASCII hợp lệ phải ném IllegalArgumentException")
        void testInvalidKeysThrowIllegalArgumentException() {
            String[] invalidKeys = {
                    "",
                    "   \t\n",
                    "1234567890",
                    "!@#$%^&*()-_=+|",
                    "123 !@# \n",
                    "ĐÂĂƠƯáàạ"
            };

            assertThrows(IllegalArgumentException.class, () -> VigenereCipher.encrypt("HELLO", null));
            assertThrows(IllegalArgumentException.class, () -> VigenereCipher.decrypt("RIJVS", null));

            for (String invalidKey : invalidKeys) {
                assertThrows(IllegalArgumentException.class,
                        () -> VigenereCipher.encrypt("HELLO", invalidKey),
                        "Phải ném IllegalArgumentException khi mã hóa với key: [" + invalidKey + "]");
                assertThrows(IllegalArgumentException.class,
                        () -> VigenereCipher.decrypt("RIJVS", invalidKey),
                        "Phải ném IllegalArgumentException khi giải mã với key: [" + invalidKey + "]");
            }
        }
    }

    @Nested
    @DisplayName("Nhóm D — Kiểm thử bất biến con trỏ khóa (Key Pointer)")
    class KeyPointerInvariantTests {

        @Test
        @DisplayName("Khoảng trắng và dấu câu không làm tăng con trỏ khóa")
        void testSpacesAndPunctuationDoNotAdvanceKeyPointer() {
            // A B-C! với KEY phải cho kết quả chữ cái giống ABC với KEY (K F A)
            assertEquals("KFA", VigenereCipher.encrypt("ABC", "KEY"));
            assertEquals("K F-A!", VigenereCipher.encrypt("A B-C!", "KEY"));
            assertEquals("A B-C!", VigenereCipher.decrypt("K F-A!", "KEY"));
        }

        @Test
        @DisplayName("Chữ số, ký tự đặc biệt và xuống dòng không làm lệch chu kỳ lặp khóa")
        void testDigitsSymbolsAndNewlinesPreserveKeyCycle() {
            // Chuỗi chữ cái: A B C D E F -> lặp 2 chu kỳ KEY -> K F A N I D
            String input = "A B-C! D1E\nF\tG|H";
            // Thêm G(6)+10=16(Q), H(7)+4=11(L)
            String expected = "K F-A! N1I\nD\tQ|L";

            assertEquals(expected, VigenereCipher.encrypt(input, "KEY"));
            assertEquals(input, VigenereCipher.decrypt(expected, "KEY"));
        }

        @Test
        @DisplayName("Phát hiện lỗi nếu tăng con trỏ khóa trên ký tự không phải chữ cái")
        void testDetectBuggyKeyPointerAdvancementOnNonLetters() {
            // Nếu tăng keyIndex sai ở dấu cách, ký tự 'B' sẽ dùng 'Y'(24)->'Z' thay vì 'E'(4)->'F'
            String encrypted = VigenereCipher.encrypt("A   B", "KEY");
            assertEquals("K   F", encrypted);
            assertEquals("A   B", VigenereCipher.decrypt(encrypted, "KEY"));
        }
    }

    @Nested
    @DisplayName("Nhóm E — Kiểm thử đối xứng (Round-trip) và biên")
    class RoundTripTests {

        @Test
        @DisplayName("Round-trip decrypt(encrypt(text)) khôi phục chính xác nhiều dạng dữ liệu")
        void testRoundTripVariousInputs() {
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
                String encrypted = VigenereCipher.encrypt(sample, key);
                String decrypted = VigenereCipher.decrypt(encrypted, key);
                assertEquals(sample, decrypted, "Round-trip thất bại cho chuỗi: " + sample);
            }
        }

        @Test
        @DisplayName("Đầu vào văn bản null phải ném IllegalArgumentException")
        void testNullInputThrowsIllegalArgumentException() {
            assertThrows(IllegalArgumentException.class, () -> VigenereCipher.encrypt(null, "KEY"));
            assertThrows(IllegalArgumentException.class, () -> VigenereCipher.decrypt(null, "KEY"));
        }
    }
}
