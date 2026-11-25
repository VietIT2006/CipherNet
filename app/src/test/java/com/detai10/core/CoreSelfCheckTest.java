package com.detai10.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ kiểm thử đơn vị cho lớp CoreSelfCheck (CORE-07).
 */
class CoreSelfCheckTest {

    @Test
    @DisplayName("CoreSelfCheck.runAllChecks() phải trả về true và đạt 100% [ALL PASS]")
    void testRunAllChecks() {
        boolean result = CoreSelfCheck.runAllChecks();
        assertTrue(result, "Toàn bộ chuỗi tự kiểm tra tầng Core phải đạt kết quả thành công");
    }

    @Test
    @DisplayName("Kiểm tra từng phương thức kiểm tra độc lập trong CoreSelfCheck")
    void testIndividualChecks() {
        assertTrue(CoreSelfCheck.checkUdpProtocol(), "Kiểm tra UdpProtocol phải thành công");
        assertTrue(CoreSelfCheck.checkVigenereCipher(), "Kiểm tra VigenereCipher phải thành công");
        assertTrue(CoreSelfCheck.checkKeywordFinder(), "Kiểm tra KeywordFinder phải thành công");
        assertTrue(CoreSelfCheck.checkFileHelper(), "Kiểm tra FileHelper phải thành công");
        assertTrue(CoreSelfCheck.checkDatabaseHelper(), "Kiểm tra DatabaseHelper phải thành công");
    }

    @Test
    @DisplayName("CoreSelfCheck.main() thực thi an toàn không ném ngoại lệ khi tất cả checks pass")
    void testMainMethodExecution() {
        assertDoesNotThrow(() -> CoreSelfCheck.runAllChecks());
    }
}
