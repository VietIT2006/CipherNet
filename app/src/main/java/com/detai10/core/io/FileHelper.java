package com.detai10.core.io;

import com.detai10.core.model.FileMode;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Tiện ích đọc và ghi file 2 chế độ:
 * - TEXT: Luồng ký tự BufferedReader / BufferedWriter với bảng mã chuẩn UTF-8.
 * - BINARY: Luồng nhị phân định kiểu có đệm DataOutputStream / DataInputStream (writeUTF / readUTF).
 * <p>
 * Tuân thủ Quy tắc 5 (Bảo toàn toàn vẹn dữ liệu trong File I/O).
 */
public final class FileHelper {

    private FileHelper() {
        throw new UnsupportedOperationException("Lớp tiện ích I/O không thể khởi tạo thể hiện.");
    }

    public static void ensureParentDirs(File file) {
        if (file != null && file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
    }

    public static void writeTextFile(File file, String content) throws IOException {
        ensureParentDirs(file);
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            if (content != null) {
                writer.write(content);
            }
            writer.flush();
        }
    }

    public static String readTextFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            char[] buffer = new char[4096];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
        }
        return sb.toString();
    }

    public static void writeBinaryFile(File file, String content) throws IOException {
        ensureParentDirs(file);
        try (DataOutputStream dos = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            dos.writeUTF(content != null ? content : "");
            dos.flush();
        }
    }

    public static String readBinaryFile(File file) throws IOException {
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            return dis.readUTF();
        }
    }

    public static void writeFile(File file, String content, FileMode mode) throws IOException {
        if (mode == FileMode.BINARY) {
            writeBinaryFile(file, content);
        } else {
            writeTextFile(file, content);
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
