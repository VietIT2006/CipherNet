# ĐỀ TÀI 10: CHƯƠNG TRÌNH QUẢN LÝ SINH VIÊN TRÊN MÔ HÌNH CLIENT – SERVER (JAVA UDP)

> **Dự án:** Ứng dụng Client – Server truyền nhận dữ liệu qua giao thức UDP, tích hợp mã hóa Vigenère, lưu trữ File 2 chế độ (Văn bản & Nhị phân) và quản lý cơ sở dữ liệu JDBC.  
> **Phương pháp phát triển:** Áp dụng Workflow chuẩn Clucky (Spec-driven & AI-ready development).

---

## 📌 1. Tổng quan dự án

Dự án hiện thực mô hình Client – Server phân tán sử dụng Java Socket với giao thức UDP. Hệ thống cho phép Client kết nối, kiểm tra tính sẵn sàng (Healthcheck PING/PONG), gửi dữ liệu văn bản theo chế độ tùy chọn; Server tiếp nhận gói tin, ghi nhận lịch sử vào cơ sở dữ liệu qua JDBC, thực hiện mã hóa Vigenère, ghi và đọc file kiểm chứng, giải mã và phân tích tìm kiếm từ khóa `"HELLO"` để phản hồi kết quả về cho Client.

### 🌟 Điểm nổi bật
- **Mô hình kiến trúc Spec-driven & AI-ready:** Toàn bộ đặc tả, sơ đồ kiến trúc, tài liệu ngữ cảnh được quy hoạch chi tiết tại thư mục `architecture/` trước khi lập trình.
- **Tách biệt hoàn toàn Core Logic & GUI:** Các module mã hóa, I/O, phân tích gói tin và tìm kiếm được viết độc lập với Java Swing, cho phép chạy Unit Test tự động trên môi trường Linux Headless CI.
- **Tối ưu hóa UDP & Đa luồng:** Server sử dụng Worker Thread riêng biệt để lắng nghe và điều phối gói tin, đảm bảo giao diện không bị đóng băng (non-blocking GUI).

---

## 🛠️ 2. Công nghệ sử dụng (Tech Stack)

| Thành phần | Công nghệ / Thư viện | Chi tiết sử dụng |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Java 21 (LTS mới nhất) | Phiên bản chuẩn cho toàn dự án (Tương thích 17+) |
| **Giao diện** | Java Swing | `ServerMainFrame`, `ConnectFrame`, `ClientMainFrame` |
| **Mạng** | Java UDP Socket | `DatagramSocket`, `DatagramPacket` |
| **I/O File** | Java I/O (2 chế độ) | `BufferedWriter`/`BufferedReader` (Text) & `DataOutputStream`/`DataInputStream` bọc Buffer (Binary) |
| **Bảo mật** | Thuật toán Vigenère | Dịch chuyển mã hóa/giải mã ký tự `A-Z`/`a-z`, bảo toàn ký tự số, ký hiệu |
| **Cơ sở dữ liệu** | JDBC (MySQL / SQL Server) | Lưu vết lịch sử giao dịch qua `PreparedStatement` |
| **Kiểm thử** | JUnit 5 | Unit Test tự động cho tầng Core Logic |
| **CI/CD** | GitHub Actions | Tự động hóa Build, Test và Release Fat JAR (`udp-server.jar`, `udp-client.jar`) |

---

## 📡 3. Đặc tả Giao thức UDP (UDP Protocol)

Giao thức trao đổi dữ liệu tùy biến giữa Client và Server:

### 3.1. Gói tin PING / PONG (Kiểm tra kết nối)
- **Client -> Server:** `PING`
- **Server -> Client:** `PONG`
- **Cơ chế:** Client thiết lập timeout 3 giây. Nếu nhận được `PONG`, kết nối thành công và chuyển sang màn hình làm việc chính; ngược lại thông báo lỗi kết nối.

### 3.2. Gói tin Dữ liệu (DATA)
- **Cú pháp gửi:**  
  `DATA|<mode>|<fileName>|<content>`
  - `<mode>`: Chế độ lưu file (`TEXT` hoặc `BINARY`).
  - `<fileName>`: Tên file lưu trên Server.
  - `<content>`: Nội dung dữ liệu văn bản do Client nhập.
- **Cú pháp phản hồi:**  
  `RESULT|<status>|<message>|<indices>`
  - Trả về danh sách các vị trí (index) tìm thấy từ khóa `"HELLO"` hoặc thông báo không tìm thấy.

---

## 🔄 4. Luồng xử lý dữ liệu tại Server

