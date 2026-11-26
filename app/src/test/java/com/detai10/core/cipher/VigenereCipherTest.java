package com.detai10.core.cipher;

import org.junit.jupiter.api.DisplayName;
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
}
