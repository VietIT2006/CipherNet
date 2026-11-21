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

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Xây dựng quy trình tự động phân phối (Continuous Deployment - CD).
  - **Sự kiện kích hoạt (Trigger):** Tự động kích hoạt khi có Git Tag mới theo định dạng phiên bản `v*.*.*` (ví dụ: `v1.0.0`).
  - **Các bước thực thi:**
    1. Checkout source code.
    2. Thiết lập môi trường JDK 17 Eclipse Temurin.
    3. Biên dịch và đóng gói 2 Fat JAR bằng lệnh `./gradlew serverJar clientJar`.
    4. Sử dụng GitHub Action `softprops/action-gh-release` để tự động tạo một GitHub Release mới tương ứng với tag, đính kèm 2 file `udp-server.jar` và `udp-client.jar`.
