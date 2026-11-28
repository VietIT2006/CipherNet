package com.detai10.core.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Thuật toán quét và tìm kiếm tất cả các vị trí xuất hiện của từ khóa trong văn bản.
 * <p>
 * Yêu cầu kỹ thuật:
 * - Tìm kiếm không phân biệt chữ hoa/chữ thường (case-insensitive).
 * - Mặc định tìm từ khóa "HELLO".
 * - Quét lặp toàn bộ vị trí index (0-based) xuất hiện trong văn bản.
 * - Hỗ trợ cả các trường hợp lặp liên tiếp và chồng lấn (overlapping) với bước nhảy {@code fromIndex = foundIndex + 1}.
 * - Xử lý an toàn chuỗi null, chuỗi rỗng (trả về danh sách rỗng).
 * - Tuân thủ Quy tắc 1 (Headless First): Tuyệt đối không import thư viện giao diện AWT/Swing.
 *
 * @author NguyenTuanNhut (CORE-05)
 */
public final class KeywordFinder {

    /** Từ khóa mặc định cần tìm kiếm theo đặc tả đồ án */
    public static final String DEFAULT_KEYWORD = "HELLO";

    /** Mã chuỗi đại diện khi không tìm thấy kết quả trong giao thức mạng */
    public static final String NOT_FOUND_CODE = "NOT_FOUND";

    /** Chuỗi hiển thị thân thiện trên giao diện khi không tìm thấy kết quả */
    public static final String NOT_FOUND_DISPLAY = "Không tìm thấy";

    // Constructor riêng tư ngăn chặn việc khởi tạo thể hiện từ bên ngoài
    private KeywordFinder() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo thể hiện.");
    }

    /**
     * Tìm tất cả các vị trí xuất hiện của từ khóa "HELLO" trong chuỗi văn bản (không phân biệt hoa/thường).
     *
     * @param text Chuỗi văn bản cần quét tìm
     * @return Danh sách các chỉ số index (0-based) xuất hiện từ khóa "HELLO". Trả về danh sách rỗng nếu không tìm thấy hoặc đầu vào null/rỗng.
     */
    public static List<Integer> findHelloIndices(String text) {
        return findKeywordIndices(text, DEFAULT_KEYWORD);
    }

    /**
     * Alias thuận tiện tương thích cho các mô-đun gọi findHelloPositions.
     *
     * @param text Chuỗi văn bản cần quét tìm
     * @return Danh sách các chỉ số index (0-based) xuất hiện từ khóa "HELLO".
     */
    public static List<Integer> findHelloPositions(String text) {
        return findHelloIndices(text);
    }

    /**
     * Alias chuẩn hóa tìm toàn bộ vị trí xuất hiện của từ khóa mặc định "HELLO" (SRV-03).
     *
     * @param text Chuỗi văn bản cần quét tìm
     * @return Danh sách các chỉ số index (0-based) xuất hiện từ khóa "HELLO".
     */
    public static List<Integer> findPositions(String text) {
        return findHelloIndices(text);
    }

    /**
     * Alias chuẩn hóa tìm toàn bộ vị trí xuất hiện của một từ khóa bất kỳ (không phân biệt hoa/thường).
     *
     * @param text    Chuỗi văn bản nguồn
     * @param keyword Từ khóa cần tìm kiếm
     * @return Danh sách các chỉ số index (0-based).
     */
    public static List<Integer> findPositions(String text, String keyword) {
        return findKeywordIndices(text, keyword);
    }

    /**
     * Quét và tìm tất cả các vị trí xuất hiện của một từ khóa bất kỳ trong văn bản (case-insensitive).
     * <p>
     * Thuật toán sử dụng bước nhảy {@code fromIndex = foundIndex + 1} để đảm bảo không bỏ sót bất kỳ lần xuất hiện nào,
     * bao gồm cả các từ khóa lặp dính liền hoặc chồng lấn (overlapping).
     *
     * @param text    Chuỗi văn bản nguồn
     * @param keyword Từ khóa cần tìm kiếm
     * @return Danh sách các chỉ số index (0-based).
     */
    public static List<Integer> findKeywordIndices(String text, String keyword) {
        if (text == null || text.isEmpty() || keyword == null || keyword.isEmpty()) {
            return Collections.emptyList();
        }

        // Chuyển toàn bộ sang chữ hoa theo Locale.ROOT để so khớp không phân biệt hoa thường
        String upperText = text.toUpperCase(Locale.ROOT);
        String upperKeyword = keyword.toUpperCase(Locale.ROOT);

        List<Integer> positions = new ArrayList<>();
        int fromIndex = 0;
        int keywordLen = upperKeyword.length();

        while (fromIndex <= upperText.length() - keywordLen) {
            int foundIndex = upperText.indexOf(upperKeyword, fromIndex);
            if (foundIndex == -1) {
                break;
            }
            positions.add(foundIndex);
            // Bước nhảy +1 để bắt được cả trường hợp overlapping nếu có
            fromIndex = foundIndex + 1;
        }

        return Collections.unmodifiableList(positions);
    }

    /**
     * Định dạng danh sách vị trí thành chuỗi phục vụ đóng gói giao thức UDP.
     * Ví dụ: [0, 15, 32] -> "0, 15, 32".
     * Nếu danh sách rỗng hoặc null, trả về "NOT_FOUND".
     *
     * @param positions Danh sách các index tìm thấy
     * @return Chuỗi phân tách bằng dấu phẩy và khoảng trắng, hoặc "NOT_FOUND" nếu không có.
     */
    public static String formatPositions(List<Integer> positions) {
        if (positions == null || positions.isEmpty()) {
            return NOT_FOUND_CODE;
        }
        return joinPositions(positions);
    }

    /**
     * Định dạng danh sách vị trí thành chuỗi hiển thị thân thiện trên UI.
     * Ví dụ: [12, 45, 102] -> "12, 45, 102".
     * Nếu danh sách rỗng hoặc null, trả về "Không tìm thấy".
     *
     * @param positions Danh sách các index tìm thấy
     * @return Chuỗi các vị trí hoặc "Không tìm thấy".
     */
    public static String formatPositionsDisplay(List<Integer> positions) {
        if (positions == null || positions.isEmpty()) {
            return NOT_FOUND_DISPLAY;
        }
        return joinPositions(positions);
    }

    /**
     * Nối các chỉ số index thành chuỗi phân tách bởi ", ".
     */
    private static String joinPositions(List<Integer> positions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < positions.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(positions.get(i));
        }
        return sb.toString();
    }
}
