package com.detai10.core.io;

import com.detai10.core.model.FileMode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tiện ích đọc/ghi file thuộc tầng Core (Headless).
 *
 * <p>Hỗ trợ 2 chế độ lưu trữ và đọc/ghi an toàn bằng {@code try-with-resources}:
 * <ul>
 *   <li>Chế độ Văn bản (Text Mode): sử dụng {@link BufferedWriter}/{@link BufferedReader} với bảng mã
 *       {@link StandardCharsets#UTF_8} tường minh.</li>
 *   <li>Chế độ Nhị phân (Binary Mode): sử dụng {@link DataOutputStream}({@link BufferedOutputStream})
 *       và {@link DataInputStream}({@link BufferedInputStream}) với {@code writeUTF}/{@code readUTF}
 *       (định dạng modified UTF-8 kèm tiền tố độ dài 2 byte).</li>
 * </ul>
 */
public final class FileHelper {

    /** Kích thước bộ đệm ký tự khi đọc file văn bản (bảo toàn mọi ký tự xuống dòng). */
    private static final int READ_BUFFER_SIZE = 4096;

    private FileHelper() {
        throw new UnsupportedOperationException("Lớp tiện ích không thể khởi tạo thể hiện.");
    }

    /**
     * Ghi toàn bộ nội dung văn bản ra file theo bảng mã {@link StandardCharsets#UTF_8}.
     * Tự động tạo các thư mục cha nếu chưa tồn tại. Nếu content là null, tự động ghi chuỗi rỗng an toàn.
     *
     * @param filePath đường dẫn file đích (không được {@code null}).
     * @param content  nội dung chuỗi cần ghi (cho phép {@code null}).
     * @throws IllegalArgumentException nếu {@code filePath} là {@code null}.
     * @throws IOException              nếu đường dẫn là thư mục hoặc xảy ra lỗi quyền/ghi đĩa.
     */
    public static void writeTextFile(Path filePath, String content) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Đường dẫn file cần ghi không được null.");
        }
        String safeContent = content != null ? content : "";

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            // Chỉ định tường minh StandardCharsets.UTF_8 để bảo toàn tiếng Việt có dấu trên mọi OS.
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8))) {
                writer.write(safeContent);
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
     * @param content nội dung chuỗi cần ghi (cho phép {@code null}).
     * @throws IllegalArgumentException nếu {@code file} là {@code null}.
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

    /**
     * Ghi chuỗi dữ liệu ra file nhị phân có đệm bằng {@link DataOutputStream}({@link BufferedOutputStream})
     * và {@link DataOutputStream#writeUTF(String)} (modified UTF-8 kèm tiền tố độ dài 2 byte).
     * Tự động tạo các thư mục cha nếu chưa tồn tại. Nếu content là null, tự động ghi chuỗi rỗng an toàn.
     *
     * @param filePath đường dẫn file nhị phân đích (không được {@code null}).
     * @param content  nội dung chuỗi cần ghi (cho phép {@code null}).
     * @throws IllegalArgumentException nếu {@code filePath} là {@code null}.
     * @throws IOException              nếu không tạo được thư mục, đường dẫn là thư mục hoặc lỗi ghi đĩa.
     */
    public static void writeBinaryFile(Path filePath, String content) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Đường dẫn file nhị phân cần ghi không được null.");
        }
        String safeContent = content != null ? content : "";

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(filePath.toFile())))) {
                dos.writeUTF(safeContent);
                dos.flush();
            }
        } catch (IOException e) {
            throw new IOException("Không thể ghi file nhị phân tại '" + filePath + "': " + e.getMessage(), e);
        }
    }

    /**
     * Ghi chuỗi dữ liệu ra {@link File} ở chế độ nhị phân có đệm.
     *
     * @param file    đối tượng {@link File} đích (không được {@code null}).
     * @param content nội dung chuỗi cần ghi (cho phép {@code null}).
     * @throws IllegalArgumentException nếu {@code file} là {@code null}.
     * @throws IOException              nếu xảy ra lỗi tạo thư mục hoặc ghi đĩa.
     */
    public static void writeBinaryFile(File file, String content) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File nhị phân cần ghi không được null.");
        }
        writeBinaryFile(file.toPath(), content);
    }

    /**
     * Đọc chuỗi dữ liệu từ file nhị phân bằng {@link DataInputStream}({@link BufferedInputStream})
     * và {@link DataInputStream#readUTF()}.
     *
     * @param filePath đường dẫn file nhị phân nguồn (không được {@code null}).
     * @return chuỗi dữ liệu gốc đã đọc từ file.
     * @throws IllegalArgumentException nếu {@code filePath} là {@code null}.
     * @throws EOFException             nếu file rỗng hoặc dữ liệu bị cắt cụt trước khi đọc đủ độ dài UTF.
     * @throws IOException              nếu file không tồn tại, là thư mục hoặc lỗi đọc đĩa.
     */
    public static String readBinaryFile(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Đường dẫn file nhị phân cần đọc không được null.");
        }
        if (!Files.exists(filePath)) {
            throw new IOException("Không tìm thấy file nhị phân tại '" + filePath + "'.");
        }
        if (Files.isDirectory(filePath)) {
            throw new IOException("Đường dẫn là thư mục, không phải file nhị phân: '" + filePath + "'.");
        }

        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(filePath.toFile())))) {
            return dis.readUTF();
        } catch (EOFException e) {
            EOFException wrapped = new EOFException(
                    "File nhị phân rỗng hoặc bị cắt cụt (EOF) tại '" + filePath + "'.");
            wrapped.initCause(e);
            throw wrapped;
        } catch (IOException e) {
            throw new IOException("Không thể đọc file nhị phân tại '" + filePath + "': " + e.getMessage(), e);
        }
    }

    /**
     * Đọc chuỗi dữ liệu từ {@link File} ở chế độ nhị phân có đệm.
     *
     * @param file đối tượng {@link File} nguồn (không được {@code null}).
     * @return chuỗi dữ liệu gốc đã đọc từ file.
     * @throws IllegalArgumentException nếu {@code file} là {@code null}.
     * @throws EOFException             nếu gặp cuối file bất ngờ khi đọc.
     * @throws IOException              nếu file không tồn tại hoặc lỗi I/O.
     */
    public static String readBinaryFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File nhị phân cần đọc không được null.");
        }
        return readBinaryFile(file.toPath());
    }

    /**
     * Ghi nội dung file tùy biến theo chế độ {@link FileMode}.
     */
    public static void writeFile(File file, String content, FileMode mode) throws IOException {
        if (mode == FileMode.BINARY) {
            writeBinaryFile(file, content);
        } else {
            writeTextFile(file, content);
        }
    }

    /**
     * Đọc nội dung file tùy biến theo chế độ {@link FileMode}.
     */
    public static String readFile(File file, FileMode mode) throws IOException {
        if (mode == FileMode.BINARY) {
            return readBinaryFile(file);
        } else {
            return readTextFile(file);
        }
    }
}
