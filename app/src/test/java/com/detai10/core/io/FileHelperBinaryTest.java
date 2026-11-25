package com.detai10.core.io;

import com.detai10.core.model.FileMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ kiểm thử đơn vị cho thao tác đọc/ghi file chế độ BINARY (DataInputStream / DataOutputStream).
 * Tuân thủ Quy tắc 5 (Bảo toàn toàn vẹn dữ liệu trong File I/O).
 */
class FileHelperBinaryTest {

    @TempDir
    File tempDir;

    @Test
    @DisplayName("Ghi và đọc file chế độ BINARY định kiểu DataStream nguyên vẹn")
    void testWriteAndReadBinaryData() throws IOException {
        File file = new File(tempDir, "sample.bin");
        String content = "Dữ liệu nhị phân có đệm DataOutputStream: HELLO WORLD 2026 - Đề tài 10 UDP!";

        FileHelper.writeBinaryFile(file, content);
        String readBack = FileHelper.readBinaryFile(file);

        assertEquals(content, readBack, "Nội dung nhị phân readUTF phải trùng khớp với writeUTF");
    }

    @Test
    @DisplayName("Ghi và đọc file BINARY với chuỗi rỗng hoặc null an toàn")
    void testEmptyAndNullContentBinary() throws IOException {
        File emptyFile = new File(tempDir, "empty_bin.dat");
        FileHelper.writeBinaryFile(emptyFile, "");
        assertEquals("", FileHelper.readBinaryFile(emptyFile));

        File nullFile = new File(tempDir, "null_bin.dat");
        FileHelper.writeBinaryFile(nullFile, null);
        assertEquals("", FileHelper.readBinaryFile(nullFile));
    }

    @Test
    @DisplayName("Tự động tạo các thư mục cha khi ghi file BINARY")
    void testAutoCreateParentDirsBinary() throws IOException {
        File nestedFile = new File(tempDir, "bin_sub/sub2/test_binary.bin");
        String content = "Dữ liệu nhị phân trong thư mục lồng nhau";

        FileHelper.writeBinaryFile(nestedFile, content);
        assertTrue(nestedFile.exists());
        assertEquals(content, FileHelper.readBinaryFile(nestedFile));
    }

    @Test
    @DisplayName("Đọc file BINARY không tồn tại phải ném ngoại lệ IOException")
    void testReadNonExistentBinaryThrowsException() {
        File nonExistent = new File(tempDir, "non_existent.bin");
        assertThrows(IOException.class, () -> FileHelper.readBinaryFile(nonExistent));
    }

    @Test
    @DisplayName("Ủy quyền qua phương thức chung FileHelper.writeFile và FileHelper.readFile với FileMode.BINARY")
    void testFileHelperDelegationBinaryMode() throws IOException {
        File file = new File(tempDir, "delegation_binary.bin");
        String content = "Nội dung ủy quyền qua FileMode.BINARY định kiểu DataStream";

        FileHelper.writeFile(file, content, FileMode.BINARY);
        String result = FileHelper.readFile(file, FileMode.BINARY);

        assertEquals(content, result);
    }
}
