package com.detai10.core.io;

import com.detai10.core.model.FileMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ kiểm thử đơn vị cho thao tác đọc/ghi file chế độ TEXT (UTF-8).
 * Tuân thủ Quy tắc 5 (Bảo toàn toàn vẹn dữ liệu trong File I/O).
 */
class FileHelperTextTest {

    @TempDir
    File tempDir;

    @Test
    @DisplayName("Ghi và đọc file văn bản UTF-8 tiếng Việt có dấu và ký tự đặc biệt nguyên vẹn")
    void testWriteAndReadUtf8TextWithAccentsAndSymbols() throws IOException {
        File file = new File(tempDir, "vietnamese_sample.txt");
        String content = "Xin chào Việt Nam! Hệ thống Client - Server UDP Đề tài 10.\n" +
                "Ký tự đặc biệt: @#$%^&*()_+~{}[]|:\"<>? và khoảng trắng   \ttab\n" +
                "Ký tự số: 0123456789";

        FileHelper.writeTextFile(file, content);
        String readBack = FileHelper.readTextFile(file);

        assertEquals(content, readBack, "Nội dung đọc lại từ file TEXT UTF-8 phải trùng khớp 100% với nội dung ghi");
    }

    @Test
    @DisplayName("Ghi và đọc file TEXT rỗng hoặc nội dung null an toàn")
    void testEmptyAndNullContentText() throws IOException {
        File emptyFile = new File(tempDir, "empty.txt");
        FileHelper.writeTextFile(emptyFile, "");
        assertEquals("", FileHelper.readTextFile(emptyFile));

        File nullFile = new File(tempDir, "null_content.txt");
        FileHelper.writeTextFile(nullFile, null);
        assertEquals("", FileHelper.readTextFile(nullFile));
    }

    @Test
    @DisplayName("Tự động tạo các thư mục cha lồng nhau khi ghi file TEXT")
    void testAutoCreateParentDirectories() throws IOException {
        File nestedFile = new File(tempDir, "nested/sub1/sub2/text_file.txt");
        String content = "Tự động tạo thư mục cha lồng nhau thành công.";

        FileHelper.writeTextFile(nestedFile, content);
        assertTrue(nestedFile.exists(), "File phải tồn tại sau khi ghi");
        assertEquals(content, FileHelper.readTextFile(nestedFile));
    }

    @Test
    @DisplayName("Đọc file TEXT không tồn tại phải ném ngoại lệ IOException")
    void testReadNonExistentFileThrowsException() {
        File nonExistent = new File(tempDir, "does_not_exist.txt");
        assertThrows(IOException.class, () -> FileHelper.readTextFile(nonExistent));
    }

    @Test
    @DisplayName("Ủy quyền qua phương thức chung FileHelper.writeFile và FileHelper.readFile với FileMode.TEXT")
    void testFileHelperDelegationTextMode() throws IOException {
        File file = new File(tempDir, "delegation_text.txt");
        String content = "Nội dung ủy quyền qua FileMode.TEXT";

        FileHelper.writeFile(file, content, FileMode.TEXT);
        String result = FileHelper.readFile(file, FileMode.TEXT);

        assertEquals(content, result);
    }

    @Test
    @DisplayName("Lớp tiện ích FileHelper không cho phép khởi tạo thể hiện qua reflection")
    void testPrivateConstructor() throws Exception {
        Constructor<FileHelper> constructor = FileHelper.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertNotNull(thrown.getCause());
        assertTrue(thrown.getCause() instanceof UnsupportedOperationException);
    }
}
