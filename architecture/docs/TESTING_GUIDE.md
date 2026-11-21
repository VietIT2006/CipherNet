# Hướng Dẫn Kiểm Thử Tự Động Dành Cho AI Agent (Automated Testing Guide)

Tài liệu này hướng dẫn cách xây dựng và thực thi các bài kiểm thử tự động (Unit Test / Integration Test) trong dự án **Vigen (Đề tài 10)**.

---

## 1. Triết lý Kiểm thử (Testing Philosophy)
1. **Kiểm thử độc lập với Giao diện (Headless Execution):** Tầng Core Logic phải được kiểm thử 100% tự động mà không yêu cầu khởi chạy màn hình đồ họa (Java Swing). Điều này cho phép GitHub Actions (Ubuntu runner) chạy xanh không cần biến ảo `DISPLAY` hay `Xvfb`.
2. **Không để lại file rác:** Khi kiểm thử tính năng đọc/ghi file (`FileHelper`), bắt buộc phải dùng thư mục tạm thời do framework JUnit 5 cấp phát qua `@TempDir`.
3. **Tính độc lập của Test Case:** Mỗi test case phải tự khởi tạo dữ liệu giả lập và không phụ thuộc vào thứ tự chạy của các bài test khác.

---

## 2. Cấu trúc Thư mục Kiểm thử

```text
app/src/test/java/com/detai10/core/
├── cipher/
│   └── VigenereCipherTest.java      # Kiểm thử mã hóa & giải mã, bảo toàn ký tự
├── io/
│   └── FileHelperTest.java          # Kiểm thử I/O Text và Binary sử dụng @TempDir
├── protocol/
│   └── UdpProtocolTest.java         # Kiểm thử bóc tách và đóng gói tin an toàn
├── search/
│   └── KeywordFinderTest.java       # Kiểm thử thuật toán tìm từ khóa 'HELLO'
└── db/
    └── DatabaseHelperTest.java      # Kiểm thử logic DAO (mock/embedded DB)
```

---

## 3. Mẫu viết Unit Test chuẩn

### 3.1. Test Vigenère Cipher (VigenereCipherTest.java)
```java
package com.detai10.core.cipher;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VigenereCipherTest {

    @Test
    @DisplayName("Mã hóa và giải mã chuỗi chuẩn phải trả về bản rõ ban đầu")
    void testEncryptAndDecryptCycle() {
        String original = "HELLO World 2026!";
        String key = "SECRET";
        
        String encrypted = VigenereCipher.encrypt(original, key);
        assertNotEquals(original, encrypted);
        
        String decrypted = VigenereCipher.decrypt(encrypted, key);
        assertEquals(original, decrypted);
    }

    @Test
    @DisplayName("Ký tự số, khoảng trắng và dấu câu phải được giữ nguyên vẹn")
    void testInvariantCharacters() {
        String original = "123 !@# \n 456";
        String key = "KEY";
        
        String encrypted = VigenereCipher.encrypt(original, key);
        assertEquals(original, encrypted, "Ký tự không phải chữ cái không được biến đổi!");
    }
}
```

### 3.2. Test File I/O với `@TempDir` (FileHelperTest.java)
```java
package com.detai10.core.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileHelperTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Ghi và đọc file chế độ TEXT UTF-8 toàn vẹn")
    void testTextModeFile() throws IOException {
        Path textFile = tempDir.resolve("test_utf8.txt");
        String content = "Xin chào thế giới! HELLO Vietnamese UTF-8";
        
        FileHelper.writeTextFile(textFile, content);
        assertTrue(textFile.toFile().exists());
        
        String readBack = FileHelper.readTextFile(textFile);
        assertEquals(content, readBack);
    }

    @Test
    @DisplayName("Ghi và đọc file chế độ BINARY định kiểu có đệm toàn vẹn")
    void testBinaryModeFile() throws IOException {
        Path binFile = tempDir.resolve("test_data.bin");
        String content = "Binary Encrypted Payload HELLO 123456";
        
        FileHelper.writeBinaryFile(binFile, content);
        assertTrue(binFile.toFile().exists());
        
        String readBack = FileHelper.readBinaryFile(binFile);
        assertEquals(content, readBack);
    }
}
```

### 3.3. Test Tìm kiếm Từ khóa (KeywordFinderTest.java)
```java
package com.detai10.core.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KeywordFinderTest {

    @Test
    @DisplayName("Tìm thấy tất cả các vị trí xuất hiện của HELLO")
    void testFindMultipleOccurrences() {
        String text = "HELLO world, this is a HELLO test. Say HELLO!";
        List<Integer> indices = KeywordFinder.findIndices(text, "HELLO");
        
        assertEquals(3, indices.size());
        assertEquals(0, indices.get(0));
        assertEquals(23, indices.get(1));
        assertEquals(39, indices.get(2));
    }

    @Test
    @DisplayName("Trả về danh sách rỗng khi không có từ khóa")
    void testNotFound() {
        String text = "Xin chao, khong co tu khoa nao o day.";
        List<Integer> indices = KeywordFinder.findIndices(text, "HELLO");
        assertTrue(indices.isEmpty());
    }
}
```

---

## 4. Lệnh chạy kiểm thử

Chạy toàn bộ test trên terminal:
```bash
./gradlew test --no-daemon
```

Xem báo cáo HTML trực quan:
```text
app/build/reports/tests/test/index.html
```
Mở file trên trình duyệt để kiểm tra chi tiết các ca kiểm thử đạt/không đạt.
