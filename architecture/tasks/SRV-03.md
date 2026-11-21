# SRV-03 — Tích hợp luồng xử lý gói DATA trên Server

- **Mã Task:** `SRV-03`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 5 SP

---

## 1. Mục tiêu (Goal / User Story)
Kết nối toàn diện quy trình xử lý dữ liệu hoàn chỉnh khi nhận gói DATA từ Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/ServerApp.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Luồng xử lý: Nhận gói -> Lưu DB -> Mã hóa Vigenère -> Ghi file theo mode -> Đọc lại file -> Giải mã bằng Key trên UI -> Tìm vị trí HELLO -> Log UI -> Gửi gói RESULT phản hồi Client.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Toàn bộ chu trình hoạt động chính xác từ lúc nhận gói đến khi phản hồi cho Client.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
