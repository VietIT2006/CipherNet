# Software Requirements Specification (SRS)

## 1. Giới thiệu
Tài liệu Đặc tả Yêu cầu Phần mềm (SRS) cho hệ thống Quản lý Sinh viên / Xử lý dữ liệu Client – Server sử dụng giao thức UDP, mã hóa Vigenère và I/O 2 chế độ.

## 2. Yêu cầu chức năng (Functional Requirements)

### 2.1. Phía Client
- **FR-C01: Thăm dò kết nối (PING/PONG):** Cho phép nhập IP và Port của Server. Gửi gói `PING` và đợi phản hồi `PONG` với timeout 3 giây.
- **FR-C02: Nhập liệu văn bản & Tên file:** Giao diện cho phép nhập chuỗi văn bản bất kỳ và tên file dự kiến lưu trên Server.
- **FR-C03: Chọn chế độ lưu file:** Cho phép người dùng chuyển đổi giữa 2 chế độ:
  - `TEXT`: Định dạng văn bản thường UTF-8.
  - `BINARY`: Định dạng nhị phân có đệm (Data Stream).
- **FR-C04: Gửi gói tin dữ liệu:** Đóng gói payload `DATA|<mode>|<fileName>|<content>` và gửi qua UDP Socket.
- **FR-C05: Nhận & Hiển thị kết quả:** Nhận kết quả từ Server phản hồi (`RESULT|OK|Vi tri HELLO: [...]` hoặc `RESULT|NOT_FOUND|...`) và hiển thị lên giao diện kèm highlight `"HELLO"`.

### 2.2. Phía Server
- **FR-S01: Cấu hình cổng & Khóa Vigenère:** Cho phép người quản trị nhập Port lắng nghe và Khóa giải mã Vigenère trên giao diện trước khi bật Server.
- **FR-S02: Lắng nghe phi đồng bộ (Non-blocking):** Chạy Worker Thread riêng để tiếp nhận các gói tin UDP đến mà không treo giao diện.
- **FR-S03: Phản hồi Healthcheck:** Tự động phản hồi `PONG` ngay khi nhận gói `PING` từ bất kỳ Client nào.
- **FR-S04: Ghi vết Cơ sở dữ liệu:** Ghi nhận thông tin giao dịch vào bảng `lich_su_xu_ly` bao gồm `client_ip`, `file_name`, `io_mode` (`TEXT`/`BINARY`), `original_content`, `encrypted_content`, `hello_positions`, `created_at`.
- **FR-S05: Mã hóa Vigenère:** Mã hóa nội dung văn bản nhận được bằng khóa bí mật. Chỉ biến đổi chữ cái `A-Z`, `a-z`, bảo toàn nguyên vẹn số, dấu cách và ký tự đặc biệt.
- **FR-S06: Lưu & Đọc kiểm chứng File:**
  - Chế độ `TEXT`: Dùng `BufferedWriter` ghi file UTF-8 $\rightarrow$ đọc lại bằng `BufferedReader`.
  - Chế độ `BINARY`: Dùng `DataOutputStream` bọc `BufferedOutputStream` (`writeUTF`) $\rightarrow$ đọc lại bằng `DataInputStream` bọc `BufferedInputStream` (`readUTF`).
- **FR-S07: Giải mã & Tìm kiếm từ khóa:** Giải mã lại nội dung đọc từ file bằng khóa Vigenère trên Server; tìm kiếm toàn bộ các vị trí (index) xuất hiện của từ khóa `"HELLO"`.
- **FR-S08: Gửi phản hồi:** Gửi chuỗi kết quả các vị trí tìm được về lại đúng địa chỉ IP và Port của Client vừa gửi.

## 3. Yêu cầu phi chức năng (Non-Functional Requirements)
- **NFR-01: Hiệu năng:** Thời gian xử lý một gói tin dữ liệu trên Server (bao gồm DB, I/O, Cipher) dưới 200ms trong điều kiện bình thường.
- **NFR-02: Tính sẵn sàng & Đa luồng:** Server có thể tiếp nhận nhiều Client gửi đồng thời mà không bị crash hoặc xung đột tài nguyên.
- **NFR-03: Kiểm thử tự động:** Tầng Core Logic có độ bao phủ Unit Test (JUnit 5) cao, độc lập với Java Swing để chạy tự động trên Linux CI.
- **NFR-04: Đóng gói:** Ứng dụng được đóng gói thành 2 file Fat JAR độc lập (`udp-server.jar`, `udp-client.jar`), có thể chạy trực tiếp qua lệnh `java -jar`.
