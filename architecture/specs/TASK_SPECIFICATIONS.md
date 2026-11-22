# TÀI LIỆU ĐẶC TẢ CHI TIẾT 24 TASK — PRODUCT BACKLOG ĐỀ TÀI 10

> **Dự án:** Vigen — Hệ thống Client – Server Quản lý & Truyền nhận Dữ liệu Mã hóa qua Giao thức UDP  
> **Phương pháp phát triển:** Spec-driven & AI-ready Development (Áp dụng Workflow chuẩn Clucky)  
> **Tổng khối lượng ước tính:** **4 Sprint | 24 Tasks | 56 Story Points (SP)**  
> **Ngôn ngữ:** Java 21 LTS | **Giao thức:** UDP (`DatagramSocket`) | **Giao diện:** Java Swing | **CSDL:** MySQL (Localhost:3306)

---

## BẢNG TỔNG HỢP PRODUCT BACKLOG THEO SPRINT

| Sprint | Milestone | Tên Sprint | Số Task | Tổng SP | Trọng tâm Sprint |
| :---: | :---: | :--- | :---: | :---: | :--- |
| **Sprint 1** | `M1-foundation` | Ngữ cảnh, Đặc tả, CSDL & Hạ tầng CI | **5 tasks** | **9 SP** | Khởi tạo repo, specs, CSDL MySQL, sơ đồ Mermaid, GitHub Actions CI |
| **Sprint 2** | `M2-core-logic` | Core Logic, DAO & Unit Test | **8 tasks** | **15 SP** | UdpProtocol, Vigenère Cipher, File I/O (Text/Binary), Finder, DAO, JUnit 5 |
| **Sprint 3** | `M3-udp-app` | Ứng dụng Server & Client (Swing & Worker) | **7 tasks** | **21 SP** | Server UI & Worker Thread, Client ConnectFrame & MainFrame, Tích hợp E2E |
| **Sprint 4** | `M4-release` | Đóng gói CD, Kiểm thử & Bàn giao | **4 tasks** | **11 SP** | Fat JAR (`udp-server.jar`, `udp-client.jar`), GitHub Release CD, Test tải, Tài liệu |
| **TỔNG** | | **Toàn bộ dự án** | **24 tasks** | **56 SP** | **Hoàn chỉnh 100% theo Product Backlog chuẩn** |

---

# CHI TIẾT TỪNG SPRINT & TỪNG TASK

---

## SPRINT 1: NGỮ CẢNH, ĐẶC TẢ, CSDL & HẠ TẦNG CI (M1-foundation — 9 SP)

### DEP-01: Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh
- **Mã Task:** `DEP-01` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `AGENTS.md`, `CONTEXT.md`, `START.md`, `README.md`, `.gitignore`, `.gitattributes`, `build.gradle`, `settings.gradle`, `app/build.gradle`, `gradlew`, `gradlew.bat`
- **Mục tiêu:** Thiết lập cấu trúc kho lưu trữ Git theo quy chuẩn Spec-driven development chuẩn Clucky, khởi tạo các tài liệu nền tảng và cấu trúc project Java 21.
- **Yêu cầu kỹ thuật:**
  - Hoàn thiện cây thư mục `architecture/` (`specs`, `tasks`, `diagrams`, `database`, `verification`) và `app/` (`src/main/java`, `src/test/java`).
  - Cấu hình Gradle với Java 21, dependencies (MySQL Connector, JUnit 5).
  - Viết tài liệu `AGENTS.md`, `CONTEXT.md`, `START.md`, cấu hình `.gitignore` và `.gitattributes`.
- **Tiêu chí hoàn thành (DoD):**
  - [x] Cây thư mục hoàn chỉnh, bộ file ngữ cảnh sẵn sàng, build Gradle thành công.

---

