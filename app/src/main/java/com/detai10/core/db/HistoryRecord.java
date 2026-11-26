package com.detai10.core.db;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Bản ghi đại diện cho một hàng trong bảng {@code lich_su_xu_ly} của cơ sở dữ liệu (khớp SRS Mục 3.2).
 */
public class HistoryRecord {

    private long id;
    private String clientIp;
    private String fileName;
    private String ioMode;
    private String originalContent;
    private String encryptedContent;
    private String helloPositions;
    private Timestamp createdAt;

    public HistoryRecord() {
    }

    public HistoryRecord(String clientIp, String fileName, String ioMode,
                         String originalContent, String encryptedContent, String helloPositions) {
        this.clientIp = clientIp;
        this.fileName = fileName;
        this.ioMode = ioMode;
        this.originalContent = originalContent;
        this.encryptedContent = encryptedContent;
        this.helloPositions = helloPositions;
    }

    public HistoryRecord(long id, String clientIp, String fileName, String ioMode,
                         String originalContent, String encryptedContent, String helloPositions,
                         Timestamp createdAt) {
        this.id = id;
        this.clientIp = clientIp;
        this.fileName = fileName;
        this.ioMode = ioMode;
        this.originalContent = originalContent;
        this.encryptedContent = encryptedContent;
        this.helloPositions = helloPositions;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getIoMode() {
        return ioMode;
    }

    public void setIoMode(String ioMode) {
        this.ioMode = ioMode;
    }

    /** Alias tương thích cho tên thuộc tính cũ. */
    public String getFileMode() {
        return ioMode;
    }

    /** Alias tương thích cho tên thuộc tính cũ. */
    public void setFileMode(String fileMode) {
        this.ioMode = fileMode;
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
                Objects.equals(ioMode, that.ioMode) &&
                Objects.equals(originalContent, that.originalContent) &&
                Objects.equals(encryptedContent, that.encryptedContent) &&
                Objects.equals(helloPositions, that.helloPositions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientIp, fileName, ioMode, originalContent, encryptedContent, helloPositions);
    }

    @Override
    public String toString() {
        return "HistoryRecord{" +
                "id=" + id +
                ", clientIp='" + clientIp + '\'' +
                ", fileName='" + fileName + '\'' +
                ", ioMode='" + ioMode + '\'' +
                ", helloPositions='" + helloPositions + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
