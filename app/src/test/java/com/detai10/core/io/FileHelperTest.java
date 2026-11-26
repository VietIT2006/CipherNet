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

class FileHelperTest {

    @TempDir
    File tempDir;

    @Test
    @DisplayName("Ghi và đọc file chế độ TEXT UTF-8 nguyên vẹn")
    void testTextMode() throws IOException {
        File file = new File(tempDir, "nested/sample.txt");
        String content = "Xin chào Việt Nam! Lap trinh mang UDP.\nDong 2 chua ky tu dac biet @#$%^&*()";

        FileHelper.writeFile(file, content, FileMode.TEXT);
        assertTrue(file.exists());
        String readBack = FileHelper.readFile(file, FileMode.TEXT);

        assertEquals(content, readBack);
    }

    @Test
    @DisplayName("Ghi và đọc file chế độ BINARY nguyên vẹn")
    void testBinaryMode() throws IOException {
        File file = new File(tempDir, "nested/sample.bin");
        String content = "Du lieu nhi phan co dinh kieu UTF: HELLO WORLD 2026";

        FileHelper.writeFile(file, content, FileMode.BINARY);
        assertTrue(file.exists());
        String readBack = FileHelper.readFile(file, FileMode.BINARY);

        assertEquals(content, readBack);
    }

    @Test
    @DisplayName("Đọc file không tồn tại ném IOException rõ ràng")
    void testReadMissingFileThrowsIOException() {
        File missing = new File(tempDir, "missing.txt");
        assertThrows(IOException.class, () -> FileHelper.readFile(missing, FileMode.TEXT));
        assertThrows(IOException.class, () -> FileHelper.readFile(missing, FileMode.BINARY));
    }
}