### DEP-02: Viết tài liệu đặc tả & sơ đồ Mermaid
- **Mã Task:** `DEP-02` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `architecture/specs/SRS.md`, `architecture/specs/udp-protocol.md`, `architecture/specs/vigenere-and-io.md`, `architecture/specs/TASK_SPECIFICATIONS.md`, `architecture/diagrams/01-system-architecture.mmd`, `architecture/diagrams/02-udp-protocol-flow.mmd`, `architecture/diagrams/03-server-processing-flow.mmd`, `architecture/diagrams/04-vigenere-cipher-flow.mmd`, `architecture/diagrams/05-database-erd.mmd`, `architecture/diagrams/06-client-state-machine.mmd`, `architecture/diagrams/07-thread-concurrency.mmd`, `architecture/diagrams/README.md`
- **Mục tiêu:** Quy chuẩn hóa toàn bộ tài liệu kỹ thuật và sơ đồ kiến trúc hệ thống trước khi lập trình.
- **Yêu cầu kỹ thuật:**
  - Chốt giao thức `PING <-> PONG` (timeout 3s) và gói `DATA|<mode>|<fileName>|<content>`.
  - Đặc tả thuật toán Vigenère bảo toàn ký tự số, ký hiệu, khoảng trắng.
  - Vẽ đầy đủ các sơ đồ Mermaid `.mmd` trực quan hóa kiến trúc hệ thống, luồng tuần tự, lưu đồ xử lý tại Server, lưu đồ Vigenère, ERD và máy trạng thái.
- **Tiêu chí hoàn thành (DoD):**
  - [x] Đầy đủ các file đặc tả trong `architecture/specs/` và 7 sơ đồ Mermaid trong `architecture/diagrams/`.

---

### DEP-03: Thiết kế & khởi tạo CSDL MySQL
- **Mã Task:** `DEP-03` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `architecture/database/schema.sql`, `architecture/database/sample_data.sql`, `architecture/database/README.md`, `scripts/init-db.bat`, `app/src/main/resources/database.properties.example`
- **Mục tiêu:** Thiết kế lược đồ cơ sở dữ liệu lưu lịch sử xử lý gói tin UDP và bảng thông tin sinh viên trên MySQL cục bộ.
- **Yêu cầu kỹ thuật:**
  - CSDL MySQL chạy tại `localhost:3306`, database `detai10_db` chuẩn `utf8mb4`.
  - Bảng `lich_su_xu_ly`: Lưu `client_ip`, `file_name`, `file_mode`, `original_content`, `encrypted_content`, `hello_positions`, `created_at`.
  - Bảng `sinh_vien`: Lưu `ma_sv`, `ho_ten`, `lop`, `email`, `ngay_sinh`.
  - Tạo script khởi tạo tự động `scripts/init-db.bat` và file mẫu `database.properties.example`.
- **Tiêu chí hoàn thành (DoD):**
  - [x] Chạy thành công `schema.sql` và nạp `sample_data.sql` không lỗi trên MySQL local.

---

### DEP-04: Thiết lập GitHub Actions CI & Branch Protection
- **Mã Task:** `DEP-04` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `.github/workflows/java-ci.yml`
- **Mục tiêu:** Tự động hóa quy trình kiểm thử liên tục (CI) khi có mã nguồn mới được push hoặc pull request.
- **Yêu cầu kỹ thuật:**
  - Viết workflow kích hoạt trên nhánh `main` và `develop`.
  - Cài đặt JDK 17 Temurin, phân quyền thực thi cho Gradle wrapper.
  - Chạy `./gradlew test --no-daemon` và lưu trữ báo cáo kiểm thử Unit Test.
- **Tiêu chí hoàn thành (DoD):**
  - [x] File workflow CI hoàn chỉnh, sẵn sàng chạy xanh tự động trên GitHub Actions.

---

### VER-01: Kiểm chứng Sprint 1 (Setup & CI)
- **Mã Task:** `VER-01` | **Sprint:** 1 | **Độ ưu tiên:** Trung bình | **Ước lượng:** 1 SP
- **File liên quan:** `architecture/verification/M1-foundation.md`
- **Mục tiêu:** Nghiệm thu toàn diện các kết quả chuyển giao của Sprint 1.
- **Tiêu chí hoàn thành (DoD):**
  - [x] Hồ sơ kiểm chứng M1 được lập đầy đủ, xác nhận hạ tầng repo, specs, DB và CI đạt chuẩn.

