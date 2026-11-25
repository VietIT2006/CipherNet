package com.detai10.core.cipher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ kiểm thử đơn vị cho thuật toán mã hóa Vigenère (CORE-02 / CORE-07).
 * Tuân thủ nghiêm ngặt Quy tắc 4:
 * - Chỉ dịch chuyển chữ cái tiếng Anh [A-Za-z].
 * - Bảo toàn số, khoảng trắng, dấu câu, ký tự đặc biệt.
 * - Con trỏ khóa không tăng bước khi gặp ký tự không phải chữ cái.
 */
class VigenereCipherTest {

    @Test
    @DisplayName("Mã hóa và giải mã chuẩn xác chuỗi tiếng Anh thuần túy theo vector chuẩn")
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
        // Ký tự số và ký hiệu giữ nguyên tuyệt đối
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

    @Test
    @DisplayName("Chuỗi chỉ gồm số và ký tự đặc biệt giữ nguyên không đổi")
    void testOnlyNonLetters() {
        String plain = "1234567890 !@#$%^&*()_+-=[]{}|;':,./<>?`~ \n\t";
        String key = "SECRET";

        String cipher = VigenereCipher.encrypt(plain, key);
        assertEquals(plain, cipher, "Văn bản không có chữ cái tiếng Anh phải giữ nguyên 100%");

        String decrypted = VigenereCipher.decrypt(cipher, key);
        assertEquals(plain, decrypted);
    }

    @Test
    @DisplayName("Bảo toàn các ký tự tiếng Việt có dấu (chỉ dịch chuyển chữ cái ASCII)")
    void testVietnameseCharactersPreserved() {
        String plain = "Hệ thống truyền nhận UDP Đề tài 10";
        String key = "CIPHER";

        String cipher = VigenereCipher.encrypt(plain, key);
        String decrypted = VigenereCipher.decrypt(cipher, key);

        assertEquals(plain, decrypted, "Giải mã phải khôi phục đúng hoàn toàn chuỗi tiếng Việt");
        // Chữ 'ệ', 'ố', 'ề', 'à' được bảo toàn
        assertTrue(cipher.contains("ệ"));
        assertTrue(cipher.contains("ố"));
        assertTrue(cipher.contains("ề"));
        assertTrue(cipher.contains("à"));
    }

    @Test
    @DisplayName("Chuẩn hóa khóa: chữ thường, số và ký tự đặc biệt")
    void testNormalizeKey() {
        assertEquals("LEMON", VigenereCipher.normalizeKey("lemon"));
        assertEquals("KEY", VigenereCipher.normalizeKey("k-e_y!123"));
        assertEquals("KEY", VigenereCipher.normalizeKey("  k e y  "));

        // Khóa rỗng hoặc null mặc định về DEFAULT_KEY
        assertEquals(VigenereCipher.DEFAULT_KEY, VigenereCipher.normalizeKey(null));
        assertEquals(VigenereCipher.DEFAULT_KEY, VigenereCipher.normalizeKey(""));
        assertEquals(VigenereCipher.DEFAULT_KEY, VigenereCipher.normalizeKey("12345!@#$"));
    }

    @Test
    @DisplayName("Xử lý chuỗi rỗng hoặc null an toàn")
    void testNullAndEmptyText() {
        assertNull(VigenereCipher.encrypt(null, "KEY"));
        assertEquals("", VigenereCipher.encrypt("", "KEY"));

        assertNull(VigenereCipher.decrypt(null, "KEY"));
        assertEquals("", VigenereCipher.decrypt("", "KEY"));
    }

    @Test
    @DisplayName("Khởi tạo thể hiện tiện ích qua reflection phải ném ngoại lệ UnsupportedOperationException")
    void testPrivateConstructor() throws Exception {
        Constructor<VigenereCipher> constructor = VigenereCipher.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertNotNull(thrown.getCause());
        assertTrue(thrown.getCause() instanceof UnsupportedOperationException);
    }
}
