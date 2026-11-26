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

    public ModelHistoryRecord(String clientIp, String fileName, String ioMode,
                              String originalContent, String encryptedContent, String helloPositions) {
        super(clientIp, fileName, ioMode, originalContent, encryptedContent, helloPositions);
    }

    public ModelHistoryRecord(long id, String clientIp, String fileName, String ioMode,
                              String originalContent, String encryptedContent, String helloPositions,
                              Timestamp createdAt) {
        super(id, clientIp, fileName, ioMode, originalContent, encryptedContent, helloPositions, createdAt);
    }
}
