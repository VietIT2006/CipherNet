# CORE-05 — Cài đặt thuật toán tìm tất cả vị trí từ HELLO (KeywordFinder)

- **Mã Task:** `CORE-05`
- **Sprint:** 2
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 1 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng thuật toán quét toàn bộ văn bản để tìm tất cả các vị trí xuất hiện của từ khóa HELLO.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/search/KeywordFinder.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Hàm findKeywordIndices(text, "HELLO") trả về List<Integer> đại diện cho các index 0-based. Xử lý trường hợp không tìm thấy và phân biệt/không phân biệt hoa thường theo đặc tả.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Trả về danh sách chính xác các vị trí xuất hiện của HELLO.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
