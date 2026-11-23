package com.detai10.core.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bộ Unit Test bao phủ 100% kịch bản cho KeywordFinder (CORE-05).
 */
class KeywordFinderTest {

    @Test
    @DisplayName("Chuỗi null hoặc rỗng trả về danh sách rỗng")
    void testNullOrEmptyInput() {
        assertTrue(KeywordFinder.findHelloIndices(null).isEmpty());
        assertTrue(KeywordFinder.findHelloIndices("").isEmpty());
        assertTrue(KeywordFinder.findKeywordIndices(null, "HELLO").isEmpty());
        assertTrue(KeywordFinder.findKeywordIndices("Hello", null).isEmpty());
        assertTrue(KeywordFinder.findKeywordIndices("Hello", "").isEmpty());
    }

    @Test
    @DisplayName("Văn bản không chứa từ khóa HELLO trả về danh sách rỗng")
    void testKeywordNotFound() {
        List<Integer> result = KeywordFinder.findHelloIndices("Lap trinh mang voi giao thuc UDP va Java.");
        assertTrue(result.isEmpty());
        assertEquals(KeywordFinder.NOT_FOUND_CODE, KeywordFinder.formatPositions(result));
        assertEquals(KeywordFinder.NOT_FOUND_DISPLAY, KeywordFinder.formatPositionsDisplay(result));
    }

    @ParameterizedTest
    @ValueSource(strings = {"hello", "HELLO", "Hello", "HeLLo", "hElLo", "hellO"})
    @DisplayName("Tìm thấy từ khóa không phân biệt chữ hoa, chữ thường")
    void testCaseInsensitivity(String variant) {
        String text = "Xin chao, " + variant + " the gioi!";
        List<Integer> result = KeywordFinder.findHelloIndices(text);
        assertEquals(List.of(10), result);
        assertEquals("10", KeywordFinder.formatPositions(result));
    }

    @Test
    @DisplayName("Tìm thấy từ khóa ở đầu chuỗi (index 0)")
    void testKeywordAtStart() {
        String text = "HELLO world, nice to meet you.";
        List<Integer> result = KeywordFinder.findHelloIndices(text);
        assertEquals(List.of(0), result);
    }

    @Test
    @DisplayName("Tìm thấy từ khóa ở cuối chuỗi")
    void testKeywordAtEnd() {
        String text = "Please say hello";
        List<Integer> result = KeywordFinder.findHelloIndices(text);
        assertEquals(List.of(11), result);
    }

    @Test
    @DisplayName("Tìm thấy nhiều lần xuất hiện tại các vị trí khác nhau")
    void testMultipleOccurrences() {
        // Index:        0123456789012345678901234567890123456
        // Text:         Hello there! HELLO friend, say hello.
        // Positions:    0            13                31
        String text = "Hello there! HELLO friend, say hello.";
        List<Integer> result = KeywordFinder.findHelloIndices(text);
        assertEquals(List.of(0, 13, 31), result);
        assertEquals("0, 13, 31", KeywordFinder.formatPositions(result));
        assertEquals("0, 13, 31", KeywordFinder.formatPositionsDisplay(result));
    }

    @Test
    @DisplayName("Xử lý từ khóa lặp dính liền liên tiếp (HELLOHELLO)")
    void testConsecutiveRepetitions() {
        String text = "HELLOHELLO";
        List<Integer> result = KeywordFinder.findHelloIndices(text);
        assertEquals(List.of(0, 5), result);
        assertEquals("0, 5", KeywordFinder.formatPositions(result));
    }

    @Test
    @DisplayName("Xử lý từ khóa nằm lồng bên trong một từ khác (Othello)")
    void testEmbeddedInsideOtherWords() {
        String text = "Othello is reading hello today.";
        // Othello: index 2 là "hello"
        // hello: index 19
        List<Integer> result = KeywordFinder.findHelloIndices(text);
        assertEquals(List.of(2, 19), result);
    }

    @Test
    @DisplayName("Kiểm tra thuật toán tìm từ khóa chồng lấn (Overlapping pattern)")
    void testOverlappingKeyword() {
        // Từ khóa "ABA" trong "ABABA" xuất hiện tại index 0 và 2
        List<Integer> result = KeywordFinder.findKeywordIndices("ABABA", "ABA");
        assertEquals(List.of(0, 2), result);
        assertEquals("0, 2", KeywordFinder.formatPositions(result));
    }

    @Test
    @DisplayName("Định dạng chuỗi kết quả formatPositions và formatPositionsDisplay")
    void testFormattingMethods() {
        assertEquals("NOT_FOUND", KeywordFinder.formatPositions(null));
        assertEquals("NOT_FOUND", KeywordFinder.formatPositions(Collections.emptyList()));
        assertEquals("Không tìm thấy", KeywordFinder.formatPositionsDisplay(null));
        assertEquals("Không tìm thấy", KeywordFinder.formatPositionsDisplay(Collections.emptyList()));

        List<Integer> positions = Arrays.asList(12, 45, 102);
        assertEquals("12, 45, 102", KeywordFinder.formatPositions(positions));
        assertEquals("12, 45, 102", KeywordFinder.formatPositionsDisplay(positions));
    }

    @Test
    @DisplayName("Kiểm tra findHelloPositions alias hoạt động đồng nhất")
    void testAliasFindHelloPositions() {
        String text = "Say Hello to everyone";
        assertEquals(KeywordFinder.findHelloIndices(text), KeywordFinder.findHelloPositions(text));
    }
}
