# QUY TẮC 5: CƠ SỞ DỮ LIỆU MYSQL CỤC BỘ & AN TOÀN TRUY VẤN

## Phạm vi áp dụng
Lớp `com.detai10.core.db.DatabaseHelper`, các cấu hình DB và script SQL.

## Yêu cầu bắt buộc
1. **Mô hình triển khai Cục bộ (Local Only):**
   - CSDL MySQL chạy trực tiếp trên máy cục bộ (`localhost:3306`), **không deploy lên cloud**.
   - Database mặc định: `detai10_db` với charset `utf8mb4`.
2. **Chống SQL Injection tuyệt đối (Zero SQL Injection):**
   - Mọi câu lệnh SQL truy vấn và chèn dữ liệu vào bảng `lich_su_xu_ly` và `sinh_vien` **bắt buộc dùng `PreparedStatement` với tham số `?`**.
   - Tuyệt đối cấm cộng chuỗi SQL (`"SELECT ... WHERE id = " + id`).
3. **Bảo mật thông tin đăng nhập:**
   - Thông tin kết nối đặt tại `app/src/main/resources/database.properties`.
   - File này nằm trong `.gitignore`, không commit mật khẩu thật lên Git repository. Chỉ commit file mẫu `database.properties.example`.
4. **Đóng kết nối an toàn:** Dùng `try-with-resources` cho `Connection`, `PreparedStatement` và `ResultSet`.
