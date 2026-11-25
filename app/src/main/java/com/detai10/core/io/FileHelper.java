package com.detai10.core.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tiện ích đọc/ghi file thuộc tầng Core (Headless).
 *
 * <p>Hỗ trợ chế độ Văn bản (Text Mode) sử dụng bảng mã {@link StandardCharsets#UTF_8} tường minh
 * với {@link BufferedWriter} và {@link BufferedReader}, đảm bảo đóng tài nguyên tự động bằng
 * {@code try-with-resources} và tự động tạo thư mục cha khi ghi file.
 */
public final class FileHelper {

    /** Kích thước bộ đệm ký tự khi đọc file văn bản (bảo toàn mọi ký tự xuống dòng). */
    private static final int READ_BUFFER_SIZE = 4096;

    private FileHelper() {
        // Lớp tiện ích tĩnh, không cho phép khởi tạo.
    }

    /**
     * Ghi toàn bộ nội dung văn bản ra file theo bảng mã {@link StandardCharsets#UTF_8}.
     * Tự động tạo các thư mục cha nếu chưa tồn tại.
     *
     * @param filePath đường dẫn file đích (không được {@code null}).
     * @param content  nội dung chuỗi cần ghi (không được {@code null}).
     * @throws IllegalArgumentException nếu {@code filePath} hoặc {@code content} là {@code null}.
     * @throws IOException              nếu đường dẫn là thư mục hoặc xảy ra lỗi quyền/ghi đĩa.
     */
    public static void writeTextFile(Path filePath, String content) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Đường dẫn file cần ghi không được null.");
        }
        if (content == null) {
            throw new IllegalArgumentException("Nội dung văn bản cần ghi không được null.");
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // Chỉ định tường minh StandardCharsets.UTF_8 để bảo toàn tiếng Việt có dấu trên mọi OS.
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8))) {
                writer.write(content);
                writer.flush();
            }
        } catch (IOException e) {
            throw new IOException("Không thể ghi file văn bản tại '" + filePath + "': " + e.getMessage(), e);
        }
    }

    /**
     * Ghi toàn bộ nội dung văn bản ra {@link File} theo bảng mã {@link StandardCharsets#UTF_8}.
     *
     * @param file    đối tượng {@link File} đích (không được {@code null}).
     * @param content nội dung chuỗi cần ghi (không được {@code null}).
     * @throws IllegalArgumentException nếu {@code file} hoặc {@code content} là {@code null}.
     * @throws IOException              nếu xảy ra lỗi tạo thư mục hoặc ghi đĩa.
     */
    public static void writeTextFile(File file, String content) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File cần ghi không được null.");
        }
        writeTextFile(file.toPath(), content);
    }

    /**
     * Đọc toàn bộ nội dung file văn bản theo bảng mã {@link StandardCharsets#UTF_8},
     * bảo toàn nguyên vẹn ký tự xuống dòng, tab, ký tự đặc biệt và tiếng Việt có dấu.
     *
     * @param filePath đường dẫn file nguồn (không được {@code null}).
     * @return toàn bộ nội dung chuỗi trong file.
     * @throws IllegalArgumentException nếu {@code filePath} là {@code null}.
     * @throws IOException              nếu file không tồn tại, là thư mục hoặc không có quyền đọc.
     */
    public static String readTextFile(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Đường dẫn file cần đọc không được null.");
        }
        if (!Files.exists(filePath)) {
            throw new IOException("Không tìm thấy file văn bản tại '" + filePath + "'.");
        }
        if (Files.isDirectory(filePath)) {
            throw new IOException("Đường dẫn là thư mục, không phải file văn bản: '" + filePath + "'.");
        }

        // Đọc theo khối ký tự bằng BufferedReader để giữ nguyên 100% ký tự xuống dòng (kể cả dòng trống/cuối file).
        StringBuilder content = new StringBuilder();
        char[] buffer = new char[READ_BUFFER_SIZE];
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8))) {
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                content.append(buffer, 0, charsRead);
            }
            return content.toString();
        } catch (IOException e) {
            throw new IOException("Không thể đọc file văn bản tại '" + filePath + "': " + e.getMessage(), e);
        }
    }

    /**
     * Đọc toàn bộ nội dung từ {@link File} theo bảng mã {@link StandardCharsets#UTF_8}.
     *
     * @param file đối tượng {@link File} nguồn (không được {@code null}).
     * @return toàn bộ nội dung chuỗi trong file.
     * @throws IllegalArgumentException nếu {@code file} là {@code null}.
     * @throws IOException              nếu file không tồn tại hoặc xảy ra lỗi I/O khi đọc.
     */
    public static String readTextFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File cần đọc không được null.");
        }
        return readTextFile(file.toPath());
    }
}