```
[Client] Gửi gói tin DATA
       │
       ▼ (UDP Socket)
[Server] UdpServerWorker nhận gói tin
       │
       ├─► 1. Lưu bản ghi vào CSDL (bảng lich_su_xu_ly qua JDBC)
       │
       ├─► 2. Mã hóa nội dung bằng thuật toán Vigenère (Key từ Server GUI)
       │
       ├─► 3. Ghi dữ liệu mã hóa ra File theo chế độ:
       │      • TEXT: BufferedWriter / BufferedReader (UTF-8)
       │      • BINARY: DataOutputStream bọc BufferedOutputStream (writeUTF)
       │
       ├─► 4. Đọc lại nội dung từ file vừa ghi để kiểm chứng
       │
       ├─► 5. Giải mã nội dung bằng khóa Vigenère
       │
       ├─► 6. Tìm tất cả vị trí xuất hiện của từ khóa "HELLO" (KeywordFinder)
       │
       ├─► 7. Cập nhật giao diện Server (SwingUtilities.invokeLater)
       │
       └─► 8. Đóng gói & gửi kết quả về lại Client qua UDP
```

---

## 📂 5. Cấu trúc thư mục dự án

```text
đề_tài_số_10/
├── .github/
│   └── workflows/
│       ├── java-ci.yml                 # CI: Tự động build & chạy JUnit 5
│       └── java-cd-release.yml         # CD: Đóng gói Fat JAR & tạo GitHub Release
├── architecture/                       # Tài liệu đặc tả & thiết kế hệ thống
│   ├── AGENTS.md                       # Quy chuẩn phối hợp Agent / Developers
│   ├── CONTEXT.md                      # Bối cảnh & phạm vi kỹ thuật của dự án
│   ├── START.md                        # Hướng dẫn bắt đầu nhanh cho thành viên mới
│   ├── SRS.md                          # Đặc tả yêu cầu phần mềm
│   ├── udp-protocol.md                 # Đặc tả chi tiết giao thức UDP
│   ├── vigenere-and-io.md              # Đặc tả thuật toán Vigenère & cơ chế File I/O
│   ├── database/
│   │   ├── schema.sql                  # Script tạo bảng lich_su_xu_ly
│   │   └── sample_data.sql             # Dữ liệu mẫu kiểm thử
│   ├── diagrams/                       # Sơ đồ Mermaid (.mmd & .png)
│   ├── tasks/                          # Chi tiết từng Task theo Sprint (DEP, CORE, SRV, UI, VER)
│   └── verification/                   # Hồ sơ & bằng chứng nghiệm thu từng Sprint
├── app/                                # Mã nguồn ứng dụng
│   ├── src/
│   │   ├── main/java/                  # Source code (Core, Server, Client)
│   │   └── test/java/                  # Unit Test (JUnit 5 độc lập với UI)
│   └── build.gradle (hoặc pom.xml)
├── PRODUCT BACKLOG.docx                # Tài liệu Product Backlog gốc
└── README.md                           # Tài liệu tổng quan dự án
```

---

## 📋 6. Kế hoạch triển khai (Product Backlog)

Dự án được chia thành **4 Sprint**, gồm **24 Tasks** với tổng số **56 Story Points (SP)**:

### 🔹 Sprint 1: Ngữ cảnh, Đặc tả, CSDL & Hạ tầng CI (`M1-foundation` - 9 SP)
- `DEP-01`: Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh.
- `DEP-02`: Viết tài liệu đặc tả (`SRS.md`, `udp-protocol.md`, ...) & vẽ sơ đồ Mermaid.
- `DEP-03`: Thiết kế & khởi tạo CSDL (`schema.sql`, `sample_data.sql`).
- `DEP-04`: Thiết lập GitHub Actions CI & Branch Protection.
- `VER-01`: Kiểm chứng Sprint 1 (CI xanh và import DB thành công).

### 🔹 Sprint 2: Core Logic, DAO & Unit Test (`M2-core-logic` - 15 SP)
- `CORE-01`: Cài đặt bộ phân tích gói tin UDP (`UdpProtocol`).
- `CORE-02`: Cài đặt thuật toán mã hóa & giải mã Vigenère (`VigenereCipher`).
- `CORE-03`: Cài đặt đọc/ghi file chế độ Văn bản (`FileHelper` Text Mode).
- `CORE-04`: Cài đặt đọc/ghi file chế độ Nhị phân (`FileHelper` Binary Mode).
- `CORE-05`: Cài đặt thuật toán tìm vị trí từ khóa `"HELLO"` (`KeywordFinder`).
- `CORE-06`: Xây dựng tầng thao tác CSDL (`DatabaseHelper`).
- `CORE-07`: Viết bộ Unit Test JUnit 5 & `CoreSelfCheck` cho Core Logic.
- `VER-02`: Kiểm chứng Sprint 2 (Pass 100% test trên GitHub Actions).

