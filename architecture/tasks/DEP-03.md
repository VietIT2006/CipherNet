# DEP-03 — Thiết kế & khởi tạo CSDL

- **Mã Task:** `DEP-03`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Thiết kế lược đồ cơ sở dữ liệu lưu lịch sử xử lý gói tin và dữ liệu sinh viên kiểm thử.

## 2. File liên quan (Affected Files)
- `architecture/database/schema.sql, sample_data.sql, README.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Tạo bảng lich_su_xu_ly lưu IP Client, tên file, chế độ, nội dung gốc, nội dung mã hóa, vị trí HELLO, thời gian; bảng bổ trợ sinh_vien. Hỗ trợ UTF-8 (utf8mb4).

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Chạy thành công schema.sql và sample_data.sql trên MySQL/SQL Server không lỗi.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
