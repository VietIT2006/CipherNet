# CORE-07 — Viết bộ Unit Test (JUnit 5) & lớp CoreSelfCheck

- **Mã Task:** `CORE-07`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng bộ kiểm thử tự động toàn diện cho các module Core Logic.

## 2. File liên quan (Affected Files)
- `app/src/test/java/com/detai10/core/**, app/src/main/java/com/detai10/core/CoreSelfCheck.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
JUnit 5 cho UdpProtocolTest, VigenereCipherTest, FileHelperTest (dùng @TempDir), KeywordFinderTest, DatabaseHelperTest. Lớp CoreSelfCheck hỗ trợ chạy kiểm tra nhanh không cần test runner.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] 100% test pass trên Linux Headless CI không có Swing.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Bộ Unit Test tự động (JUnit 5):**
    - `UdpProtocolTest`: Kiểm tra serialize/deserialize gói PING, PONG, DATA (có chứa ký tự `|`), RESULT, ERROR.
    - `VigenereCipherTest`: Kiểm tra mã hóa và giải mã với chữ hoa, chữ thường, số, dấu cách, ký tự đặc biệt, kiểm tra ném lỗi khi key không hợp lệ.
    - `FileHelperTextTest` & `FileHelperBinaryTest`: Sử dụng thư mục tạm `@TempDir` của JUnit 5 để kiểm tra tính toàn vẹn khi ghi và đọc lại file.
    - `KeywordFinderTest`: Kiểm tra chuỗi rỗng, không có HELLO, có 1 HELLO, có nhiều HELLO nằm rải rác hoặc liên tiếp.
    - `DatabaseHelperTest`: Kiểm tra ghi và đọc bản ghi trên H2 in-memory.
  - **Lớp CoreSelfCheck:**
    - Có hàm `main(String[] args)` thực thi nhanh chuỗi kiểm tra độc lập trên terminal không cần UI và không cần Gradle, in ra kết quả `[PASS]` xanh toàn bộ.