### 🔹 Sprint 3: Ứng dụng Server & Client (`M3-udp-app` - 21 SP)
- `SRV-01`: Thiết kế giao diện Server (`ServerMainFrame`).
- `SRV-02`: Xây dựng luồng nền lắng nghe UDP (`UdpServerWorker`).
- `SRV-03`: Tích hợp luồng xử lý gói DATA trên Server (DB, Vigenère, I/O, Keyword).
- `UI-01`: Thiết kế & xử lý màn hình kết nối Client (`ConnectFrame`).
- `UI-02`: Thiết kế màn hình làm việc chính Client (`ClientMainFrame`).
- `UI-03`: Xử lý gửi gói DATA & nhận kết quả hiển thị trên Client.
- `VER-03`: Kiểm chứng Sprint 3 (E2E Client – Server).

### 🔹 Sprint 4: Đóng gói CD, Kiểm thử & Bàn giao (`M4-release` - 11 SP)
- `DEP-05`: Cấu hình đóng gói 2 Fat JAR (`udp-server.jar`, `udp-client.jar`).
- `DEP-06`: Xây dựng CD Workflow tự động phát hành bản Release.
- `VER-04`: Kiểm thử tải đồng thời & nghiệm thu bản Release.
- `DEP-07`: Hoàn thiện `README.md`, `START.md` và tài liệu báo cáo.

---

## ⚡ 7. Quy trình làm việc 5 bước (Standard Workflow)

1. **Chốt Context & Spec:** Định nghĩa rõ file ngữ cảnh (`CONTEXT.md`, `AGENTS.md`), giao thức và sơ đồ trước khi code.
2. **Chia Task nguyên tử:** Mỗi task được mô tả cụ thể trong `architecture/tasks/<TASK_ID>.md` với mục tiêu, file tác động, yêu cầu kỹ thuật và checklist hoàn thành.
3. **Nhánh Git & TDD:** Phát triển theo nhánh tính năng tách từ `develop` (ví dụ: `feat/CORE-02-vigenere-cipher`). Logic cốt lõi được kiểm thử tự động với JUnit 5.
4. **Pull Request & CI Gate:** Mọi PR bắt buộc phải vượt qua CI workflow (`java-ci.yml`) trước khi được merge.
5. **Verification & Release CD:** Lưu lại bằng chứng kiểm chứng tại `architecture/verification/` sau mỗi Sprint và kích hoạt CD khi gắn thẻ tag phiên bản (`v*`).

---

## 🚀 8. Hướng dẫn cài đặt & Khởi chạy

### Yêu cầu môi trường
- Java Development Kit (JDK) 21 LTS (hoặc JDK 17 trở lên).
- Quản lý dự án: Gradle hoặc Maven.
- Hệ quản trị CSDL: MySQL hoặc SQL Server.

### Bước 1: Khởi tạo Cơ sở dữ liệu
Chạy script SQL tại `architecture/database/schema.sql` để tạo bảng lưu trữ lịch sử:
```sql
-- Cấu trúc bảng lịch sử xử lý dữ liệu
CREATE TABLE IF NOT EXISTS lich_su_xu_ly (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_ip VARCHAR(50) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_mode VARCHAR(10) NOT NULL,
    original_content TEXT NOT NULL,
    encrypted_content TEXT NOT NULL,
    hello_positions VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Bước 2: Build dự án
```bash
# Build & chạy Unit Test
./gradlew test   # hoặc: mvn test

# Đóng gói Fat JAR độc lập
./gradlew shadowJar   # hoặc: mvn package
```

### Bước 3: Khởi chạy ứng dụng
1. **Chạy Server:**
   ```bash
   java -jar udp-server.jar
   ```
   - Nhập Port lắng nghe (mặc định: `9876`).
   - Nhập Key mã hóa Vigenère (ví dụ: `KEYSECRET`).
   - Nhấn **Start Server**.

2. **Chạy Client:**
   ```bash
   java -jar udp-client.jar
   ```
   - Nhập IP Server (ví dụ: `127.0.0.1`) và Port (`9876`).
   - Nhấn **Kết nối** (gửi PING kiểm tra).
   - Sau khi kết nối thành công, nhập nội dung văn bản, tên file và chọn chế độ lưu (`TEXT` hoặc `BINARY`), nhấn **Gửi dữ liệu**.

---

## 📝 9. Ghi chú quan trọng (Technical Note)
- **Về phạm vi đề tài:** Tiêu đề đồ án là *"Quản lí Sinh Viên"* tuy nhiên nội dung kỹ thuật trọng tâm theo yêu cầu là truyền nhận gói tin qua UDP, mã hóa/giải mã Vigenère, lưu trữ File 2 chế độ và tìm kiếm vị trí từ khóa `"HELLO"`.
- Nếu có yêu cầu tích hợp thêm bảng thông tin sinh viên, schema có thể mở rộng bổ sung bảng `sinh_vien` phục vụ quản lý song song.
