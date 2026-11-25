package com.detai10.core.model;

import com.detai10.core.db.HistoryRecord;

import java.sql.Timestamp;

/**
 * Lớp mở rộng thuận tiện tương thích cho các mô-đun trong package model.
 */
public class ModelHistoryRecord extends HistoryRecord {

    public ModelHistoryRecord() {
        super();
    }

    public ModelHistoryRecord(String clientIp, String fileName, String fileMode,
                              String originalContent, String encryptedContent, String helloPositions) {
        super(clientIp, fileName, fileMode, originalContent, encryptedContent, helloPositions);
    }

    public ModelHistoryRecord(int id, String clientIp, String fileName, String fileMode,
                              String originalContent, String encryptedContent, String helloPositions,
                              Timestamp createdAt) {
        super(id, clientIp, fileName, fileMode, originalContent, encryptedContent, helloPositions, createdAt);
    }
}