---

## SPRINT 2: CORE LOGIC, DAO & UNIT TEST (M2-core-logic — 15 SP)

### CORE-01: Cài đặt bộ phân tích gói tin UDP (UdpProtocol)
- **Mã Task:** `CORE-01` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Ước lượng:** 1 SP
- **File liên quan:** `app/src/main/java/com/detai10/core/protocol/UdpProtocol.java`, `app/src/main/java/com/detai10/core/model/FileMode.java`, `app/src/test/java/com/detai10/core/protocol/UdpProtocolTest.java`
- **Mục tiêu:** Đóng gói và bóc tách các bản tin giao tiếp UDP giữa Client và Server.
- **Yêu cầu kỹ thuật:**
  - Hỗ trợ gói tin `PING`, `PONG`, `DATA|<mode>|<fileName>|<content>`, `RESULT|<status>|<positions>`, `ERROR|<message>`.
  - Phân tách an toàn bằng `rawMessage.split("\\|", 4)` để nội dung văn bản có thể chứa ký tự `|` mà không làm vỡ cấu trúc gói.
- **Tiêu chí hoàn thành (DoD):**
  - Đóng gói và trích xuất dữ liệu chính xác 100%, có Unit Test bao phủ ca biên.

---

### CORE-02: Cài đặt thuật toán mã hóa & giải mã Vigenère (VigenereCipher)
- **Mã Task:** `CORE-02` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`, `app/src/test/java/com/detai10/core/cipher/VigenereCipherTest.java`
- **Mục tiêu:** Cài đặt phương thức mã hóa `encrypt(text, key)` và giải mã `decrypt(cipherText, key)`.
- **Yêu cầu kỹ thuật:**
  - Chỉ dịch chuyển chữ cái tiếng Anh `A-Z` và `a-z` theo modulo 26.
  - Bảo toàn tuyệt đối chữ số `0-9`, dấu cách, ký tự đặc biệt, dấu câu, ký tự xuống dòng.
  - Con trỏ khóa (key index) không được tăng bước khi gặp ký tự invariant.
  - Chuẩn hóa khóa chỉ giữ lại chữ cái và chuyển về chữ hoa.
- **Tiêu chí hoàn thành (DoD):**
  - Chu trình mã hóa rồi giải mã khớp 100% với văn bản gốc ban đầu.

---

### CORE-03: Cài đặt đọc/ghi file chế độ Văn bản (FileHelper – Text Mode)
- **Mã Task:** `CORE-03` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `app/src/main/java/com/detai10/core/io/FileHelper.java`, `app/src/test/java/com/detai10/core/io/FileHelperTextTest.java`
- **Mục tiêu:** Hiện thực hàm đọc và ghi file văn bản thuần túy theo chuẩn UTF-8.
- **Yêu cầu kỹ thuật:**
  - Ghi file bằng `BufferedWriter` (UTF-8); đọc file bằng `BufferedReader` (UTF-8).
  - Tự động tạo thư mục cha nếu chưa có; quản lý luồng bằng `try-with-resources`.
- **Tiêu chí hoàn thành (DoD):**
  - Đọc và ghi file văn bản tiếng Việt UTF-8 nguyên vẹn, không bị lỗi font.

---

### CORE-04: Cài đặt đọc/ghi file chế độ Nhị phân (FileHelper – Binary Mode)
- **Mã Task:** `CORE-04` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `app/src/main/java/com/detai10/core/io/FileHelper.java`, `app/src/test/java/com/detai10/core/io/FileHelperBinaryTest.java`
- **Mục tiêu:** Hiện thực hàm đọc và ghi file nhị phân định kiểu có đệm.
- **Yêu cầu kỹ thuật:**
  - Ghi file bằng `DataOutputStream` bọc `BufferedOutputStream` sử dụng `writeUTF(content)`.
  - Đọc file bằng `DataInputStream` bọc `BufferedInputStream` sử dụng `readUTF()`.
- **Tiêu chí hoàn thành (DoD):**
  - Đọc lại dữ liệu nhị phân khớp từng byte so với chuỗi ban đầu.

---

### CORE-05: Cài đặt thuật toán tìm tất cả vị trí từ "HELLO" (KeywordFinder)
- **Mã Task:** `CORE-05` | **Sprint:** 2 | **Độ ưu tiên:** Trung bình | **Ước lượng:** 1 SP
- **File liên quan:** `app/src/main/java/com/detai10/core/search/KeywordFinder.java`, `app/src/test/java/com/detai10/core/search/KeywordFinderTest.java`
- **Mục tiêu:** Quét toàn bộ văn bản sau giải mã để tìm tất cả các vị trí xuất hiện của từ khóa `"HELLO"`.
- **Yêu cầu kỹ thuật:**
  - Trả về danh sách `List<Integer>` đại diện cho các index 0-based.
  - Tìm chính xác tất cả các lần xuất hiện, không dừng lại ở lần đầu tiên.
  - Xử lý trả về danh sách rỗng nếu không tìm thấy.
- **Tiêu chí hoàn thành (DoD):**
  - Trả về đúng danh sách vị trí trong mọi ca kiểm thử.

---

### CORE-06: Xây dựng tầng thao tác CSDL (DatabaseHelper)
- **Mã Task:** `CORE-06` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`, `app/src/main/java/com/detai10/core/model/HistoryRecord.java`, `app/src/test/java/com/detai10/core/db/DatabaseHelperTest.java`
- **Mục tiêu:** Hiện thực tầng truy xuất dữ liệu JDBC để ghi log xử lý của Server vào MySQL.
- **Yêu cầu kỹ thuật:**
  - Đọc cấu hình từ `database.properties` (URL, user, password).
  - Hàm `insertLog(...)` bắt buộc sử dụng `PreparedStatement` với tham số `?` để chống SQL Injection.
  - Ghi nhận đầy đủ: `client_ip`, `file_name`, `file_mode`, `original_content`, `encrypted_content`, `hello_positions`.
