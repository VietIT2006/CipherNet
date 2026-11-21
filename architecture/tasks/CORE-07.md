# CORE-07 — Viết bộ Unit Test JUnit 5 cho toàn bộ tầng Core

- **Mã Task:** `CORE-07`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 4 (Server / Network)

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng bộ kiểm thử đơn vị tự động bằng JUnit 5 cho tất cả các lớp trong tầng Core Logic, đảm bảo độ bao phủ cao và chạy độc lập trên môi trường Linux Headless CI.

## 2. File liên quan (Affected Files)
- `app/src/test/java/com/detai10/core/protocol/UdpProtocolTest.java`
- `app/src/test/java/com/detai10/core/cipher/VigenereCipherTest.java`
- `app/src/test/java/com/detai10/core/io/FileHelperTextTest.java`
- `app/src/test/java/com/detai10/core/io/FileHelperBinaryTest.java`
- `app/src/test/java/com/detai10/core/search/KeywordFinderTest.java`
- `app/src/test/java/com/detai10/core/db/DatabaseHelperTest.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Kiểm thử toàn diện các ca biên: Chuỗi rỗng, null, ký tự đặc biệt, tiếng Việt UTF-8.
- Sử dụng `@TempDir` của JUnit 5 cho các test đọc/ghi file để tự động xóa sạch file rác sau khi chạy.
- Sử dụng CSDL H2 in-memory cho `DatabaseHelperTest` để không phụ thuộc vào dịch vụ CSDL ngoài.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Chạy lệnh `./gradlew test` xanh 100%.
- [ ] Tỷ lệ bao phủ kiểm thử (Test Coverage) tầng Core đạt trên 85%.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Nền tảng đảm bảo chất lượng phần mềm theo tôn chỉ Spec-driven: Mọi logic nghiệp vụ cốt lõi đều được tự động xác minh trước khi bước vào giai đoạn ghép nối giao diện.
