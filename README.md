# ĐỀ TÀI 10: HỆ THỐNG CLIENT–SERVER TRUYỀN NHẬN DỮ LIỆU MÃ HÓA QUA GIAO THỨC UDP (JAVA)

> **Dự án:** Vigen (CipherNet) — Ứng dụng Client–Server truyền nhận dữ liệu qua giao thức UDP, tích hợp mã hóa Vigenère, lưu trữ tệp tin theo hai chế độ (Văn bản và Nhị phân) và quản trị lịch sử qua JDBC.  
> **Phương pháp phát triển:** Quy trình Spec-driven & AI-ready Development (chuẩn Clucky).

---

## 1. Tổng quan dự án

Hệ thống được xây dựng theo mô hình Client–Server phân tán trên nền tảng Java Socket với giao thức phi kết nối UDP (`DatagramSocket`, `DatagramPacket`). Client thực hiện thăm dò trạng thái sẵn sàng của Server bằng cơ chế `PING`/`PONG`, sau đó gửi văn bản kèm tên tệp tin và chế độ lưu trữ tùy chọn. Server tiếp nhận gói tin trên luồng nền, mã hóa nội dung bằng thuật toán Vigenère, ghi và đọc lại tệp tin để kiểm chứng tính toàn vẹn dữ liệu, giải mã, tìm tất cả vị trí xuất hiện của từ khóa `"HELLO"`, ghi nhận lịch sử giao dịch vào cơ sở dữ liệu thông qua JDBC và gửi kết quả phản hồi về Client.

### Đặc điểm kiến trúc nổi bật
- **Phát triển hướng đặc tả (Spec-driven & AI-ready):** Toàn bộ tài liệu yêu cầu, giao thức, sơ đồ kiến trúc và danh mục tác vụ được quy hoạch tại thư mục `architecture/` (trạng thái chỉ đọc — bất biến) trước khi triển khai mã nguồn.
- **Tách biệt tầng xử lý nghiệp vụ và giao diện (Headless First):** Các gói `com.detai10.core.*` và `com.detai10.client.net.*` hoàn toàn độc lập với thư viện giao diện (`java.awt.*`, `javax.swing.*`), bảo đảm kiểm thử tự động JUnit 5 thực thi ổn định trên môi trường CI Linux Headless.
- **Xử lý bất đồng bộ và an toàn đa luồng:** Server sử dụng luồng nền `UdpServerWorker` kết hợp nhóm luồng xử lý song song và đồng bộ hóa thao tác tệp theo tên; Client sử dụng `SwingWorker` để không gây treo giao diện người dùng (EDT).

---

## 2. Công nghệ sử dụng (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Chi tiết sử dụng |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java 17 LTS (tương thích JDK 17+) | Chuẩn biên dịch thống nhất toàn dự án (`--release 17`) |
| **Công cụ build** | Gradle Wrapper (`9.7.1`) | Quản lý phụ thuộc, biên dịch, kiểm thử và đóng gói Fat JAR |
| **Giao diện** | Java Swing | `ServerMainFrame`, `ConnectFrame`, `ClientMainFrame` |
| **Mạng** | Java UDP Socket | `DatagramSocket`, `DatagramPacket` (bộ đệm 65.507 bytes, chuẩn UTF-8) |
| **I/O File** | Java I/O (2 chế độ) | `BufferedWriter`/`BufferedReader` (`TEXT` UTF-8) và `DataOutputStream`/`DataInputStream` bọc đệm (`BINARY` `writeUTF`/`readUTF`) |
| **Mã hóa** | Thuật toán Vigenère | Dịch chuyển chữ cái `[A-Za-z]` theo modulo 26; bảo toàn chữ số, khoảng trắng và ký hiệu |
| **Cơ sở dữ liệu** | JDBC (MySQL 8.0+ / H2 Fallback) | Lưu lịch sử xử lý vào bảng `lich_su_xu_ly` qua `PreparedStatement` |
| **Kiểm thử** | JUnit 5 & JaCoCo | Kiểm thử đơn vị, kiểm thử tải đồng thời và kiểm thử E2E bằng `DatagramSocket` thực |
| **CI/CD** | GitHub Actions | Tự động kiểm thử Headless, phân tích bảo mật CodeQL và phát hành Fat JAR (`udp-server.jar`, `udp-client.jar`) |

---

## 3. Đặc tả giao thức UDP (UDP Protocol)