- **Tiêu chí hoàn thành (DoD):**
  - Thêm mới bản ghi vào bảng `lich_su_xu_ly` thành công từ Java code.

---

### CORE-07: Viết bộ Unit Test (JUnit 5) & lớp CoreSelfCheck cho toàn bộ Core Logic
- **Mã Task:** `CORE-07` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `app/src/main/java/com/detai10/core/CoreSelfCheck.java`, `app/src/test/java/com/detai10/core/protocol/UdpProtocolTest.java`, `app/src/test/java/com/detai10/core/cipher/VigenereCipherTest.java`, `app/src/test/java/com/detai10/core/io/FileHelperTextTest.java`, `app/src/test/java/com/detai10/core/io/FileHelperBinaryTest.java`, `app/src/test/java/com/detai10/core/search/KeywordFinderTest.java`, `app/src/test/java/com/detai10/core/db/DatabaseHelperTest.java`
- **Mục tiêu:** Xây dựng bộ test tự động toàn diện kiểm chứng toàn bộ tầng Core Logic.
- **Yêu cầu kỹ thuật:**
  - Unit Test JUnit 5 cho `UdpProtocolTest`, `VigenereCipherTest`, `FileHelperTest` (bắt buộc dùng `@TempDir`), `KeywordFinderTest`, `DatabaseHelperTest`.
  - Tách biệt hoàn toàn khỏi Swing để chạy độc lập trên môi trường Linux Headless CI.
  - Lớp `CoreSelfCheck` có hàm `main()` hỗ trợ kiểm tra nhanh trực tiếp trên console.
- **Tiêu chí hoàn thành (DoD):**
  - 100% test pass trên `./gradlew test --no-daemon`.

---

### VER-02: Kiểm chứng Sprint 2 (Core Unit Test)
- **Mã Task:** `VER-02` | **Sprint:** 2 | **Độ ưu tiên:** Trung bình | **Ước lượng:** 1 SP
- **File liên quan:** `architecture/verification/M2-core-logic.md`
- **Mục tiêu:** Lưu trữ báo cáo kiểm chứng toàn bộ Core Logic đạt 100% test pass.
- **Tiêu chí hoàn thành (DoD):**
  - Báo cáo kết quả kiểm thử được lưu vào `architecture/verification/M2-core-logic.md`.

