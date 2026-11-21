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

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Luồng lắng nghe không chặn giao diện (Non-blocking):**
    - Chạy trên một `Thread` độc lập. Mở `DatagramSocket` lắng nghe trên Port đã cấu hình với mảng byte buffer kích thước 65507 bytes.
    - Vòng lặp `receive(DatagramPacket packet)` liên tục đón gói tin đến từ các Client.
  - **Phản hồi Healthcheck tức thì:**
    - Khi nhận được nội dung `"PING"`: Ngay lập tức tạo gói `PONG` và gửi ngược lại đúng địa chỉ IP và Port của Client gửi tới.
  - **Cập nhật giao diện an toàn:**
    - Mọi thao tác ghi log lên `JTextArea` hoặc thêm hàng vào `JTable` của `ServerMainFrame` bắt buộc phải được bọc trong `SwingUtilities.invokeLater()`.
  - **Dừng socket an toàn:** Phương thức `stopServer()` đóng socket an toàn, xử lý ngoại lệ `SocketException` mà không làm crash ứng dụng.
