package com.detai10.core.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ Unit Test cho DatabaseHelper và HistoryRecord (CORE-06).
 * Sử dụng H2 In-Memory Database để chạy độc lập trên Headless CI / Local.
 */
class DatabaseHelperTest {

    private DatabaseHelper dbHelper;

    @BeforeEach
    void setUp() {
        dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();
    }

    @Test
    @DisplayName("Khởi tạo bảng tự động và kết nối thành công")
    void testInitDatabase() {
        assertTrue(dbHelper.isConnectionAlive(), "Kết nối tới CSDL in-memory phải hoạt động");
    }

    @Test
    @DisplayName("Lưu bản ghi lịch sử bằng saveHistory và insertLog với cột io_mode")
    void testSaveAndInsertLogSuccess() {
        HistoryRecord record = new HistoryRecord(
                "192.168.1.10",
                "test.txt",
                "TEXT",
                "Hello World",
                "Rovvy Gybvn",
                "[0]"
        );

        assertTrue(dbHelper.saveHistory(record));
        assertTrue(record.getId() > 0);

        assertTrue(dbHelper.insertLog("127.0.0.1", "bin.dat", "BINARY", "No key", "Cipher", "NOT_FOUND"));

        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(2, historyList.size());

        HistoryRecord latest = historyList.get(0);
        assertEquals("127.0.0.1", latest.getClientIp());
        assertEquals("bin.dat", latest.getFileName());
        assertEquals("BINARY", latest.getIoMode());

        HistoryRecord first = historyList.get(1);
        assertEquals(record.getId(), first.getId());
        assertEquals("TEXT", first.getIoMode());
        assertEquals("[0]", first.getHelloPositions());
        assertNotNull(first.getCreatedAt());
    }

    @Test
    @DisplayName("Lưu bản ghi null trả về false an toàn")
    void testSaveNullRecord() {
        assertFalse(dbHelper.saveHistory(null));
    }

    @Test
    @DisplayName("Truy vấn toàn bộ lịch sử sắp xếp theo thời gian mới nhất")
    void testGetAllHistoryOrdering() throws InterruptedException {
        HistoryRecord rec1 = new HistoryRecord("127.0.0.1", "file1.txt", "TEXT", "First", "Cipher1", "NOT_FOUND");
        HistoryRecord rec2 = new HistoryRecord("127.0.0.1", "file2.txt", "BINARY", "Second", "Cipher2", "[5]");
        HistoryRecord rec3 = new HistoryRecord("127.0.0.1", "file3.txt", "TEXT", "Third", "Cipher3", "[0, 10]");

        dbHelper.saveHistory(rec1);
        Thread.sleep(10);
        dbHelper.saveHistory(rec2);
        Thread.sleep(10);
        dbHelper.saveHistory(rec3);

        List<HistoryRecord> list = dbHelper.getAllHistory();
        assertEquals(3, list.size());
        assertEquals(rec3.getId(), list.get(0).getId());
        assertEquals(rec2.getId(), list.get(1).getId());
        assertEquals(rec1.getId(), list.get(2).getId());
    }

    @Test
    @DisplayName("Truy vấn số lượng giới hạn getRecentHistory(limit)")
    void testGetRecentHistoryLimit() {
        for (int i = 1; i <= 10; i++) {
            dbHelper.saveHistory(new HistoryRecord(
                    "10.0.0." + i,
                    "file_" + i + ".dat",
                    i % 2 == 0 ? "BINARY" : "TEXT",
                    "Content " + i,
                    "Encrypted " + i,
                    "NOT_FOUND"
            ));
        }

        assertEquals(3, dbHelper.getRecentHistory(3).size());
        assertEquals(5, dbHelper.getRecentHistory(5).size());
    }

    @Test
    @DisplayName("Chống tấn công SQL Injection tuyệt đối (Zero SQL Injection)")
    void testZeroSqlInjection() {
        String maliciousPayload = "'; DROP TABLE lich_su_xu_ly; -- ' OR '1'='1";
        HistoryRecord injectionRecord = new HistoryRecord(
                "192.168.1.999",
                maliciousPayload,
                "TEXT",
                maliciousPayload,
                maliciousPayload,
                maliciousPayload
        );

        assertTrue(dbHelper.saveHistory(injectionRecord));
        List<HistoryRecord> list = dbHelper.getAllHistory();
        assertEquals(1, list.size());
        assertEquals(maliciousPayload, list.get(0).getFileName());
        assertTrue(dbHelper.isConnectionAlive());
    }

    @Test
    @DisplayName("Lưu và truy vấn nội dung Unicode Tiếng Việt UTF-8 nguyên vẹn")
    void testVietnameseUnicodeContent() {
        String original = "Xin chào các bạn sinh viên Đề tài 10, chúc một ngày tốt lành!";
        String encrypted = "Xjo djào dác cạn tjog wjên Đề tài 10, djúd một ogày tốt làoh!";

        assertTrue(dbHelper.insertLog("127.0.0.1", "tieng_viet.txt", "TEXT", original, encrypted, "NOT_FOUND"));

        List<HistoryRecord> list = dbHelper.getAllHistory();
        assertEquals(1, list.size());
        assertEquals(original, list.get(0).getOriginalContent());
        assertEquals(encrypted, list.get(0).getEncryptedContent());
    }

    @Test
    @DisplayName("Xóa sạch lịch sử với clearHistory()")
    void testClearHistory() {
        dbHelper.insertLog("127.0.0.1", "a.txt", "TEXT", "content", "cipher", "[0]");
        assertEquals(1, dbHelper.getAllHistory().size());

        dbHelper.clearHistory();
        assertEquals(0, dbHelper.getAllHistory().size());
    }
}
