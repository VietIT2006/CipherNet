# DEP-04 — Thiết lập GitHub Actions CI & Branch Protection

- **Mã Task:** `DEP-04`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Thiết lập quy trình tích hợp liên tục tự động kiểm tra cú pháp, biên dịch và chạy JUnit 5 khi có thay đổi code.

## 2. File liên quan (Affected Files)
- `.github/workflows/java-ci.yml`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Viết workflow chạy trên ubuntu-latest, cài đặt JDK 17, thực thi ./gradlew test, upload test report artifact.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Workflow CI được kích hoạt và chạy xanh thành công khi push/PR vào nhánh develop/main.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
