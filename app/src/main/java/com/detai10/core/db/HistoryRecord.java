package com.detai10.core.db;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Bản ghi đại diện cho một hàng trong bảng {@code lich_su_xu_ly} của cơ sở dữ liệu.
 * <p>
 * Lưu vết đầy đủ các thông tin của gói tin UDP do Client gửi tới Server:
 * - Địa chỉ IP của Client
 * - Tên file lưu trữ
 * - Chế độ lưu trữ (TEXT hoặc BINARY)
 * - Nội dung nguyên bản Client gửi
 * - Nội dung sau khi mã hóa Vigenère
 * - Danh sách vị trí xuất hiện của từ khóa "HELLO"
 * - Dấu thời gian (Timestamp) xử lý
 */
public class HistoryRecord {

    private int id;
    private String clientIp;
    private String fileName;
    private String fileMode;
    private String originalContent;
    private String encryptedContent;
    private String helloPositions;
    private Timestamp createdAt;

    /**
     * Constructor mặc định không tham số.
     */
    public HistoryRecord() {
    }

    /**
     * Constructor phục vụ lưu mới bản ghi vào CSDL (id và createdAt do CSDL tự động sinh).
     *
     * @param clientIp         Địa chỉ IP Client gửi gói
     * @param fileName         Tên file chỉ định lưu trữ
     * @param fileMode         Chế độ lưu trữ: TEXT hoặc BINARY
     * @param originalContent  Nội dung văn bản gốc
     * @param encryptedContent Nội dung văn bản sau khi mã hóa Vigenère
     * @param helloPositions   Chuỗi danh sách vị trí tìm thấy "HELLO" (VD: "0, 15") hoặc "NOT_FOUND"
     */
    public HistoryRecord(String clientIp, String fileName, String fileMode,
                         String originalContent, String encryptedContent, String helloPositions) {
        this.clientIp = clientIp;
        this.fileName = fileName;
        this.fileMode = fileMode;
        this.originalContent = originalContent;
        this.encryptedContent = encryptedContent;
        this.helloPositions = helloPositions;
    }

    /**
     * Constructor đầy đủ tất cả các trường dữ liệu khi đọc từ CSDL.
     */
    public HistoryRecord(int id, String clientIp, String fileName, String fileMode,
                         String originalContent, String encryptedContent, String helloPositions,
                         Timestamp createdAt) {
        this.id = id;
        this.clientIp = clientIp;
        this.fileName = fileName;
        this.fileMode = fileMode;
        this.originalContent = originalContent;
        this.encryptedContent = encryptedContent;
        this.helloPositions = helloPositions;
        this.createdAt = createdAt;
    }

    // ==================== Getters & Setters ====================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMode() {
        return fileMode;
    }

    public void setFileMode(String fileMode) {
        this.fileMode = fileMode;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getEncryptedContent() {
        return encryptedContent;
    }

    public void setEncryptedContent(String encryptedContent) {
        this.encryptedContent = encryptedContent;
    }

    public String getHelloPositions() {
        return helloPositions;
    }

    public void setHelloPositions(String helloPositions) {
        this.helloPositions = helloPositions;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HistoryRecord that = (HistoryRecord) o;
        return id == that.id &&
                Objects.equals(clientIp, that.clientIp) &&
                Objects.equals(fileName, that.fileName) &&
                Objects.equals(fileMode, that.fileMode) &&
                Objects.equals(originalContent, that.originalContent) &&
                Objects.equals(encryptedContent, that.encryptedContent) &&
                Objects.equals(helloPositions, that.helloPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientIp, fileName, fileMode, originalContent, encryptedContent, helloPositions);
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "id=" + id +
                ", clientIp='" + clientIp + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileMode='" + fileMode + '\'' +
                ", originalContent='" + (originalContent != null && originalContent.length() > 30 ? originalContent.substring(0, 30) + "..." : originalContent) + '\'' +
                ", encryptedContent='" + (encryptedContent != null && encryptedContent.length() > 30 ? encryptedContent.substring(0, 30) + "..." : encryptedContent) + '\'' +
                ", helloPositions='" + helloPositions + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
