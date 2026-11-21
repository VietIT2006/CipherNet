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

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Kết nối JDBC MySQL/SQL Server; hàm insertLog(...) sử dụng PreparedStatement để chống SQL Injection; quản lý kết nối an toàn.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Thực thi thêm mới bản ghi vào bảng lich_su_xu_ly thành công từ Java code.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
