package com.detai10.core.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileHelperBinaryTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Ghi và đọc file nhị phân khớp 100% nội dung gốc và có tiền tố độ dài 2-byte")
    void testWriteAndReadBinaryAsciiPayload() throws IOException {
        Path binFile = tempDir.resolve("payload.bin");
        String content = "Binary Encrypted Payload HELLO 123456";

        FileHelper.writeBinaryFile(binFile, content);

        assertTrue(Files.exists(binFile));
        assertEquals(2L + content.length(), Files.size(binFile));
        assertEquals(content, FileHelper.readBinaryFile(binFile));
    }

    @Test
    @DisplayName("Ghi và đọc toàn vẹn tiếng Việt có dấu, xuống dòng, tab và ký tự đặc biệt ở chế độ Binary")
    void testWriteAndReadBinaryUtf8AndSpecialChars() throws IOException {
        Path binFile = tempDir.resolve("utf8_binary.bin");
        String content = "Xin chào HELLO!\nDòng 2\r\n\tTab | @#$%^&*() ăâđêôơư";

        FileHelper.writeBinaryFile(binFile, content);
        assertEquals(content, FileHelper.readBinaryFile(binFile));
    }

    @Test
    @DisplayName("Ghi và đọc chuỗi rỗng ở chế độ Binary tạo header 2-byte và trả về chuỗi rỗng")
    void testWriteAndReadBinaryEmptyString() throws IOException {
        Path binFile = tempDir.resolve("empty_str.bin");

        FileHelper.writeBinaryFile(binFile, "");

        assertEquals(2L, Files.size(binFile));
        assertEquals("", FileHelper.readBinaryFile(binFile));
    }

    @Test
    @DisplayName("Tự động tạo thư mục cha lồng nhau khi ghi file nhị phân")
    void testAutoCreateNestedParentDirectories() throws IOException {
        Path nestedBin = tempDir.resolve("nested/bin/data.bin");
        String content = "Payload trong thư mục con";

        FileHelper.writeBinaryFile(nestedBin, content);

        assertTrue(Files.isDirectory(nestedBin.getParent()));
        assertTrue(Files.exists(nestedBin));
        assertEquals(content, FileHelper.readBinaryFile(nestedBin));
    }

    @Test
    @DisplayName("Ghi đè file nhị phân đã tồn tại cập nhật đúng nội dung mới")
    void testOverwriteExistingBinaryFile() throws IOException {
        Path binFile = tempDir.resolve("overwrite.bin");

        FileHelper.writeBinaryFile(binFile, "Nội dung nhị phân cũ rất dài");
        FileHelper.writeBinaryFile(binFile, "Nội dung mới");

        assertEquals("Nội dung mới", FileHelper.readBinaryFile(binFile));
    }

    @Test
    @DisplayName("Nạp chồng tham số java.io.File hoạt động đồng nhất với Path")
    void testFileOverloadsWorkIdentically() throws IOException {
        File binFile = tempDir.resolve("by_file/sample.bin").toFile();
        String content = "Binary Data via File API — HELLO!";

        FileHelper.writeBinaryFile(binFile, content);
        assertTrue(binFile.exists());
        assertEquals(content, FileHelper.readBinaryFile(binFile));
    }

    @Test
    @DisplayName("Đọc file nhị phân rỗng 0 byte ném EOFException có thông điệp rõ ràng")
    void testReadZeroByteFileThrowsEOFException() throws IOException {
        Path emptyRawFile = tempDir.resolve("zero_bytes.bin");
        Files.createFile(emptyRawFile);

        EOFException exception = assertThrows(EOFException.class, () -> FileHelper.readBinaryFile(emptyRawFile));
        assertTrue(exception.getMessage().contains("EOF"));
    }

    @Test
    @DisplayName("Đọc file nhị phân bị cắt cụt dữ liệu ném EOFException")
    void testReadTruncatedBinaryFileThrowsEOFException() throws IOException {
        Path truncatedFile = tempDir.resolve("truncated.bin");
        // Tiền tố độ dài khai báo 10 byte (0x00, 0x0A) nhưng chỉ ghi 3 byte dữ liệu.
        Files.write(truncatedFile, new byte[]{0x00, 0x0A, 'A', 'B', 'C'});

        EOFException exception = assertThrows(EOFException.class, () -> FileHelper.readBinaryFile(truncatedFile));
        assertTrue(exception.getMessage().contains("EOF"));
    }

    @Test
    @DisplayName("Đọc file nhị phân không tồn tại hoặc đường dẫn là thư mục ném IOException")
    void testMissingFileAndDirectoryThrowIOException() throws IOException {
        Path missingFile = tempDir.resolve("missing.bin");
        Path directory = tempDir.resolve("dir_path");
        Files.createDirectories(directory);

        IOException missingEx = assertThrows(IOException.class, () -> FileHelper.readBinaryFile(missingFile));
        assertTrue(missingEx.getMessage().contains("Không tìm thấy file nhị phân"));

        IOException readDirEx = assertThrows(IOException.class, () -> FileHelper.readBinaryFile(directory));
        assertTrue(readDirEx.getMessage().contains("thư mục"));

        IOException writeDirEx = assertThrows(IOException.class, () -> FileHelper.writeBinaryFile(directory, "abc"));
        assertTrue(writeDirEx.getMessage().contains("Không thể ghi file nhị phân"));
    }

    @Test
    @DisplayName("Tham số null cho đường dẫn hoặc nội dung ném IllegalArgumentException")
    void testNullArgumentsThrowIllegalArgumentException() {
        Path validPath = tempDir.resolve("valid.bin");
        File validFile = validPath.toFile();

        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeBinaryFile((Path) null, "abc"));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeBinaryFile((File) null, "abc"));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeBinaryFile(validPath, null));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.writeBinaryFile(validFile, null));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.readBinaryFile((Path) null));
        assertThrows(IllegalArgumentException.class, () -> FileHelper.readBinaryFile((File) null));
    }
}

