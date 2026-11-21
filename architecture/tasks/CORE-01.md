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

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Đóng gói (Serialization):**
    - `createPingPacket()`: Trả về chuỗi `"PING"`.
    - `createPongPacket()`: Trả về chuỗi `"PONG"`.
    - `createDataPacket(String mode, String fileName, String content)`: Trả về `"DATA|" + mode + "|" + fileName + "|" + content`.
    - `createResultPacket(String status, String details)`: Trả về `"RESULT|" + status + "|" + details` (VD: `RESULT|FOUND|0, 15` hoặc `RESULT|NOT_FOUND|Khong tim thay`).
    - `createErrorPacket(String message)`: Trả về `"ERROR|" + message`.
  - **Bóc tách an toàn (Deserialization / Safe Parsing):**
    - Nhận vào mảng byte hoặc chuỗi UTF-8 thô.
    - Kiểm tra loại gói tin bằng tiền tố. Với gói `DATA`, áp dụng phương pháp tách an toàn `rawMessage.split("\\|", 4)`. Nhờ giới hạn 4 phần tử, trường `<content>` có thể chứa bất kỳ ký tự `|` nào mà không làm lệch cấu trúc gói tin.
    - Trả về đối tượng `Packet` chứa các trường tương ứng hoặc trả về `Packet` dạng `ERROR` nếu cú pháp không hợp lệ.
  - **Nguyên tắc thiết kế:** Không import bất kỳ thư viện UI nào (`java.awt.*`, `javax.swing.*`).
