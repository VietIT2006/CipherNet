# DEP-06 — Xây dựng CD Workflow (java-cd-release.yml)

- **Mã Task:** `DEP-06`
- **Sprint:** 4
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Thiết lập quy trình tự động đóng gói và xuất bản release trên GitHub khi có tag mới.

## 2. File liên quan (Affected Files)
- `.github/workflows/java-cd-release.yml`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
GitHub Actions kích hoạt khi push tag v*, build 2 Fat JAR và tạo GitHub Release đính kèm file JAR.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Tự động tạo Release kèm 2 file .jar khi đẩy tag phiên bản.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
