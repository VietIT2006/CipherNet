# SRV-02 — Xây dựng luồng nền lắng nghe UDP (UdpServerWorker)

- **Mã Task:** `SRV-02`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng Worker Thread chuyên trách việc lắng nghe socket UDP và phân phối gói tin.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
DatagramSocket.receive() chạy trên luồng phụ để không làm đơ giao diện; phản hồi gói PONG tức thì khi nhận PING; cập nhật UI qua SwingUtilities.invokeLater.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Server lắng nghe ổn định, phản hồi PONG tức thời, giao diện không bị giật lag.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
