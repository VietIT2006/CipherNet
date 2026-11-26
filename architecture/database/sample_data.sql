-- ========================================================
-- DỮ LIỆU MẪU ĐỀ TÀI 10 (TEST FIXTURES)
-- ========================================================

USE detai10_db;

-- Dữ liệu mẫu bảng sinh_vien
INSERT INTO sinh_vien (ma_sv, ho_ten, lop, email, ngay_sinh) VALUES
('B21DCCN001', 'Nguyễn Văn An', 'D21CQCN01-B', 'an.nv@ptit.edu.vn', '2003-05-15'),
('B21DCCN002', 'Trần Thị Bình', 'D21CQCN01-B', 'binh.tt@ptit.edu.vn', '2003-08-20'),
('B21DCCN003', 'Lê Hoàng Cường', 'D21CQCN02-B', 'cuong.lh@ptit.edu.vn', '2003-12-10');

-- Dữ liệu mẫu bảng lich_su_xu_ly
INSERT INTO lich_su_xu_ly (client_ip, file_name, io_mode, original_content, encrypted_content, hello_positions) VALUES
('127.0.0.1', 'sinhvien_test1.txt', 'TEXT', 'HELLO world this is a test with HELLO keyword', 'RIJVS dztpk drtc sc e mvce amph RIJVS bprpzte', '[0, 32]'),
('127.0.0.1', 'data_binary.bin', 'BINARY', 'No greeting here just simple characters', 'Xy kziipve hsvi nywt gmwxti trrvritovw', 'Khong tim thay tu khoa HELLO');
