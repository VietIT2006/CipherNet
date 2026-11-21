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

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Xây dựng file cấu hình CI `.github/workflows/java-ci.yml`.
  - Tự động kích hoạt khi có `push` hoặc `pull_request` vào nhánh `main`.
  - Môi trường thực thi: Ubuntu Latest (Headless Runner), cài đặt JDK 17 Eclipse Temurin.
  - Các bước thực thi: Checkout code, cấp quyền thực thi cho `./gradlew`, biên dịch mã nguồn, chạy toàn bộ Unit Test bằng `./gradlew test --no-daemon`, tải lên Test Report Artifacts.
  - Đảm bảo kiểm thử headless không phụ thuộc vào màn hình hiển thị đồ họa.
