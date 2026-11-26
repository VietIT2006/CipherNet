-- ========================================================
-- CSDL ĐỀ TÀI 10: QUẢN LÝ SINH VIÊN & XỬ LÝ DỮ LIỆU UDP
-- ========================================================

CREATE DATABASE IF NOT EXISTS detai10_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE detai10_db;

-- 1. BẢNG TRỌNG TÂM: Lịch sử xử lý gói tin UDP từ Client (khớp SRS Mục 3.2)
DROP TABLE IF EXISTS lich_su_xu_ly;
CREATE TABLE lich_su_xu_ly (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_ip VARCHAR(45) NOT NULL COMMENT 'Địa chỉ IP của Client gửi gói tin',
    file_name VARCHAR(255) NOT NULL COMMENT 'Tên file lưu trữ trên Server',
    io_mode VARCHAR(10) NOT NULL COMMENT 'Chế độ đọc/ghi: TEXT hoặc BINARY',
    original_content TEXT NOT NULL COMMENT 'Nội dung nguyên bản Client gửi lên',
    encrypted_content TEXT NOT NULL COMMENT 'Nội dung sau khi mã hóa Vigenère',
    hello_positions VARCHAR(500) DEFAULT NULL COMMENT 'Danh sách index của HELLO hoặc thông báo không tìm thấy',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Thời điểm Server tiếp nhận xử lý'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. BẢNG BỔ TRỢ: Quản lý thông tin Sinh viên (Đáp ứng tiêu đề Đề tài)
DROP TABLE IF EXISTS sinh_vien;
CREATE TABLE sinh_vien (
    ma_sv VARCHAR(20) PRIMARY KEY COMMENT 'Mã số sinh viên (VD: B21DCCN001)',
    ho_ten VARCHAR(100) NOT NULL COMMENT 'Họ và tên sinh viên',
    lop VARCHAR(20) NOT NULL COMMENT 'Lớp sinh hoạt',
    email VARCHAR(100) DEFAULT NULL COMMENT 'Email liên hệ',
    ngay_sinh DATE DEFAULT NULL COMMENT 'Ngày sinh'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
