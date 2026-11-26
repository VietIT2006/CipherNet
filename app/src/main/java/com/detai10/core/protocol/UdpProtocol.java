package com.detai10.core.protocol;

import com.detai10.core.model.FileMode;
import com.detai10.core.search.KeywordFinder;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Đặc tả và hiện thực các quy chuẩn đóng gói, phân tích gói tin giao tiếp UDP giữa Client và Server.
 * <p>
 * Định dạng các loại gói tin (khớp SRS Mục 3.1):
 * - Handshake / Healthcheck: {@code PING} &lt;-&gt; {@code PONG}
 * - Gửi dữ liệu: {@code DATA|<mode>|<fileName>|<content>}
 * - Phản hồi thành công có từ khóa: {@code RESULT|OK|Vi tri HELLO: [<indices>]}
 * - Phản hồi không có từ khóa: {@code RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO}
 * - Phản hồi lỗi: {@code ERROR|<error_message>}
 * <p>
 * Tuân thủ Quy tắc 3 (split("\\|", 4)), EX-03 (<= 30.000 bytes UTF-8) và NFR-02 (chặn ../ trong tên file).
 */
public final class UdpProtocol {

    public static final String PING = "PING";
    public static final String PONG = "PONG";
    public static final String PREFIX_DATA = "DATA";
    public static final String PREFIX_RESULT = "RESULT";
    public static final String PREFIX_ERROR = "ERROR";

    public static final String STATUS_OK = "OK";
    public static final String STATUS_NOT_FOUND = "NOT_FOUND";
    public static final String MSG_NOT_FOUND = "Khong tim thay tu khoa HELLO";

    /** Kích thước bộ đệm tối đa an toàn của payload UDP IPv4 (SRS Mục 3.1) */
    public static final int BUFFER_SIZE = 65507;

    /** Giới hạn an toàn dung lượng payload UTF-8 phía Client để tránh lỗi UDP / writeUTF (SRS EX-03) */
    public static final int MAX_PAYLOAD_BYTES = 30000;

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

    /**
     * Kiểm tra tên file hợp lệ và chặn tấn công Path Traversal (../, ..\, ký tự cấm).
     */
    public static String sanitizeFileName(String rawFileName) {
        if (rawFileName == null || rawFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Ten file khong duoc de trong");
        }
        String trimmed = rawFileName.trim();
        if (trimmed.equals(".") || trimmed.equals("..")
                || trimmed.contains("..")
                || trimmed.contains("/")
                || trimmed.contains("\\")
                || trimmed.matches(".*[:*?\"<>|].*")) {
            throw new IllegalArgumentException("Ten file khong hop le hoac chua ky tu cam: " + trimmed);
        }
        return trimmed;
    }

    /**
     * Kiểm tra kích thước chuỗi UTF-8 không vượt quá ngưỡng an toàn 30.000 bytes (SRS EX-03).
     */
    public static void validatePayloadSize(String rawMessage) {
        if (rawMessage == null) {
            throw new IllegalArgumentException("Noi dung goi tin khong duoc null");
        }
        int byteLength = rawMessage.getBytes(StandardCharsets.UTF_8).length;
        if (byteLength > MAX_PAYLOAD_BYTES) {
            throw new IllegalArgumentException(
                    "Dung luong goi tin (" + byteLength + " bytes) vuot qua gioi han an toan " + MAX_PAYLOAD_BYTES + " bytes");
        }
    }

    public static String createDataMessage(FileMode mode, String fileName, String content) {
        FileMode safeMode = mode != null ? mode : FileMode.TEXT;
        String safeFileName = sanitizeFileName(fileName);
        String safeContent = content != null ? content : "";
        String packet = PREFIX_DATA + "|" + safeMode.name() + "|" + safeFileName + "|" + safeContent;
        validatePayloadSize(packet);
        return packet;
    }

    /**
     * Đóng gói thông điệp kết quả theo SRS 3.1: {@code RESULT|OK|Vi tri HELLO: [0, 15]}
     * hoặc {@code RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO} nếu rỗng.
     */
    public static String createResultMessage(List<Integer> positions) {
        if (positions == null || positions.isEmpty()) {
            return createResultNotFoundMessage();
        }
        return PREFIX_RESULT + "|" + STATUS_OK + "|Vi tri HELLO: [" + KeywordFinder.formatPositions(positions) + "]";
    }

    public static String createResultOkMessage(String message) {
        return PREFIX_RESULT + "|" + STATUS_OK + "|" + (message != null ? message : "");
    }

    public static String createResultNotFoundMessage() {
        return PREFIX_RESULT + "|" + STATUS_NOT_FOUND + "|" + MSG_NOT_FOUND;
    }

    public static String createErrorMessage(String errorMessage) {
        return PREFIX_ERROR + "|" + (errorMessage != null ? errorMessage : "Loi khong xac dinh");
    }

    /**
     * Bóc tách an toàn chuỗi payload gói tin DATA thành đối tượng {@link DataPayload}.
     * Sử dụng {@code split("\\|", 4)} để cho phép content chứa ký tự phân tách '|'.
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
        String fileName = sanitizeFileName(parts[2]);
        String content = parts[3];

        return new DataPayload(mode, fileName, content);
    }

    /**
     * Bóc tách gói tin phản hồi {@code RESULT|<status>|<message>} hoặc {@code ERROR|<message>}.
     */
    public static ResponsePayload parseResponsePacket(String rawMessage) {
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Phan hoi tu Server rong hoac null");
        }
        if (rawMessage.startsWith(PREFIX_ERROR + "|")) {
            String[] parts = rawMessage.split("\\|", 2);
            return new ResponsePayload(PREFIX_ERROR, "ERROR", parts.length > 1 ? parts[1] : "");
        }
        if (rawMessage.startsWith(PREFIX_RESULT + "|")) {
            String[] parts = rawMessage.split("\\|", 3);
            if (parts.length < 3) {
                throw new IllegalArgumentException("Goi RESULT khong hop le: " + rawMessage);
            }
            return new ResponsePayload(PREFIX_RESULT, parts[1].trim(), parts[2]);
        }
        throw new IllegalArgumentException("Goi phan hoi khong hop le: " + rawMessage);
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

    /**
     * Đối tượng lưu trữ gói tin phản hồi từ Server (RESULT hoặc ERROR).
     */
    public static class ResponsePayload {
        private final String type;
        private final String status;
        private final String message;

        public ResponsePayload(String type, String status, String message) {
            this.type = type;
            this.status = status;
            this.message = message;
        }

        public String getType() {
            return type;
        }

        public String getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }

        public boolean isError() {
            return PREFIX_ERROR.equalsIgnoreCase(type);
        }

        public boolean isFound() {
            return PREFIX_RESULT.equalsIgnoreCase(type) && STATUS_OK.equalsIgnoreCase(status);
        }
    }
}
