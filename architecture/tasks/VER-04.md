# VER-04 — Kiểm thử tải nhẹ & Nghiệm thu bản Release

- **Mã Task:** `VER-04`
- **Sprint:** 4 (M4-release)
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm thử chịu tải nhẹ khi nhiều Client kết nối gửi dữ liệu đồng thời và nghiệm thu 2 file Fat JAR trên GitHub Release.

## 2. File liên quan (Affected Files)
- `architecture/verification/M4-release.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Chạy 2-3 Client đồng thời gửi liên tục các gói tin lớn nhỏ.
- Xác nhận Server xử lý đa luồng an toàn, không crash, không xung đột I/O.
- Tải trực tiếp `udp-server.jar` và `udp-client.jar` từ GitHub Release để chạy thử nghiệm.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Hồ sơ nghiệm thu bản phát hành hoàn thiện tại `architecture/verification/M4-release.md`.
