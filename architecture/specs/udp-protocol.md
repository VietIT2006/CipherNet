# Đặc Tả Giao Thức UDP (UDP Protocol Specification)

## 1. Giới thiệu tổng quan
Giao thức trao đổi dữ liệu trong dự án được thiết kế chạy trên nền giao vận UDP (`java.net.DatagramSocket` và `java.net.DatagramPacket`). Vì UDP là giao thức không hướng kết nối (connectionless) và không có cơ chế bắt tay 3 bước như TCP, ứng dụng thiết kế cơ chế kiểm tra tính sẵn sàng ở tầng ứng dụng bằng gói tin `PING`/`PONG`.

---

## 2. Các loại gói tin

### 2.1. Gói tin Kiểm tra kết nối (PING / PONG)
Dùng để xác thực Server đang hoạt động và sẵn sàng nhận dữ liệu.

| Hướng gửi | Payload (Chuỗi UTF-8) | Mô tả |
| :--- | :--- | :--- |
| **Client $\rightarrow$ Server** | `PING` | Client gửi gói tin có nội dung "PING" tới địa chỉ IP và Port của Server. |
| **Server $\rightarrow$ Client** | `PONG` | Server phản hồi lại đúng địa chỉ IP và Port vừa nhận được gói PING. |

- **Xử lý Timeout:** Client đặt `socket.setSoTimeout(3000)` (3 giây). Nếu không nhận được `PONG` trong 3 giây, xem như kết nối thất bại và hiển thị thông báo lỗi.

---

### 2.2. Gói tin Dữ liệu (DATA)
Dùng khi Client gửi dữ liệu cần xử lý lên Server.

**Cú pháp:**
```text
DATA|<mode>|<fileName>|<content>
```

**Chi tiết trường dữ liệu:**
1. `DATA`: Header định danh loại gói tin.
2. `<mode>`: Chế độ xử lý và lưu file trên Server:
   - `TEXT`: Ghi và đọc bằng luồng ký tự (`BufferedWriter`/`BufferedReader` UTF-8).
   - `BINARY`: Ghi và đọc bằng luồng nhị phân định kiểu có đệm (`DataOutputStream`/`DataInputStream` với `writeUTF`).
3. `<fileName>`: Tên file lưu trên máy chủ (ví dụ: `sinhvien.txt`, `data.bin`).
4. `<content>`: Chuỗi nội dung văn bản do người dùng nhập.

**Quy tắc phân tách an toàn (Safe Parsing):**
Để tránh lỗi khi `<content>` chứa ký tự `|`, phía nhận thực hiện split giới hạn tối đa 4 phần tử:
```java
String[] parts = rawMessage.split("\\|", 4);
// parts[0] = "DATA"
// parts[1] = mode
// parts[2] = fileName
// parts[3] = content
```

---

### 2.3. Gói tin Kết quả phản hồi (RESULT / ERROR)
Dùng khi Server xử lý xong và phản hồi về cho Client.

**Cú pháp thành công:**
```text
RESULT|OK|<message>
# Ví dụ: RESULT|OK|Vi tri HELLO: [0, 15]
hoặc:
RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO
```

**Cú pháp lỗi:**
```text
ERROR|<error_message>
# Ví dụ: ERROR|Sai dinh dang goi tin DATA
```

---

## 3. Kích thước bộ đệm (Buffer Size)
- Kích thước bộ đệm chuẩn cho `DatagramPacket`: `65,507 bytes` (giới hạn tối đa payload an toàn của UDP trên IPv4) hoặc bộ đệm khuyến nghị `8192 bytes` cho các gói tin văn bản thông thường.
- Mã hóa ký tự: Toàn bộ chuỗi được chuyển đổi qua mảng byte sử dụng chuẩn `StandardCharsets.UTF_8`.
