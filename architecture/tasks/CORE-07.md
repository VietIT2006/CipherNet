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
