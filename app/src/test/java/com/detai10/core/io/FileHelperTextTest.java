package com.detai10.core.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileHelperTextTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Ghi và đọc toàn vẹn tiếng Việt có dấu UTF-8 và ký tự đặc biệt")
    void testWriteAndReadVietnameseUtf8AndSpecialChars() throws IOException {
        Path textFile = tempDir.resolve("utf8_vietnamese.txt");
        String content = "Xin chào thế giới! HELLO Đề tài 10 — ăâđêôơư @#$%|";

        FileHelper.writeTextFile(textFile, content);
        assertTrue(Files.exists(textFile));

        String actual = FileHelper.readTextFile(textFile);
        assertEquals(content, actual);
    }

    @Test
    @DisplayName("Bảo toàn nguyên vẹn ký tự xuống dòng, dòng trống, tab và xuống dòng cuối file")
    void testPreserveNewlinesBlankLinesAndTabs() throws IOException {
        Path textFile = tempDir.resolve("multiline.txt");
        String content = "Dòng 1: HELLO\n\nDòng 3:\tTab ở đây\r\nDòng 4 có dấu |\n";

        FileHelper.writeTextFile(textFile, content);
        String actual = FileHelper.readTextFile(textFile);

        assertEquals(content, actual);
    }

    @Test
    @DisplayName("Ghi và đọc chuỗi rỗng trả về chuỗi rỗng")
    void testWriteAndReadEmptyString() throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");

        FileHelper.writeTextFile(emptyFile, "");
        assertTrue(Files.exists(emptyFile));
        assertEquals("", FileHelper.readTextFile(emptyFile));
    }

    @Test
    @DisplayName("Tự động tạo thư mục cha lồng nhau nếu chưa tồn tại")
    void testAutoCreateNestedParentDirectories() throws IOException {
        Path nestedFile = tempDir.resolve("nested/sub/folder/demo.txt");
        String content = "Nội dung trong thư mục con tự tạo";

        FileHelper.writeTextFile(nestedFile, content);

        assertTrue(Files.isDirectory(nestedFile.getParent()));
        assertTrue(Files.exists(nestedFile));
        assertEquals(content, FileHelper.readTextFile(nestedFile));
    }

    @Test
    @DisplayName("Ghi đè file đã tồn tại cập nhật đúng nội dung mới")
    void testOverwriteExistingFile() throws IOException {
        Path targetFile = tempDir.resolve("overwrite.txt");

        FileHelper.writeTextFile(targetFile, "Nội dung cũ dài hơn rất nhiều");
        FileHelper.writeTextFile(targetFile, "Nội dung mới");

        assertEquals("Nội dung mới", FileHelper.readTextFile(targetFile));
    }

    @Test
    @DisplayName("Nạp chồng tham số java.io.File hoạt động đồng nhất với Path")
    void testFileOverloadsWorkIdentically() throws IOException {
        File targetFile = tempDir.resolve("by_file_api/sample.txt").toFile();
        String content = "Dữ liệu dùng java.io.File — HELLO!";

        FileHelper.writeTextFile(targetFile, content);
        assertTrue(targetFile.exists());
        assertEquals(content, FileHelper.readTextFile(targetFile));
    }

    @Test
    @DisplayName("Đọc file không tồn tại ném IOException có thông điệp rõ ràng")
    void testReadNonExistentFileThrowsIOException() {
        Path missingFile = tempDir.resolve("khong_ton_tai.txt");

        IOException exception = assertThrows(IOException.class, () -> FileHelper.readTextFile(missingFile));
        assertTrue(exception.getMessage().contains("Không tìm thấy file văn bản"));
    }

    @Test
    @DisplayName("Đọc hoặc ghi vào đường dẫn là thư mục ném IOException")
    void testDirectoryPathThrowsIOException() throws IOException {
        Path directory = tempDir.resolve("a_directory");
        Files.createDirectories(directory);

        IOException readEx = assertThrows(IOException.class, () -> FileHelper.readTextFile(directory));
        assertTrue(readEx.getMessage().contains("thư mục"));

        IOException writeEx = assertThrows(IOException.class, () -> FileHelper.writeTextFile(directory, "data"));
        assertTrue(writeEx.getMessage().contains("Không thể ghi file văn bản"));
    }

    @Test
    @DisplayName("Tham số null cho đường dẫn hoặc nội dung ném IllegalArgumentException")
    void testNullArgumentsThrowIllegalArgumentException() {
        Path validPath = tempDir.resolve("valid.txt");
        File validFile = validPath.toFile();

        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeTextFile((Path) null, "abc"));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeTextFile((File) null, "abc"));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeTextFile(validPath, null));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeTextFile(validFile, null));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.readTextFile((Path) null));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.readTextFile((File) null));
    }
}

