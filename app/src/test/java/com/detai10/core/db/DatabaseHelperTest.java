package com.detai10.core.db;

import com.detai10.core.model.ModelHistoryRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ Unit Test bao phủ 100% kịch bản cho DatabaseHelper, HistoryRecord và ModelHistoryRecord (CORE-06 / CORE-07).
 * Sử dụng H2 In-Memory Database để chạy độc lập và chuẩn xác trên mọi môi trường (Headless CI / Local).
 */
class DatabaseHelperTest {

    private DatabaseHelper dbHelper;

    @BeforeEach
    void setUp() {
        // Khởi tạo thể hiện H2 Database in-memory cho từng ca kiểm thử
        dbHelper = DatabaseHelper.createInMemoryHelper();
        dbHelper.clearHistory();
    }

    @Test
    @DisplayName("Khởi tạo bảng tự động và kết nối thành công")
    void testInitDatabase() {
        assertTrue(dbHelper.isConnectionAlive(), "Kết nối tới CSDL in-memory phải hoạt động");
    }

    @Test
    @DisplayName("Lưu bản ghi lịch sử thành công và tự động sinh ID")
    void testSaveHistorySuccess() {
        HistoryRecord record = new HistoryRecord(
                "192.168.1.10",
                "test.txt",
                "TEXT",
                "Hello World",
                "Rovvy Gybvn",
                "0"
        );

        boolean saved = dbHelper.saveHistory(record);
        assertTrue(saved, "saveHistory phải trả về true");
        assertTrue(record.getId() > 0, "ID bản ghi phải được tự sinh tăng dần");

        List<HistoryRecord> historyList = dbHelper.getAllHistory();
        assertEquals(1, historyList.size());

        HistoryRecord retrieved = historyList.get(0);
        assertEquals(record.getId(), retrieved.getId());
        assertEquals("192.168.1.10", retrieved.getClientIp());
        assertEquals("test.txt", retrieved.getFileName());
        assertEquals("TEXT", retrieved.getFileMode());
        assertEquals("Hello World", retrieved.getOriginalContent());
        assertEquals("Rovvy Gybvn", retrieved.getEncryptedContent());
        assertEquals("0", retrieved.getHelloPositions());
        assertNotNull(retrieved.getCreatedAt(), "Thời gian created_at phải được CSDL gán tự động");
    }

    @Test
    @DisplayName("Lưu bản ghi null trả về false an toàn")
    void testSaveNullRecord() {
        assertFalse(dbHelper.saveHistory(null));
    }

    @Test
    @DisplayName("Truy vấn toàn bộ lịch sử sắp xếp theo thời gian mới nhất (ORDER BY created_at DESC, id DESC)")
    void testGetAllHistoryOrdering() throws InterruptedException {
        HistoryRecord rec1 = new HistoryRecord("127.0.0.1", "file1.txt", "TEXT", "First", "Cipher1", "NOT_FOUND");
        HistoryRecord rec2 = new HistoryRecord("127.0.0.1", "file2.txt", "BINARY", "Second", "Cipher2", "5");
        HistoryRecord rec3 = new HistoryRecord("127.0.0.1", "file3.txt", "TEXT", "Third", "Cipher3", "0, 10");

        dbHelper.saveHistory(rec1);
        Thread.sleep(10);
        dbHelper.saveHistory(rec2);
        Thread.sleep(10);
        dbHelper.saveHistory(rec3);

        List<HistoryRecord> list = dbHelper.getAllHistory();
        assertEquals(3, list.size());
        // rec3 là mới nhất phải đứng đầu danh sách
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

        List<HistoryRecord> top3 = dbHelper.getRecentHistory(3);
        assertEquals(3, top3.size(), "getRecentHistory(3) phải trả về đúng 3 bản ghi mới nhất");

        List<HistoryRecord> top5 = dbHelper.getRecentHistory(5);
        assertEquals(5, top5.size());
    }

    @Test
    @DisplayName("Chống tấn công SQL Injection tuyệt đối (Zero SQL Injection - Quy tắc 6)")
    void testZeroSqlInjection() {
        // Dữ liệu giả lập tấn công chèn chuỗi độc hại
        String maliciousPayload = "'; DROP TABLE lich_su_xu_ly; -- ' OR '1'='1";
        HistoryRecord injectionRecord = new HistoryRecord(
                "192.168.1.999",
                maliciousPayload,
                "TEXT",
                maliciousPayload,
                maliciousPayload,
                maliciousPayload
        );

        boolean saved = dbHelper.saveHistory(injectionRecord);
        assertTrue(saved, "PreparedStatement phải bọc chuỗi an toàn và lưu thành công");

        List<HistoryRecord> list = dbHelper.getAllHistory();
        assertEquals(1, list.size());
        assertEquals(maliciousPayload, list.get(0).getFileName());
        assertEquals(maliciousPayload, list.get(0).getOriginalContent());

        // Xác nhận bảng lich_su_xu_ly không hề bị DROP
        assertTrue(dbHelper.isConnectionAlive());
    }

