# QUY TẮC 2: GIAO THỨC TRUYỀN NHẬN UDP & PHÂN TÁCH AN TOÀN

## Phạm vi áp dụng
Các lớp xử lý mạng và đóng gói gói tin (`UdpProtocol`, `UdpClientService`, `UdpServerWorker`).

## Yêu cầu bắt buộc
1. **Healthcheck Ping/Pong:**
   - Client gửi `PING` -> Server phản hồi `PONG`.
   - Client thiết lập timeout 3 giây (`socket.setSoTimeout(3000)`). Quá 3 giây không có phản hồi phải báo lỗi, không treo giao diện.
2. **Cú pháp gói tin DATA:**
   - Định dạng: `DATA|<mode>|<fileName>|<content>`.
   - Trong đó `<mode>` là `TEXT` hoặc `BINARY`.
   - Dung lượng payload `DATA` phía Client giới hạn an toàn `<= 30.000` bytes UTF-8.
3. **Phân tách an toàn (Safe Delimiter Parsing):**
   - Bắt buộc dùng `split("\\|", 4)` để giới hạn tối đa 4 phần tử:
     ```java
     String[] parts = rawMessage.split("\\|", 4);
     ```
   - Cho phép nội dung `<content>` chứa ký tự phân tách `|` mà không làm hỏng cấu trúc gói tin.
4. **Bộ đệm UDP tiêu chuẩn:** Cấp phát buffer `65.507` bytes (`UdpProtocol.BUFFER_SIZE`), giải mã dữ liệu theo đúng `packet.getOffset()` và `packet.getLength()` với `StandardCharsets.UTF_8`.
