package com.detai10.core.io;

import com.detai10.core.model.FileMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileHelperTest {

    @TempDir
    File tempDir;

    @Test
    @DisplayName("Ghi và đọc file chế độ TEXT UTF-8 nguyên vẹn")
    void testTextMode() throws IOException {
        File file = new File(tempDir, "sample.txt");
        String content = "Xin chào Việt Nam! Lap trinh mang UDP.\nDong 2 chua ky tu dac biet @#$%^&*()";

        FileHelper.writeFile(file, content, FileMode.TEXT);
        String readBack = FileHelper.readFile(file, FileMode.TEXT);

        assertEquals(content, readBack);
    }

    @Test
    @DisplayName("Ghi và đọc file chế độ BINARY nguyên vẹn")
    void testBinaryMode() throws IOException {
        File file = new File(tempDir, "sample.bin");
        String content = "Du lieu nhi phan co dinh kieu UTF: HELLO WORLD 2026";

        FileHelper.writeFile(file, content, FileMode.BINARY);
        String readBack = FileHelper.readFile(file, FileMode.BINARY);

        assertEquals(content, readBack);
    }
}
