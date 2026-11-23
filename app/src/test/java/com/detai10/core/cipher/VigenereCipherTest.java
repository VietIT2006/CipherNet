package com.detai10.core.cipher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        String plain = "Hello 123! How are you?";
        String key = "KEY";

        String cipher = VigenereCipher.encrypt(plain, key);
        String decrypted = VigenereCipher.decrypt(cipher, key);

        assertEquals(plain, decrypted);
        // Ký tự số và ký hiệu giữ nguyên
        assertEquals(" 123! ", cipher.substring(5, 11));
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
}