    @Test
    @DisplayName("Lưu và truy vấn nội dung Unicode Tiếng Việt UTF-8 nguyên vẹn")
    void testVietnameseUnicodeContent() {
        String original = "Xin chào các bạn sinh viên Đề tài 10, chúc một ngày tốt lành!";
        String encrypted = "Xjo djào dác cạn tjog wjên Đề tài 10, djúd một ogày tốt làoh!";

        HistoryRecord record = new HistoryRecord(
                "127.0.0.1",
                "tieng_viet.txt",
                "TEXT",
                original,
                encrypted,
                "NOT_FOUND"
        );

        assertTrue(dbHelper.saveHistory(record));

        List<HistoryRecord> list = dbHelper.getAllHistory();
        assertEquals(1, list.size());
        assertEquals(original, list.get(0).getOriginalContent());
        assertEquals(encrypted, list.get(0).getEncryptedContent());
    }

    @Test
    @DisplayName("Xóa sạch lịch sử với clearHistory()")
    void testClearHistory() {
        dbHelper.saveHistory(new HistoryRecord("127.0.0.1", "a.txt", "TEXT", "content", "cipher", "0"));
        assertEquals(1, dbHelper.getAllHistory().size());

        dbHelper.clearHistory();
        assertEquals(0, dbHelper.getAllHistory().size());
    }

    @Test
    @DisplayName("Kiểm tra toàn diện HistoryRecord và ModelHistoryRecord constructors, getters, setters, equals, hashCode")
    void testHistoryRecordModel() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        HistoryRecord r1 = new HistoryRecord(100, "10.0.0.1", "f1.txt", "TEXT", "orig", "enc", "0", now);
        assertEquals(100, r1.getId());
        assertEquals("10.0.0.1", r1.getClientIp());
        assertEquals("f1.txt", r1.getFileName());
        assertEquals("TEXT", r1.getFileMode());
        assertEquals("orig", r1.getOriginalContent());
        assertEquals("enc", r1.getEncryptedContent());
        assertEquals("0", r1.getHelloPositions());
        assertEquals(now, r1.getCreatedAt());

        HistoryRecord r2 = new HistoryRecord();
        r2.setId(100);
        r2.setClientIp("10.0.0.1");
        r2.setFileName("f1.txt");
        r2.setFileMode("TEXT");
        r2.setOriginalContent("orig");
        r2.setEncryptedContent("enc");
        r2.setHelloPositions("0");
        r2.setCreatedAt(now);

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
        assertEquals(r1, r1);
        assertNotEquals(r1, null);
        assertNotEquals(r1, "Khong phai HistoryRecord");

        // ModelHistoryRecord kế thừa
        ModelHistoryRecord m1 = new ModelHistoryRecord();
        m1.setId(1);
        m1.setClientIp("127.0.0.1");
        m1.setFileName("m.txt");
        m1.setFileMode("BINARY");
        m1.setOriginalContent("origM");
        m1.setEncryptedContent("encM");
        m1.setHelloPositions("5");
        m1.setCreatedAt(now);

        ModelHistoryRecord m2 = new ModelHistoryRecord("127.0.0.1", "m.txt", "BINARY", "origM", "encM", "5");
        m2.setId(1);
        m2.setCreatedAt(now);
        assertEquals(m1, m2);

        ModelHistoryRecord m3 = new ModelHistoryRecord(1, "127.0.0.1", "m.txt", "BINARY", "origM", "encM", "5", now);
        assertEquals(m1, m3);
    }

    @Test
    @DisplayName("Kiểm tra Singleton getInstance và setInstance")
    void testSingletonInstance() {
        DatabaseHelper helper = DatabaseHelper.getInstance();
        assertNotNull(helper);
        assertTrue(helper.isConnectionAlive());

        DatabaseHelper custom = DatabaseHelper.createInMemoryHelper();
        DatabaseHelper.setInstance(custom);
        assertEquals(custom, DatabaseHelper.getInstance());
        // Khôi phục lại
        DatabaseHelper.setInstance(helper);
    }

    @Test
    @DisplayName("Kiểm tra AutoCloseable close() an toàn")
    void testAutoCloseable() {
        try (DatabaseHelper helper = DatabaseHelper.createInMemoryHelper()) {
            assertTrue(helper.isConnectionAlive());
        }
    }
}
