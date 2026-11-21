# CORE-03 — Cài đặt đọc/ghi file chế độ Văn bản (FileHelper – Text Mode)

- **Mã Task:** `CORE-03`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng hàm đọc và ghi file văn bản thuần túy theo chuẩn UTF-8.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/io/FileHelper.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Sử dụng BufferedWriter và BufferedReader. Tự động tạo thư mục cha nếu chưa tồn tại; đảm bảo đóng luồng an toàn bằng try-with-resources.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Ghi nội dung văn bản ra đĩa và đọc lại toàn vẹn nội dung không bị lỗi font.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