Giao thức tầng ứng dụng trao đổi gói tin giữa Client và Server sử dụng bảng mã `StandardCharsets.UTF_8`:

### 3.1. Gói tin kiểm tra kết nối (`PING` / `PONG`)
- **Client gửi Server:** `PING`
- **Server phản hồi Client:** `PONG`
- **Cơ chế:** Client thiết lập `socket.setSoTimeout(3000)` (3 giây). Nếu nhận được `PONG` trong thời hạn, giao diện chuyển từ `ConnectFrame` sang `ClientMainFrame`; nếu hết thời gian chờ (`SocketTimeoutException`), hiển thị thông báo lỗi và cho phép thử lại.

### 3.2. Gói tin dữ liệu (`DATA`) và phản hồi (`RESULT` / `ERROR`)
- **Cú pháp gửi:**  
  `DATA|<mode>|<fileName>|<content>`
  - `<mode>`: Chế độ lưu trữ tệp (`TEXT` hoặc `BINARY`).
  - `<fileName>`: Tên tệp hợp lệ lưu trên Server (bị từ chối nếu chứa `..`, `/`, `\` hoặc ký tự cấm).
  - `<content>`: Nội dung văn bản nguồn (giới hạn an toàn tối đa `30.000` bytes UTF-8 phía Client; cho phép chứa ký tự `|` nhờ bóc tách `split("\\|", 4)`).
- **Cú pháp phản hồi:**
  - Tìm thấy từ khóa: `RESULT|OK|Vi tri HELLO: [<indices>]`
  - Không tìm thấy từ khóa: `RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO`
  - Lỗi xử lý hoặc sai định dạng: `ERROR|<error_message>`

---

## 4. Luồng xử lý dữ liệu tại Server

```text
[Client] Gui goi tin DATA|<mode>|<fileName>|<content>
       |
       v (UDP DatagramPacket)
[Server] UdpServerWorker tiep nhan goi tin tren luong nen
       |
       +--> 1. Boc tach an toan bang UdpProtocol.parseDataPacket() (split("\\|", 4))
       |
       +--> 2. Ma hoa noi dung bang VigenereCipher.encrypt(content, serverKey)
       |
       +--> 3. Dong bo theo ten tep va ghi xuong server_storage/:
       |         - TEXT: BufferedWriter / BufferedReader (UTF-8)
       |         - BINARY: DataOutputStream / DataInputStream boc BufferedStream (writeUTF/readUTF)
       |
       +--> 4. Doc nguoc lai ban ma tu tep tren dia de kiem chung toan ven
       |
       +--> 5. Giai ma noi dung doc tu dia bang VigenereCipher.decrypt()
       |
       +--> 6. Tim tat ca vi tri xuat hien cua tu khoa "HELLO" (KeywordFinder)
       |
       +--> 7. Luu lich su giao dich vao CSDL bang lich_su_xu_ly (PreparedStatement)
       |
       +--> 8. Cap nhat giao dien Server (SwingUtilities.invokeLater) va gui RESULT ve Client
```

---

## 5. Cấu trúc thư mục dự án

```text
CipherNet/
├── .agents/
│   └── rules/                          # Quy tắc vận hành và chuẩn kỹ thuật cho AI Agent
├── .github/
│   └── workflows/
│       ├── java-ci.yml                 # CI: Tự động biên dịch, chạy JUnit 5 và báo cáo JaCoCo
│       ├── java-cd-release.yml         # CD: Đóng gói Fat JAR và tạo GitHub Release khi gắn tag v*
│       └── codeql.yml                  # Phân tích bảo mật tĩnh CodeQL cho Java
├── architecture/                       # Thư mục đặc tả bất biến (Strict Read-Only)
│   ├── specs/                          # SRS.md, udp-protocol.md, vigenere-and-io.md, TASK_SPECIFICATIONS.md
│   ├── database/                       # schema.sql, sample_data.sql, README.md
│   ├── diagrams/                       # 7 sơ đồ kiến trúc Mermaid (.mmd)
│   ├── docs/                           # AGENT_WORKFLOW.md, CODING_STANDARDS.md, TESTING_GUIDE.md, UI_UX_DESIGN_GUIDELINES.md
│   ├── tasks/                          # 24 tệp mô tả tác vụ theo Sprint (DEP, CORE, SRV, UI, VER)
│   └── verification/                   # Hồ sơ nghiệm thu M1-foundation đến M4-release
├── app/                                # Mã nguồn chính và bộ kiểm thử
│   ├── src/main/java/com/detai10/
│   │   ├── core/                       # Logic nghiệp vụ độc lập UI (protocol, cipher, io, search, db, model)
│   │   ├── server/                     # Ứng dụng Server (ServerApp, ServerMainFrame, UdpServerWorker)
│   │   └── client/                     # Ứng dụng Client (ClientApp, UdpClientService, ConnectFrame, ClientMainFrame)
│   ├── src/main/resources/             # Cấu hình database.properties.example
│   ├── src/test/java/com/detai10/      # Bộ kiểm thử JUnit 5 (Unit, Stress Test và E2E Socket thực)
│   └── build.gradle
├── scripts/                            # Tiện ích khởi tạo DB, kiểm thử, build và khởi chạy trên Windows
├── AGENTS.md                           # Quy chuẩn tổng quát cho AI Agent
├── CONTEXT.md                          # Ngữ cảnh nghiệp vụ và danh mục thuật ngữ chuẩn
├── START.md                            # Hướng dẫn cài đặt và vận hành nhanh
└── README.md                           # Tài liệu tổng quan dự án
```

---

## 6. Kế hoạch triển khai (Product Backlog)

Dự án được tổ chức thành **4 Sprint**, bao gồm **24 Tasks** với tổng khối lượng **56 Story Points (SP)**:

### Sprint 1: Ngữ cảnh, Đặc tả, CSDL và Hạ tầng CI (`M1-foundation` — 9 SP)
- `DEP-01`: Khởi tạo kho mã nguồn, cấu trúc thư mục chuẩn và bộ tài liệu ngữ cảnh.
- `DEP-02`: Soạn thảo tài liệu đặc tả (`SRS.md`, `udp-protocol.md`, `vigenere-and-io.md`) và sơ đồ Mermaid.
- `DEP-03`: Thiết kế và khởi tạo cơ sở dữ liệu (`schema.sql`, `sample_data.sql`).
- `DEP-04`: Thiết lập quy trình GitHub Actions CI và quy tắc bảo vệ nhánh.
- `VER-01`: Kiểm chứng Milestone 1 (CI đạt chuẩn và nạp cơ sở dữ liệu thành công).

### Sprint 2: Core Logic, DAO và Kiểm thử đơn vị (`M2-core-logic` — 15 SP)
- `CORE-01`: Cài đặt bộ đóng gói và phân tích gói tin UDP (`UdpProtocol` và tiện ích `DatagramPacket`).
- `CORE-02`: Cài đặt thuật toán mã hóa và giải mã Vigenère (`VigenereCipher`).
- `CORE-03`: Cài đặt đọc/ghi tệp chế độ văn bản (`FileHelper` Text Mode).
- `CORE-04`: Cài đặt đọc/ghi tệp chế độ nhị phân (`FileHelper` Binary Mode).
- `CORE-05`: Cài đặt thuật toán tìm tất cả vị trí từ khóa `"HELLO"` (`KeywordFinder`).
- `CORE-06`: Xây dựng tầng truy xuất cơ sở dữ liệu (`DatabaseHelper`).
- `CORE-07`: Xây dựng bộ kiểm thử JUnit 5 và lớp `CoreSelfCheck` cho tầng Core.
- `VER-02`: Kiểm chứng Milestone 2 (100% kiểm thử Headless đạt trên GitHub Actions).

### Sprint 3: Ứng dụng Server và Client (`M3-udp-app` — 21 SP)
- `SRV-01`: Thiết kế giao diện quản trị Server (`ServerMainFrame`).
- `SRV-02`: Xây dựng luồng nền lắng nghe UDP (`UdpServerWorker`).
- `SRV-03`: Tích hợp luồng xử lý gói `DATA` trên Server (Mã hóa, File I/O, Giải mã, Tìm kiếm, DB, Phản hồi).
- `UI-01`: Thiết kế và xử lý màn hình kết nối Client (`ConnectFrame` kết hợp `UdpClientService`).
- `UI-02`: Thiết kế màn hình làm việc chính Client (`ClientMainFrame`).
- `UI-03`: Xử lý gửi gói `DATA` qua `SwingWorker`, nhận phản hồi và tô sáng từ khóa `"HELLO"`.
- `VER-03`: Kiểm chứng Milestone 3 (kiểm thử tích hợp E2E bằng `DatagramSocket` thực và kiểm thử giao diện với MySQL).

### Sprint 4: Đóng gói CD, Kiểm thử tải và Bàn giao (`M4-release` — 11 SP)
- `DEP-05`: Cấu hình đóng gói 2 tệp Fat JAR (`udp-server.jar`, `udp-client.jar`).
- `DEP-06`: Xây dựng CD Workflow tự động phát hành bản Release trên GitHub.
- `VER-04`: Kiểm thử tải đồng thời 100 gói UDP và nghiệm thu bản phát hành.
- `DEP-07`: Hoàn thiện `README.md`, `START.md` và bộ tài liệu báo cáo.

---

## 7. Quy trình làm việc tiêu chuẩn (5 bước)

1. **Chốt ngữ cảnh và đặc tả:** Tham chiếu `CONTEXT.md`, `AGENTS.md` và tài liệu trong `architecture/specs/` trước khi lập trình.
2. **Triển khai theo tác vụ nguyên tử:** Tuân thủ yêu cầu kỹ thuật và tiêu chí hoàn thành tại `architecture/tasks/<TASK_ID>.md`.
3. **Phân nhánh Git và TDD:** Tạo nhánh tính năng từ nhánh chính/nhánh phát triển; viết kiểm thử JUnit 5 độc lập với Swing trước khi hoàn thiện mã nguồn.
4. **Pull Request và CI Gate:** Mọi thay đổi trước khi hợp nhất bắt buộc vượt qua quy trình kiểm thử tự động (`java-ci.yml`).
5. **Nghiệm thu và phát hành:** Lưu hồ sơ kiểm chứng theo từng cột mốc và đẩy thẻ phiên bản (`v*`) để kích hoạt đóng gói Release tự động.

---

## 8. Hướng dẫn cài đặt và khởi chạy

### Yêu cầu môi trường
- **Java Development Kit (JDK) 17 LTS trở lên.**
- Công cụ build: Gradle Wrapper (`./gradlew` trên Linux/macOS hoặc `.\gradlew.bat` trên Windows).
- Hệ quản trị cơ sở dữ liệu: MySQL 8.0+ cục bộ (hoặc tự động chuyển sang H2 In-Memory khi chạy kiểm thử/chưa cấu hình MySQL).

### Bước 1: Khởi tạo Cơ sở dữ liệu
Thực thi kịch bản tại `architecture/database/schema.sql` (hoặc chạy `.\scripts\init-db.bat` trên Windows):
```sql
CREATE TABLE IF NOT EXISTS lich_su_xu_ly (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_ip VARCHAR(45) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    io_mode VARCHAR(10) NOT NULL,
    original_content TEXT NOT NULL,
    encrypted_content TEXT NOT NULL,
    hello_positions VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Bước 2: Biên dịch, kiểm thử và đóng gói
```bash
# Chạy toàn bộ kiểm thử tự động (Headless)
./gradlew test --no-daemon

# Đóng gói 2 Fat JAR độc lập vào app/build/libs/
./gradlew shadowJar --no-daemon
```

### Bước 3: Khởi chạy ứng dụng
1. **Khởi chạy Server:**
   ```bash
   java -jar app/build/libs/udp-server.jar
   ```
   - Nhập cổng lắng nghe (mặc định: `9876`) và khóa Vigenère (mặc định: `KEYSECRET`), sau đó nhấn **Khởi động (Start)**.
2. **Khởi chạy Client:**
   ```bash
   java -jar app/build/libs/udp-client.jar
   ```
   - Nhập địa chỉ IP Server (mặc định: `127.0.0.1`) và cổng (`9876`), nhấn **Kết nối** để kiểm tra `PING`/`PONG`.
   - Tại màn hình chính, nhập tên tệp, nội dung văn bản, chọn chế độ `TEXT` hoặc `BINARY` và nhấn **Gửi dữ liệu tới Server**.

---

## 9. Ghi chú kỹ thuật
- **Phạm vi nghiệp vụ:** Tên đề tài là *"Quản lí Sinh Viên"*, tuy nhiên trọng tâm kỹ thuật theo đặc tả là truyền nhận dữ liệu qua giao thức UDP, mã hóa/giải mã Vigenère, đọc/ghi tệp 2 chế độ và tìm kiếm vị trí từ khóa `"HELLO"`.
- Bảng `sinh_vien` được chuẩn bị sẵn trong `architecture/database/schema.sql` để hỗ trợ mở rộng khi có yêu cầu quản lý thông tin sinh viên song song.
