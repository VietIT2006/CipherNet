# CORE-06 — Xây dựng tầng thao tác CSDL (DatabaseHelper)

- **Mã Task:** `CORE-06`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Hiện thực tầng truy xuất dữ liệu JDBC để ghi log xử lý gói tin của Server.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`
- `app/src/main/java/com/detai10/core/model/HistoryRecord.java`
- `app/src/test/java/com/detai10/core/db/DatabaseHelperTest.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Kết nối JDBC MySQL/SQL Server; hàm insertLog(...) sử dụng PreparedStatement để chống SQL Injection; quản lý kết nối an toàn.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Thực thi thêm mới bản ghi vào bảng lich_su_xu_ly thành công từ Java code.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Khởi tạo và cấu hình CSDL:**
    - Hỗ trợ kết nối CSDL H2 Database (In-memory/File-based) cho môi trường kiểm thử/chạy độc lập, và MySQL cho môi trường sản phẩm.
    - Tự động chạy DDL tạo bảng `lich_su_xu_ly` và `sinh_vien` nếu chưa tồn tại (`initDatabase()`).
  - **Ghi lịch sử (`saveHistory(HistoryRecord record)`):**
    - Chèn bản ghi lịch sử xử lý gói tin gồm: `client_ip`, `file_name`, `file_mode`, `original_content`, `encrypted_content`, `hello_positions`.
    - **Quy tắc an toàn tuyệt đối:** Sử dụng 100% `PreparedStatement` với tham số `?` để ngăn chặn hoàn toàn nguy cơ SQL Injection.
  - **Truy vấn lịch sử (`getAllHistory()` / `getRecentHistory(int limit)`):**
    - Đọc danh sách lịch sử sắp xếp giảm dần theo thời gian (`ORDER BY created_at DESC`).
    - Quản lý tài nguyên an toàn bằng `try-with-resources` cho `Connection`, `PreparedStatement` và `ResultSet`.
