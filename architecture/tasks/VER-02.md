# VER-02 — Kiểm chứng Sprint 2 (Core Unit Test)

- **Mã Task:** `VER-02`
- **Sprint:** 2 (M2-core-logic)
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 1 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm chứng toàn bộ các lớp thuộc Core Logic (UdpProtocol, VigenereCipher, FileHelper, KeywordFinder, DatabaseHelper) thông qua kiểm thử tự động.

## 2. File liên quan (Affected Files)
- `architecture/verification/M2-core-logic.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Chạy toàn bộ test suite JUnit 5.
- Đảm bảo tách biệt khỏi Java Swing để chạy ổn định trên Linux Headless CI.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Báo cáo test JUnit 5 đạt 100% pass trên GitHub Actions CI và được lưu lại trong `architecture/verification/M2-core-logic.md`.
