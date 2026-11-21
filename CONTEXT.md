# Bối Cảnh Nghiệp Vụ & Kỹ Thuật (Project Context)

Tài liệu này xác lập ranh giới ngữ cảnh, danh mục thuật ngữ chuẩn và các quyết định kỹ thuật trọng tâm cho Đề tài số 10.

---

## 1. Bối cảnh dự án
Đề tài mang tên danh nghĩa *"Xây dựng chương trình Quản lí Sinh Viên trên mô hình Client – Server bằng Java với giao thức UDP"*, tuy nhiên nội dung kỹ thuật trọng tâm theo yêu cầu của đồ án tập trung vào:
1. Giao tiếp mạng phi kết nối (Connectionless) qua UDP Socket giữa Client và Server.
2. Thuật toán mật mã học cổ điển Vigenère (mã hóa và giải mã theo khóa đối xứng).
3. Thao tác luồng vào/ra (Java I/O) với 2 chế độ: Văn bản (Text) và Nhị phân (Binary luồng định kiểu có đệm).
4. Phân tích văn bản sau giải mã để tìm vị trí xuất hiện của từ khóa `"HELLO"`.
5. Lưu trữ lịch sử giao dịch vào cơ sở dữ liệu quan hệ (RDBMS) qua JDBC.

> [!NOTE]
> Để đáp ứng đầy đủ tên gọi "Quản lý Sinh viên" trong trường hợp Hội đồng bảo vệ yêu cầu, hệ thống cung cấp sẵn bảng `sinh_vien` trong schema dự phòng bên cạnh bảng trọng tâm `lich_su_xu_ly`.

---

## 2. Danh mục thuật ngữ chuẩn (Ubiquitous Language)

| Thuật ngữ | Định nghĩa chuẩn trong dự án | Tránh dùng / Nhầm lẫn |
| :--- | :--- | :--- |
| **Giao thức UDP** | User Datagram Protocol, giao thức tầng Transport không duy trì kết nối, gửi nhận dưới dạng `DatagramPacket`. | Nhầm lẫn với TCP Socket luồng liên tục (InputStream/OutputStream của Socket). |
| **Gói PING / PONG** | Gói tin UDP ngắn dùng để thăm dò trạng thái sẵn sàng của Server trước khi mở phiên làm việc. | Không gọi là "kết nối TCP" vì UDP là connectionless. |
| **Gói DATA** | Chuỗi payload định dạng `DATA\|<mode>\|<fileName>\|<content>` được Client gửi tới Server. | Tránh format không có ký tự phân tách hoặc format JSON cồng kềnh không cần thiết. |
| **Chế độ TEXT** | Chế độ ghi file văn bản dạng ký tự dùng `BufferedWriter`/`BufferedReader` UTF-8. | Nhầm lẫn với ghi nhị phân. |
| **Chế độ BINARY** | Chế độ ghi file nhị phân có cấu trúc dùng luồng định kiểu `DataOutputStream` bọc luồng đệm `BufferedOutputStream` (`writeUTF`). | Tránh ghi byte thô không cấu trúc. |
| **Vigenère Cipher** | Thuật toán mã hóa đa bảng thay thế dựa trên bảng chữ cái Latinh (`A-Z`, `a-z`) và khóa bí mật; bảo toàn số, khoảng trắng và ký hiệu. | Tránh tự chế mã hóa không theo chuẩn Vigenère. |
| **KeywordFinder** | Thuật toán quét và trả về danh sách index (0-based) của tất cả lần xuất hiện của chuỗi `"HELLO"` trong chuỗi giải mã. | Không chỉ dừng lại ở lần xuất hiện đầu tiên. |
| **Bảng lịch sử xử lý** | Bảng `lich_su_xu_ly` trong DB lưu: IP Client, tên file, chế độ, nội dung gốc, nội dung mã hóa, vị trí "HELLO", thời gian. | Tránh lưu thiếu thông tin mã hóa hoặc thiếu IP. |

---

## 3. Ranh giới kiến trúc & Thiết kế thành phần

```
┌────────────────────────────────────────────────────────┐
│                      Client GUI                        │
│   (ConnectFrame: PING/PONG -> ClientMainFrame: DATA)   │
└───────────────────────────┬────────────────────────────┘
                            │ UDP (DatagramPacket)
                            ▼
┌────────────────────────────────────────────────────────┐
│                   Server UDP Worker                    │
│             (DatagramSocket listener thread)           │
├────────────────────────────────────────────────────────┤
│                      Core Logic                        │
│  - UdpProtocol: Parse / serialize packet               │
│  - VigenereCipher: Encrypt / decrypt                   │
│  - FileHelper: Text & Binary I/O                       │
│  - KeywordFinder: Find "HELLO" indices                 │
│  - DatabaseHelper: JDBC DAO (lich_su_xu_ly)            │
├────────────────────────────────────────────────────────┤
│                      Server GUI                        │
│            (ServerMainFrame: Logs, State, Key)         │
└────────────────────────────────────────────────────────┘
```

---

## 4. Quyết định kỹ thuật cốt lõi (ADR)
1. **ADR-01: Tách biệt hoàn toàn tầng Core và GUI**
   - *Lý do:* Cho phép chạy test tự động trên môi trường CI (GitHub Actions) không có màn hình hiển thị (headless Linux).
2. **ADR-02: Cơ chế Worker Thread trên Server**
   - *Lý do:* Vòng lặp `socket.receive()` là thao tác blocking; nếu chạy trên Event Dispatch Thread (EDT) của Swing sẽ gây đơ giao diện.
3. **ADR-03: Ký tự phân tách `|` an toàn trong gói tin**
   - *Lý do:* Sử dụng cú pháp `DATA|<mode>|<fileName>|<content>` với quy tắc phân tách tách tối đa 4 phần (`split("\\|", 4)`), cho phép `<content>` chứa ký tự `|` mà không làm hỏng gói tin.
