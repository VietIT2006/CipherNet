# CORE-01 — Cài đặt bộ phân tích gói tin UDP (UdpProtocol)

- **Mã Task:** `CORE-01`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 1 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng module đóng gói và bóc tách gói tin giao tiếp UDP giữa Client và Server.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/protocol/UdpProtocol.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Xử lý 2 loại gói: PING/PONG và DATA|<mode>|<fileName>|<content>. Bóc tách an toàn bằng split("\\|", 4) để nội dung có thể chứa ký tự |.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Đóng gói và phân tích chính xác 100% các loại gói tin, Unit Test bao phủ các ca biên.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
