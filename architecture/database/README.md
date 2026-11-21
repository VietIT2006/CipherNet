# Quản Trị Cơ Sở Dữ Liệu MySQL Local (Database Administration)

> **Mô hình triển khai:** Chạy trực tiếp trên máy cục bộ (**MySQL Localhost:3306**), **không deploy lên Cloud/Server ngoài**.

---

## 1. Giới thiệu
Thư mục chứa script DDL khởi tạo bảng (`schema.sql`) và dữ liệu mẫu kiểm thử (`sample_data.sql`) cho Đề tài số 10 (Vigen / CipherNet).

## 2. Các bảng dữ liệu
1. **`lich_su_xu_ly`:** Bảng nghiệp vụ cốt lõi, ghi nhận mọi giao dịch xử lý gói tin DATA của Client gửi qua socket UDP:
   - `id`: Khóa chính tự tăng
   - `client_ip`: Địa chỉ IP của Client
   - `file_name`: Tên file lưu trên Server
   - `file_mode`: Chế độ `TEXT` hoặc `BINARY`
   - `original_content`: Nội dung gốc Client gửi
   - `encrypted_content`: Nội dung đã mã hóa Vigenère
   - `hello_positions`: Danh sách vị trí xuất hiện từ khóa "HELLO"
   - `created_at`: Thời gian tiếp nhận
2. **`sinh_vien`:** Bảng lưu thông tin sinh viên dự phòng phục vụ bảo vệ đồ án.

## 3. Hướng dẫn khởi tạo trên máy cục bộ

### Cách 1: Chạy file script tự động (Nhanh nhất)
Nhấp đúp chuột hoặc chạy file [scripts/init-db.bat](file:///d:/đề_tài_số_10/scripts/init-db.bat) từ terminal:
```cmd
.\scripts\init-db.bat
```

### Cách 2: Chạy lệnh MySQL CLI
```bash
mysql -u root -p < schema.sql
mysql -u root -p < sample_data.sql
```

## 4. Cấu hình ứng dụng
Thông tin kết nối cục bộ được cấu hình tại:
`app/src/main/resources/database.properties`
