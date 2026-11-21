# VER-03 — Kiểm chứng Sprint 3 (Client – Server E2E)

- **Mã Task:** `VER-03`
- **Sprint:** 3 (M3-udp-app)
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm chứng luồng tích hợp đầu-cuối (End-to-End) giữa Client và Server trên giao thức UDP.

## 2. File liên quan (Affected Files)
- `architecture/verification/M3-udp-app.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Kiểm thử các kịch bản: Server bật/tắt, Client gửi PING nhận PONG, Client gửi DATA với cả 2 chế độ TEXT và BINARY.
- Kiểm tra tính nhất quán dữ liệu được ghi vào CSDL và file trên đĩa.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Bằng chứng kiểm thử E2E thực tế được lưu vào `architecture/verification/M3-udp-app.md`.
