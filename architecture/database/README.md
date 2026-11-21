# Quản Trị Cơ Sở Dữ Liệu (Database Administration)

## 1. Giới thiệu
Thư mục chứa script DDL khởi tạo bảng (`schema.sql`) và dữ liệu mẫu kiểm thử (`sample_data.sql`) cho Đề tài số 10.

## 2. Các bảng dữ liệu
1. **`lich_su_xu_ly`:** Bảng nghiệp vụ cốt lõi, ghi nhận mọi giao dịch xử lý gói tin DATA của Client gửi qua socket UDP.
2. **`sinh_vien`:** Bảng lưu thông tin sinh viên phục vụ bảo vệ đồ án và mở rộng tính năng nghiệp vụ.

## 3. Hướng dẫn khởi tạo
```bash
# Đăng nhập MySQL
mysql -u root -p

# Thực thi lệnh
mysql -u root -p < schema.sql
mysql -u root -p < sample_data.sql
```
