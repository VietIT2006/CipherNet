package com.detai10.core.protocol;

import com.detai10.core.model.FileMode;

import java.util.Objects;

/**
 * Đặc tả và hiện thực các quy chuẩn đóng gói, phân tích gói tin giao tiếp UDP giữa Client và Server.
 * <p>
 * Định dạng các loại gói tin:
 * - Handshake / Healthcheck: {@code PING} &lt;-&gt; {@code PONG}
 * - Gửi dữ liệu: {@code DATA|<mode>|<fileName>|<content>}
 * - Phản hồi thành công có từ khóa: {@code RESULT|FOUND|<positions>}
 * - Phản hồi thành công không có từ khóa: {@code RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO}
 * - Phản hồi lỗi: {@code ERROR|<error_message>}
 * <p>
 * Áp dụng Quy tắc 3: Bóc tách gói tin an toàn (Safe Payload Parsing) với giới hạn {@code split("\\|", 4)}.
 */
public final class UdpProtocol {

    public static final String PING = "PING";
    public static final String PONG = "PONG";
    public static final String PREFIX_DATA = "DATA";
    public static final String PREFIX_RESULT = "RESULT";
    public static final String PREFIX_ERROR = "ERROR";
    public static final String STATUS_FOUND = "FOUND";
    public static final String STATUS_NOT_FOUND = "NOT_FOUND";
    public static final String MSG_NOT_FOUND = "Khong tim thay tu khoa HELLO";

    public static final int BUFFER_SIZE = 8192;
    public static final int DEFAULT_PORT = 9876;

    private UdpProtocol() {
        throw new UnsupportedOperationException("Lớp tiện ích giao thức không thể khởi tạo thể hiện.");
    }

    public static boolean isPing(String message) {
        return PING.equalsIgnoreCase(message != null ? message.trim() : null);
    }

    public static boolean isPong(String message) {
        return PONG.equalsIgnoreCase(message != null ? message.trim() : null);
    }

    public static String createPingMessage() {
        return PING;
    }

    public static String createPongMessage() {
        return PONG;
    }

    public static String createDataMessage(FileMode mode, String fileName, String content) {
        String safeMode = mode != null ? mode.name() : FileMode.TEXT.name();
        String safeFileName = fileName != null ? fileName.trim() : "unnamed.dat";
        String safeContent = content != null ? content : "";
        return PREFIX_DATA + "|" + safeMode + "|" + safeFileName + "|" + safeContent;
    }

    public static String createResultFoundMessage(String positions) {
        return PREFIX_RESULT + "|" + STATUS_FOUND + "|" + (positions != null ? positions : "");
    }

    public static String createResultNotFoundMessage() {
        return PREFIX_RESULT + "|" + STATUS_NOT_FOUND + "|" + MSG_NOT_FOUND;
    }

    public static String createErrorMessage(String errorMessage) {
        return PREFIX_ERROR + "|" + (errorMessage != null ? errorMessage : "Loi khong xac dinh");
    }

    /**
     * Bóc tách an toàn chuỗi payload gói tin DATA thành đối tượng cấu trúc {@link DataPayload}.
     * Sử dụng split("\\|", 4) để cho phép content chứa ký tự phân tách '|'.
     */
    public static DataPayload parseDataPacket(String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Goi tin rong hoac null");
        }

        String[] parts = rawMessage.split("\\|", 4);
        if (parts.length < 4 || !PREFIX_DATA.equalsIgnoreCase(parts[0].trim())) {
            throw new IllegalArgumentException("Sai dinh dang goi tin DATA: phai co dang DATA|<mode>|<fileName>|<content>");
        }

        FileMode mode = FileMode.fromString(parts[1]);
        String fileName = parts[2].trim();
        if (fileName.isEmpty()) {
            throw new IllegalArgumentException("Ten file khong duoc de trong");
        }
        String content = parts[3];

        return new DataPayload(mode, fileName, content);
    }

    /**
     * Đối tượng lưu trữ payload bóc tách từ gói DATA.
     */
    public static class DataPayload {
        private final FileMode mode;
        private final String fileName;
        private final String content;

        public DataPayload(FileMode mode, String fileName, String content) {
            this.mode = mode != null ? mode : FileMode.TEXT;
            this.fileName = fileName;
            this.content = content != null ? content : "";
        }

        public FileMode getMode() {
            return mode;
        }

        public String getFileName() {
            return fileName;
        }

        public String getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DataPayload that = (DataPayload) o;
            return mode == that.mode &&
                    Objects.equals(fileName, that.fileName) &&
                    Objects.equals(content, that.content);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mode, fileName, content);
        }
    }
}