---

## SPRINT 3: ỨNG DỤNG SERVER & CLIENT (M3-udp-app — 21 SP)

### SRV-01: Thiết kế giao diện Server (ServerMainFrame)
- **Mã Task:** `SRV-01` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `app/src/main/java/com/detai10/server/ui/ServerMainFrame.java`, `app/src/main/java/com/detai10/server/ServerApp.java`
- **Mục tiêu:** Xây dựng màn hình đồ họa quản trị Server bằng Java Swing.
- **Yêu cầu kỹ thuật:**
  - Cấu hình: Ô nhập Port (`9876`), ô nhập Key Vigenère (`KEYSECRET`), nút Start/Stop Server có toggle trạng thái.
  - Hiển thị: Bảng `JTable` lịch sử gói tin nhận được và `JTextArea` console log (có auto-scroll).
  - Áp dụng layout linh hoạt (`BorderLayout`, `GridBagLayout`), phông chữ Segoe UI chuẩn tiếng Việt.
- **Tiêu chí hoàn thành (DoD):**
  - Giao diện trực quan, hiển thị mượt mà, quản lý trạng thái Server rõ ràng.

---

### SRV-02: Xây dựng luồng nền lắng nghe UDP (UdpServerWorker)
- **Mã Task:** `SRV-02` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`, `app/src/main/java/com/detai10/core/protocol/UdpProtocol.java`
- **Mục tiêu:** Xây dựng Worker Thread chuyên trách lắng nghe socket UDP và phản hồi tức thời.
- **Yêu cầu kỹ thuật:**
  - Vòng lặp `DatagramSocket.receive()` chạy trên Thread độc lập để không làm đơ giao diện Server.
  - Phản hồi `PONG` ngay lập tức khi nhận gói `PING`.
  - Mọi thao tác cập nhật bảng log và trạng thái phải bọc trong `SwingUtilities.invokeLater()`.
- **Tiêu chí hoàn thành (DoD):**
  - Server lắng nghe liên tục, không treo giao diện, phản hồi PONG ổn định.

---

### SRV-03: Tích hợp luồng xử lý gói DATA trên Server
- **Mã Task:** `SRV-03` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Ước lượng:** 5 SP
- **File liên quan:** `app/src/main/java/com/detai10/server/ServerApp.java`, `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`, `app/src/main/java/com/detai10/server/ui/ServerMainFrame.java`, `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`, `app/src/main/java/com/detai10/core/io/FileHelper.java`, `app/src/main/java/com/detai10/core/search/KeywordFinder.java`, `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`
- **Mục tiêu:** Kết nối hoàn chỉnh chu trình tiếp nhận, xử lý, lưu trữ và phản hồi gói tin DATA.
- **Yêu cầu kỹ thuật:**
  - Chu trình 8 bước:
    1. Nhận gói tin UDP $\rightarrow$ Bóc tách bằng `UdpProtocol.parseDataPacket()`.
    2. Lưu bản ghi vào CSDL MySQL qua `DatabaseHelper`.
    3. Mã hóa nội dung bằng `VigenereCipher.encrypt(content, serverKey)`.
    4. Ghi dữ liệu ra file theo chế độ: `TEXT` (`BufferedWriter`) hoặc `BINARY` (`DataOutputStream`).
    5. Đọc lại nội dung từ file vừa ghi để kiểm chứng tính toàn vẹn (Round-trip verification).
    6. Giải mã bằng `VigenereCipher.decrypt(readEncrypted, serverKey)`.
    7. Tìm kiếm tất cả vị trí từ khóa `"HELLO"` bằng `KeywordFinder`.
    8. Cập nhật giao diện Server và gửi gói phản hồi `RESULT` về cho Client qua UDP.
- **Tiêu chí hoàn thành (DoD):**
  - Chu trình hoạt động trơn tru từ lúc nhận dữ liệu đến khi phản hồi cho Client.

---

### UI-01: Thiết kế & xử lý màn hình Kết nối Client (ConnectFrame)
- **Mã Task:** `UI-01` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `app/src/main/java/com/detai10/client/ui/ConnectFrame.java`, `app/src/main/java/com/detai10/client/ClientApp.java`, `app/src/main/java/com/detai10/core/protocol/UdpProtocol.java`
- **Mục tiêu:** Xây dựng màn hình nhập IP/Port và kiểm tra kết nối với Server trước khi làm việc.
- **Yêu cầu kỹ thuật:**
  - Ô nhập IP (mặc định `127.0.0.1`), ô nhập Port (`9876`).
  - Nút Kết nối: Gửi gói `PING`, đặt timeout 3 giây (`setSoTimeout(3000)`).
  - Nhận `PONG` $\rightarrow$ Đóng `ConnectFrame`, mở `ClientMainFrame`.
  - Quá 3s không nhận được `PONG` $\rightarrow$ Hiển thị hộp thoại báo lỗi, cho phép thử lại.
- **Tiêu chí hoàn thành (DoD):**
  - Kiểm tra kết nối chính xác, chuyển cảnh màn hình mượt mà.

---

### UI-02: Thiết kế màn hình làm việc chính Client (ClientMainFrame)
- **Mã Task:** `UI-02` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `app/src/main/java/com/detai10/client/ui/ClientMainFrame.java`, `app/src/main/java/com/detai10/core/model/FileMode.java`
- **Mục tiêu:** Xây dựng giao diện nhập dữ liệu và hiển thị kết quả cho người dùng Client.
- **Yêu cầu kỹ thuật:**
  - Ô nhập tên file (ví dụ `data.txt`), ô nhập nội dung văn bản (`JTextArea` có scroll).
  - Nhóm radio button chọn chế độ lưu: `○ Văn bản (TEXT)` hoặc `○ Nhị phân (BINARY)`.
  - Nút lớn: **[ Gửi dữ liệu tới Server ]**.
  - Khung hiển thị danh sách các vị trí tìm thấy từ khóa `"HELLO"` và tự động highlight trên văn bản.
- **Tiêu chí hoàn thành (DoD):**
  - Bố cục rõ ràng, tiện dụng, không dùng `setLayout(null)`.

---

### UI-03: Xử lý gửi gói DATA & nhận kết quả trên Client
- **Mã Task:** `UI-03` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `app/src/main/java/com/detai10/client/ClientApp.java`, `app/src/main/java/com/detai10/client/ui/ClientMainFrame.java`, `app/src/main/java/com/detai10/core/protocol/UdpProtocol.java`
- **Mục tiêu:** Lập trình sự kiện gửi gói UDP DATA bằng `SwingWorker` và tiếp nhận phản hồi từ Server.
- **Yêu cầu kỹ thuật:**
  - Validate dữ liệu đầu vào: Tên file và nội dung không được để trống.
  - Sử dụng `SwingWorker` để truyền nhận mạng ngầm, không làm đơ giao diện.
  - Khóa nút gửi trong khi đang truyền tin; đặt timeout nhận phản hồi 5 giây.
  - Nhận gói `RESULT`, bóc tách vị trí và hiển thị kết quả trực quan lên giao diện.
- **Tiêu chí hoàn thành (DoD):**
  - Gửi nhận mượt mà, phản hồi chính xác, xử lý timeout an toàn.

---

### VER-03: Kiểm chứng Sprint 3 (Client – Server E2E)
- **Mã Task:** `VER-03` | **Sprint:** 3 | **Độ ưu tiên:** Trung bình | **Ước lượng:** 2 SP
- **File liên quan:** `architecture/verification/M3-udp-app.md`
- **Mục tiêu:** Kiểm thử tích hợp toàn diện đầu-cuối giữa Client và Server trên môi trường thực tế.
- **Tiêu chí hoàn thành (DoD):**
  - Chạy thử nghiệm thành công cả 2 chế độ Text và Binary, CSDL ghi nhận chính xác.

---

## SPRINT 4: ĐÓNG GÓI CD, KIỂM THỬ & BÀN GIAO (M4-release — 11 SP)

### DEP-05: Cấu hình đóng gói 2 Executable Fat JAR (udp-server.jar & udp-client.jar)
- **Mã Task:** `DEP-05` | **Sprint:** 4 | **Độ ưu tiên:** Cao | **Ước lượng:** 2 SP
- **File liên quan:** `app/build.gradle`, `scripts/build-all.bat`
- **Mục tiêu:** Cấu hình Gradle build xuất ra 2 file JAR độc lập kèm đầy đủ thư viện phụ thuộc.
- **Yêu cầu kỹ thuật:**
  - Task `serverJar`: Đóng gói `udp-server.jar` với `Main-Class: com.detai10.server.ServerApp`.
  - Task `clientJar`: Đóng gói `udp-client.jar` với `Main-Class: com.detai10.client.ClientApp`.
  - Task `shadowJar`: Đóng gói cả 2 file Fat JAR vào thư mục `app/build/libs/`.
- **Tiêu chí hoàn thành (DoD):**
  - Chạy được ứng dụng trực tiếp bằng lệnh `java -jar udp-server.jar` và `java -jar udp-client.jar`.

---

### DEP-06: Xây dựng CD Workflow (java-cd-release.yml)
- **Mã Task:** `DEP-06` | **Sprint:** 4 | **Độ ưu tiên:** Trung bình | **Ước lượng:** 2 SP
- **File liên quan:** `.github/workflows/java-cd-release.yml`
- **Mục tiêu:** Tự động build và tạo bản phát hành (GitHub Release) khi gắn thẻ tag phiên bản mới.
- **Yêu cầu kỹ thuật:**
  - Kích hoạt khi push tag `v*` (ví dụ `v1.0.0`).
  - Tự động biên dịch, đóng gói 2 Fat JAR và đính kèm vào GitHub Release.
- **Tiêu chí hoàn thành (DoD):**
  - Tự động tạo Release trên GitHub kèm 2 file JAR khi đẩy tag.

---

### VER-04: Kiểm thử tải nhẹ & Nghiệm thu bản Release
- **Mã Task:** `VER-04` | **Sprint:** 4 | **Độ ưu tiên:** Cao | **Ước lượng:** 3 SP
- **File liên quan:** `architecture/verification/M4-release.md`
- **Mục tiêu:** Kiểm thử khả năng chịu tải nhẹ khi 2–3 Client gửi dữ liệu đồng thời và nghiệm thu file JAR.
- **Yêu cầu kỹ thuật:**
  - Mở 2–3 Client gửi dữ liệu liên tục tới Server; xác nhận Server xử lý đa luồng an toàn không crash.
  - Kiểm thử các ca biên: Nhập sai Key giải mã, gửi file dung lượng lớn.
  - Tải trực tiếp file `.jar` từ GitHub Release để chạy kiểm tra.
- **Tiêu chí hoàn thành (DoD):**
  - Toàn bộ ca kiểm thử tải đạt yêu cầu, hồ sơ nghiệm thu M4 hoàn tất.

---

### DEP-07: Hoàn thiện README.md, START.md & tài liệu báo cáo đồ án
- **Mã Task:** `DEP-07` | **Sprint:** 4 | **Độ ưu tiên:** Thấp | **Ước lượng:** 2 SP
- **File liên quan:** `README.md`, `START.md`, `architecture/docs/CODING_STANDARDS.md`, `architecture/docs/TESTING_GUIDE.md`, `architecture/docs/UI_UX_DESIGN_GUIDELINES.md`
- **Mục tiêu:** Hoàn thiện bộ tài liệu hướng dẫn và tài liệu thuyết minh phục vụ bảo vệ đồ án.
- **Yêu cầu kỹ thuật:**
  - Cập nhật đầy đủ hướng dẫn cài đặt, chạy ứng dụng, sơ đồ kiến trúc, kịch bản demo và câu hỏi vấn đáp.
- **Tiêu chí hoàn thành (DoD):**
  - Bộ tài liệu hoàn chỉnh, sẵn sàng cho buổi bảo vệ đồ án.
