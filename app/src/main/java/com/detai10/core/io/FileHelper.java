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
 * Tiện ích đọc và ghi file 2 chế độ thuộc tầng Core (Headless):
 * - TEXT: Luồng ký tự BufferedReader / BufferedWriter với bảng mã chuẩn UTF-8.
 * - BINARY: Luồng nhị phân định kiểu có đệm DataOutputStream / DataInputStream (writeUTF / readUTF).
 * <p>
 * Hỗ trợ cả {@link File}, {@link Path} và {@link FileMode}. Tuân thủ Quy tắc 5.
 */
public final class FileHelper {

    private static final int READ_BUFFER_SIZE = 4096;

    private FileHelper() {
        throw new UnsupportedOperationException("Lớp tiện ích I/O không thể khởi tạo thể hiện.");
    }

    public static void ensureParentDirs(File file) {
        if (file != null && file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

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
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8))) {
                writer.write(content);
                writer.flush();
            }
        } catch (IOException e) {
            throw new IOException("Không thể ghi file văn bản tại '" + filePath + "': " + e.getMessage(), e);
        }
    }

    public static void writeTextFile(File file, String content) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File cần ghi không được null.");
        }
        writeTextFile(file.toPath(), content);
    }

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

    public static String readTextFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File cần đọc không được null.");
        }
        return readTextFile(file.toPath());
    }

    public static void writeBinaryFile(Path filePath, String content) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("Đường dẫn file nhị phân cần ghi không được null.");
        }
        if (content == null) {
            throw new IllegalArgumentException("Nội dung chuỗi cần ghi nhị phân không được null.");
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (DataOutputStream dos = new DataOutputStream(
                    new BufferedOutputStream(new FileOutputStream(filePath.toFile())))) {
                dos.writeUTF(content);
                dos.flush();
            }
        } catch (IOException e) {
            throw new IOException("Không thể ghi file nhị phân tại '" + filePath + "': " + e.getMessage(), e);
        }
    }

    public static void writeBinaryFile(File file, String content) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File nhị phân cần ghi không được null.");
        }
        writeBinaryFile(file.toPath(), content);
    }

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

    public static String readBinaryFile(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("Đối tượng File nhị phân cần đọc không được null.");
        }
        return readBinaryFile(file.toPath());
    }

    public static void writeFile(File file, String content, FileMode mode) throws IOException {
        String safeContent = content != null ? content : "";
        if (mode == FileMode.BINARY) {
            writeBinaryFile(file, safeContent);
        } else {
            writeTextFile(file, safeContent);
        }
    }

    public static String readFile(File file, FileMode mode) throws IOException {
        if (mode == FileMode.BINARY) {
            return readBinaryFile(file);
        } else {
            return readTextFile(file);
        }
    }
}
