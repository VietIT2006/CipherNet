package com.detai10.core.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileModeTest {

    @Test
    @DisplayName("Chuyển chuỗi hợp lệ thành FileMode")
    void testFromStringValid() {
        assertEquals(FileMode.TEXT, FileMode.fromString("TEXT"));
        assertEquals(FileMode.TEXT, FileMode.fromString("text"));
        assertEquals(FileMode.BINARY, FileMode.fromString("BINARY"));
        assertEquals(FileMode.BINARY, FileMode.fromString("binary"));
    }

    @Test
    @DisplayName("Chuỗi null hoặc không hợp lệ mặc định về TEXT")
    void testFromStringFallback() {
        assertEquals(FileMode.TEXT, FileMode.fromString(null));
        assertEquals(FileMode.TEXT, FileMode.fromString(""));
        assertEquals(FileMode.TEXT, FileMode.fromString("UNKNOWN"));
    }
}
