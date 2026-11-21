# TÀI LIỆU MÔ TẢ CHỨC NĂNG CHI TIẾT CỦA TỪNG TASK (TASK SPECIFICATIONS)

> **Dự án:** Vigen UDP System — Hệ thống Client – Server Quản lý & Truyền nhận Dữ liệu Mã hóa qua Giao thức UDP  
> **Phiên bản:** 1.0  
> **Phương pháp phát triển:** Spec-driven & AI-ready Development (Chuẩn Clucky)  
> **Ngôn ngữ:** Java 17 | **Giao thức:** UDP (DatagramSocket) | **Giao diện:** Java Swing  

---

## MỤC LỤC
- [SPRINT 1: KHỞI TẠO NỀN TẢNG & CI/CD](#sprint-1-khởi-tạo-nền-tảng--cicd)
  - [DEP-01 — Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh](#dep-01)
  - [DEP-02 — Viết tài liệu đặc tả & sơ đồ Mermaid](#dep-02)
  - [DEP-03 — Thiết kế & khởi tạo CSDL](#dep-03)
  - [DEP-04 — Thiết lập GitHub Actions CI & Branch Protection](#dep-04)
  - [VER-01 — Kiểm chứng Sprint 1 (Setup & CI)](#ver-01)
- [SPRINT 2: CORE LOGIC & KIỂM THỬ ĐƠN VỊ](#sprint-2-core-logic--kiểm-thử-đơn-vị)
  - [CORE-01 — Cài đặt bộ phân tích gói tin UDP (UdpProtocol)](#core-01)
  - [CORE-02 — Cài đặt thuật toán mã hóa & giải mã Vigenère (VigenereCipher)](#core-02)
  - [CORE-03 — Cài đặt đọc/ghi file chế độ Văn bản (FileHelper – Text Mode)](#core-03)
  - [CORE-04 — Cài đặt đọc/ghi file chế độ Nhị phân (FileHelper – Binary Mode)](#core-04)
  - [CORE-05 — Cài đặt thuật toán tìm tất cả vị trí từ HELLO (KeywordFinder)](#core-05)
  - [CORE-06 — Xây dựng tầng thao tác CSDL (DatabaseHelper)](#core-06)
  - [CORE-07 — Viết bộ Unit Test (JUnit 5) & lớp CoreSelfCheck](#core-07)
  - [VER-02 — Kiểm chứng Sprint 2 (Core Unit Test)](#ver-02)
- [SPRINT 3: GIAO DIỆN SWING & TÍCH HỢP UDP SOCKET](#sprint-3-giao-diện-swing--tích-hợp-udp-socket)
  - [SRV-01 — Thiết kế giao diện Server (ServerMainFrame)](#srv-01)
  - [SRV-02 — Xây dựng luồng nền lắng nghe UDP (UdpServerWorker)](#srv-02)
  - [SRV-03 — Tích hợp luồng xử lý gói DATA trên Server](#srv-03)
  - [UI-01 — Thiết kế & xử lý màn hình Kết nối Client (ConnectFrame)](#ui-01)
  - [UI-02 — Thiết kế màn hình làm việc chính Client (ClientMainFrame)](#ui-02)
  - [UI-03 — Xử lý gửi gói DATA & nhận kết quả trên Client](#ui-03)
  - [VER-03 — Kiểm chứng Sprint 3 (Client – Server E2E)](#ver-03)
- [SPRINT 4: ĐÓNG GÓI RELEASE & BÁO CÁO](#sprint-4-đóng-gói-release--báo-cáo)
  - [DEP-05 — Cấu hình đóng gói 2 Executable Fat JAR](#dep-05)
  - [DEP-06 — Xây dựng CD Workflow (java-cd-release.yml)](#dep-06)
  - [DEP-07 — Hoàn thiện README.md, START.md & tài liệu báo cáo đồ án](#dep-07)
  - [VER-04 — Kiểm thử tải nhẹ & Nghiệm thu bản Release](#ver-04)

---

# SPRINT 1: KHỞI TẠO NỀN TẢNG & CI/CD

<a id="dep-01"></a>
### DEP-01: Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh
- **Mã Task:** `DEP-01` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Chức năng chính:**
  - Thiết lập cấu trúc kho lưu trữ Git theo quy chuẩn Clucky Spec-driven development.
  - Tạo cấu trúc thư mục:
    - `architecture/specs/`: Tài liệu đặc tả kỹ thuật (SRS, Protocol, Cipher & I/O).
    - `architecture/tasks/`: Bộ 24 file task atomic.
    - `architecture/database/`: Scripts DDL và dữ liệu mẫu CSDL.
    - `architecture/verification/`: Checklist nghiệm thu từng Sprint (M1 đến M4).
    - `app/src/main/java/com/detai10/`: Mã nguồn phân tách thành 3 package `client`, `server`, `core`.
    - `app/src/test/java/com/detai10/core/`: Bộ Unit Test kiểm thử độc lập cho Core.
  - Cấu hình tệp `build.gradle` với JDK 17, JUnit 5 Jupiter, H2 Database Driver, MySQL Connector.
  - Khởi tạo các file quy chuẩn: `AGENTS.md` (Agent Constitution), `CONTEXT.md`, `START.md`, `.gitignore`.

---

<a id="dep-02"></a>
### DEP-02: Viết tài liệu đặc tả & sơ đồ Mermaid
- **Mã Task:** `DEP-02` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Chức năng chính:**
  - Soạn thảo 3 tài liệu đặc tả:
    1. `SRS.md`: Đặc tả yêu cầu chức năng (FR-C01..C05 cho Client, FR-S01..S08 cho Server) và yêu cầu phi chức năng (NFR-01..04).
    2. `udp-protocol.md`: Đặc tả chi tiết cấu trúc 4 loại gói UDP (`PING`, `PONG`, `DATA|<mode>|<fileName>|<content>`, `RESULT|...`, `ERROR|...`), cơ chế Safe Parsing và xử lý timeout.
    3. `vigenere-and-io.md`: Đặc tả thuật toán mã hóa/giải mã Vigenère bảo toàn ký tự và cơ chế I/O 2 chế độ (`TEXT` UTF-8 vs `BINARY` DataStream).
  - Thiết kế sơ đồ Mermaid:
    - Sequence Diagram: Quy trình tương tác Ping/Pong và gửi nhận gói Data.
    - Architecture Flowchart: Kiến trúc phân tầng và luồng dữ liệu Client $\leftrightarrow$ Server $\leftrightarrow$ Database/File.

---

<a id="dep-03"></a>
### DEP-03: Thiết kế & khởi tạo CSDL
- **Mã Task:** `DEP-03` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Chức năng chính:**
  - Thiết kế lược đồ CSDL phục vụ ghi log lịch sử và quản lý sinh viên:
    1. Bảng `lich_su_xu_ly`: Lưu `id` (PK, Auto-increment), `client_ip`, `file_name`, `file_mode` (`TEXT`/`BINARY`), `original_content`, `encrypted_content`, `hello_positions`, `created_at` (Timestamp).
    2. Bảng `sinh_vien`: Quản lý thông tin sinh viên gồm `ma_sv` (PK), `ho_ten`, `lop`, `email`, `ngay_sinh`.
  - Viết file `schema.sql` (tương thích cả MySQL và H2 Embedded/In-memory) và `sample_data.sql` với các bản ghi mẫu hợp lệ.

---

<a id="dep-04"></a>
### DEP-04: Thiết lập GitHub Actions CI & Branch Protection
- **Mã Task:** `DEP-04` | **Sprint:** 1 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Chức năng chính:**
  - Xây dựng file cấu hình CI `.github/workflows/java-ci.yml`.
  - Tự động kích hoạt khi có `push` hoặc `pull_request` vào nhánh `main`.
  - Môi trường thực thi: Ubuntu Latest (Headless Runner), cài đặt JDK 17 Eclipse Temurin.
  - Các bước thực thi: Checkout code, cấp quyền thực thi cho `./gradlew`, biên dịch mã nguồn, chạy toàn bộ Unit Test bằng `./gradlew test --no-daemon`, tải lên Test Report Artifacts.
  - Đảm bảo kiểm thử headless không phụ thuộc vào màn hình hiển thị đồ họa.

---

<a id="ver-01"></a>
### VER-01: Kiểm chứng Sprint 1 (Setup & CI)
- **Mã Task:** `VER-01` | **Sprint:** 1 (M1-foundation) | **Độ ưu tiên:** Trung bình | **Story Points:** 1 SP
- **Chức năng chính:**
  - Nghiệm thu chất lượng giai đoạn Milestone 1 (Foundation).
  - Kiểm tra tính đầy đủ của tài liệu đặc tả, tính đúng đắn của schema SQL.
  - Kiểm tra tính hợp lệ của workflow CI trên GitHub Actions và đảm bảo pipeline pass 100%.
  - Cập nhật kết quả nghiệm thu, commit SHA và bằng chứng vào `architecture/verification/M1-foundation.md`.

---

# SPRINT 2: CORE LOGIC & KIỂM THỬ ĐƠN VỊ

<a id="core-01"></a>
### CORE-01: Cài đặt bộ phân tích gói tin UDP (UdpProtocol)
- **Mã Task:** `CORE-01` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Story Points:** 1 SP
- **Vị trí file:** `app/src/main/java/com/detai10/core/protocol/UdpProtocol.java`
- **Mô tả chức năng chi tiết:**
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

---

<a id="core-02"></a>
### CORE-02: Cài đặt thuật toán mã hóa & giải mã Vigenère (VigenereCipher)
- **Mã Task:** `CORE-02` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:** `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`
- **Mô tả chức năng chi tiết:**
  - **Quy tắc toán học:**
    - Mã hóa: $C_i = (P_i + K_j) \pmod{26}$.
    - Giải mã: $P_i = (C_i - K_j + 26) \pmod{26}$.
  - **Bảo toàn ký tự (Character Preservation):**
    - Chữ hoa `A-Z`: Dịch vòng tròn trong khoảng `['A', 'Z']`.
    - Chữ thường `a-z`: Dịch vòng tròn trong khoảng `['a', 'z']`.
    - Ký tự số (`0-9`), ký tự có dấu, ký hiệu đặc biệt, dấu câu, khoảng trắng, ký tự xuống dòng: **Giữ nguyên 100% không biến đổi**.
    - **Quan trọng:** Con trỏ bước nhảy khóa ($j$) **không được tăng** khi gặp các ký tự không phải chữ cái tiếng Anh.
  - **Xử lý Khóa (Key):**
    - Khóa là chuỗi chữ cái không phân biệt hoa thường. Tự động chuẩn hóa khóa (lọc bỏ các ký tự không hợp lệ).
    - Ném ngoại lệ `IllegalArgumentException` nếu khóa rỗng (`null` hoặc `""`).

---

<a id="core-03"></a>
### CORE-03: Cài đặt đọc/ghi file chế độ Văn bản (FileHelper – Text Mode)
- **Mã Task:** `CORE-03` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Vị trí file:** `app/src/main/java/com/detai10/core/io/FileHelper.java`
- **Mô tả chức năng chi tiết:**
  - **Ghi file văn bản (`writeTextFile(File file, String content)`):**
    - Sử dụng `BufferedWriter` kết hợp `OutputStreamWriter` chỉ định bảng mã tường minh `StandardCharsets.UTF_8` (hoặc `Files.newBufferedWriter(path, StandardCharsets.UTF_8)`).
    - Ghi toàn bộ nội dung chuỗi xuống đĩa, gọi `flush()` và đóng luồng tự động bằng `try-with-resources`.
  - **Đọc file văn bản (`readTextFile(File file)`):**
    - Sử dụng `BufferedReader` kết hợp `InputStreamReader` bảng mã `StandardCharsets.UTF_8`.
    - Đọc toàn bộ nội dung dòng kèm ký tự xuống dòng `\n`, bảo toàn nguyên vẹn tiếng Việt có dấu.
  - **Bảo đảm:** Ném `IOException` kèm thông báo ngữ cảnh khi file không tồn tại hoặc lỗi quyền truy cập.

---

<a id="core-04"></a>
### CORE-04: Cài đặt đọc/ghi file chế độ Nhị phân (FileHelper – Binary Mode)
- **Mã Task:** `CORE-04` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Vị trí file:** `app/src/main/java/com/detai10/core/io/FileHelper.java`
- **Mô tả chức năng chi tiết:**
  - **Ghi file nhị phân (`writeBinaryFile(File file, String content)`):**
    - Sử dụng luồng định kiểu có đệm:
      ```java
      try (DataOutputStream dos = new DataOutputStream(
              new BufferedOutputStream(new FileOutputStream(file)))) {
          dos.writeUTF(content);
          dos.flush();
      }
      ```
    - Lưu chuỗi theo định dạng modified UTF-8 có tiền tố độ dài 2-byte, tối ưu tốc độ đọc/ghi nhị phân.
  - **Đọc file nhị phân (`readBinaryFile(File file)`):**
    - Sử dụng luồng định kiểu có đệm:
      ```java
      try (DataInputStream dis = new DataInputStream(
              new BufferedInputStream(new FileInputStream(file)))) {
          return dis.readUTF();
      }
      ```
    - Bắt và xử lý `EOFException` an toàn khi gặp cuối file.

---

<a id="core-05"></a>
### CORE-05: Cài đặt thuật toán tìm tất cả vị trí từ HELLO (KeywordFinder)
- **Mã Task:** `CORE-05` | **Sprint:** 2 | **Độ ưu tiên:** Trung bình | **Story Points:** 1 SP
- **Vị trí file:** `app/src/main/java/com/detai10/core/search/KeywordFinder.java`
- **Mô tả chức năng chi tiết:**
  - **Tìm kiếm không phân biệt chữ hoa/chữ thường (Case-insensitive):**
    - Từ khóa tìm kiếm: `"HELLO"` (khớp với `"Hello"`, `"hello"`, `"HeLLo"`, ...).
  - **Thuật toán quét vị trí:**
    - Sử dụng `String.indexOf("HELLO", fromIndex)` trên chuỗi đã chuyển thành uppercase (hoặc dùng Regex `Pattern.compile("(?i)HELLO")`).
    - Lặp tìm kiếm bắt đầu từ `index = 0` và tăng bước `fromIndex = foundIndex + 1` cho đến khi hết chuỗi.
  - **Đầu ra:**
    - Trả về `List<Integer>` chứa tất cả các chỉ số index (0-based).
    - Cung cấp phương thức `formatPositions(List<Integer> positions)` trả về chuỗi `"0, 15, 32"` nếu tìm thấy hoặc `"NOT_FOUND"` nếu danh sách rỗng.

---

<a id="core-06"></a>
### CORE-06: Xây dựng tầng thao tác CSDL (DatabaseHelper)
- **Mã Task:** `CORE-06` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:** `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`
- **Mô tả chức năng chi tiết:**
  - **Khởi tạo và cấu hình CSDL:**
    - Hỗ trợ kết nối CSDL H2 Database (In-memory/File-based) cho môi trường kiểm thử/chạy độc lập, và MySQL cho môi trường sản phẩm.
    - Tự động chạy DDL tạo bảng `lich_su_xu_ly` và `sinh_vien` nếu chưa tồn tại (`initDatabase()`).
  - **Ghi lịch sử (`saveHistory(HistoryRecord record)`):**
    - Chèn bản ghi lịch sử xử lý gói tin gồm: `client_ip`, `file_name`, `file_mode`, `original_content`, `encrypted_content`, `hello_positions`.
    - **Quy tắc an toàn tuyệt đối:** Sử dụng 100% `PreparedStatement` với tham số `?` để ngăn chặn hoàn toàn nguy cơ SQL Injection.
  - **Truy vấn lịch sử (`getAllHistory()` / `getRecentHistory(int limit)`):**
    - Đọc danh sách lịch sử sắp xếp giảm dần theo thời gian (`ORDER BY created_at DESC`).
    - Quản lý tài nguyên an toàn bằng `try-with-resources` cho `Connection`, `PreparedStatement` và `ResultSet`.

---

<a id="core-07"></a>
### CORE-07: Viết bộ Unit Test (JUnit 5) & lớp CoreSelfCheck
- **Mã Task:** `CORE-07` | **Sprint:** 2 | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:**
  - `app/src/test/java/com/detai10/core/**/*Test.java`
  - `app/src/main/java/com/detai10/core/CoreSelfCheck.java`
- **Mô tả chức năng chi tiết:**
  - **Bộ Unit Test tự động (JUnit 5):**
    - `UdpProtocolTest`: Kiểm tra serialize/deserialize gói PING, PONG, DATA (có chứa ký tự `|`), RESULT, ERROR.
    - `VigenereCipherTest`: Kiểm tra mã hóa và giải mã với chữ hoa, chữ thường, số, dấu cách, ký tự đặc biệt, kiểm tra ném lỗi khi key không hợp lệ.
    - `FileHelperTextTest` & `FileHelperBinaryTest`: Sử dụng thư mục tạm `@TempDir` của JUnit 5 để kiểm tra tính toàn vẹn khi ghi và đọc lại file.
    - `KeywordFinderTest`: Kiểm tra chuỗi rỗng, không có HELLO, có 1 HELLO, có nhiều HELLO nằm rải rác hoặc liên tiếp.
    - `DatabaseHelperTest`: Kiểm tra ghi và đọc bản ghi trên H2 in-memory.
  - **Lớp CoreSelfCheck:**
    - Có hàm `main(String[] args)` thực thi nhanh chuỗi kiểm tra độc lập trên terminal không cần UI và không cần Gradle, in ra kết quả `[PASS]` xanh toàn bộ.

---

<a id="ver-02"></a>
### VER-02: Kiểm chứng Sprint 2 (Core Unit Test)
- **Mã Task:** `VER-02` | **Sprint:** 2 (M2-core-logic) | **Độ ưu tiên:** Trung bình | **Story Points:** 1 SP
- **Chức năng chính:**
  - Nghiệm thu Milestone 2 (Core Logic).
  - Chạy lệnh `./gradlew test` trên console, kiểm tra 100% test cases đều pass (xanh).
  - Đo lường độ bao phủ (code coverage) của các lớp Core.
  - Cập nhật log kiểm thử chi tiết và trạng thái nghiệm thu vào `architecture/verification/M2-core-logic.md`.

---

# SPRINT 3: GIAO DIỆN SWING & TÍCH HỢP UDP SOCKET

<a id="srv-01"></a>
### SRV-01: Thiết kế giao diện Server (ServerMainFrame)
- **Mã Task:** `SRV-01` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:** `app/src/main/java/com/detai10/server/ui/ServerMainFrame.java`
- **Mô tả chức năng chi tiết:**
  - **Bố cục giao diện quản trị Server:**
    - Vùng Cấu hình: Ô nhập Port lắng nghe (mặc định 9876), Ô nhập Khóa Vigenère bí mật của Server, Nút "Bắt đầu Server" (Start) và "Dừng Server" (Stop).
    - Vùng Trạng thái: Đèn tín hiệu hiển thị `ONLINE` (màu xanh lá) khi server đang chạy hoặc `OFFLINE` (màu đỏ) khi dừng.
    - Vùng Lịch sử xử lý: Bảng `JTable` hiển thị danh sách các gói tin đã xử lý gồm các cột: STT, IP Client, Tên file, Chế độ (`TEXT`/`BINARY`), Nội dung gốc, Nội dung mã hóa, Vị trí HELLO, Thời gian.
    - Vùng Console Log: `JTextArea` có thanh cuộn (`JScrollPane`) ghi vết thời gian thực mọi hoạt động nhận/gửi gói tin UDP.
  - **Đảm bảo:** Nút Start bị vô hiệu hóa khi Server đang chạy, nút Stop chỉ bật khi Server đang Online.

---

<a id="srv-02"></a>
### SRV-02: Xây dựng luồng nền lắng nghe UDP (UdpServerWorker)
- **Mã Task:** `SRV-02` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:** `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`
- **Mô tả chức năng chi tiết:**
  - **Luồng lắng nghe không chặn giao diện (Non-blocking):**
    - Chạy trên một `Thread` độc lập. Mở `DatagramSocket` lắng nghe trên Port đã cấu hình với mảng byte buffer kích thước 65507 bytes.
    - Vòng lặp `receive(DatagramPacket packet)` liên tục đón gói tin đến từ các Client.
  - **Phản hồi Healthcheck tức thì:**
    - Khi nhận được nội dung `"PING"`: Ngay lập tức tạo gói `PONG` và gửi ngược lại đúng địa chỉ IP và Port của Client gửi tới.
  - **Cập nhật giao diện an toàn:**
    - Mọi thao tác ghi log lên `JTextArea` hoặc thêm hàng vào `JTable` của `ServerMainFrame` bắt buộc phải được bọc trong `SwingUtilities.invokeLater()`.
  - **Dừng socket an toàn:** Phương thức `stopServer()` đóng socket an toàn, xử lý ngoại lệ `SocketException` mà không làm crash ứng dụng.

---

<a id="srv-03"></a>
### SRV-03: Tích hợp luồng xử lý gói DATA trên Server
- **Mã Task:** `SRV-03` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Story Points:** 5 SP
- **Vị trí file:**
  - `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`
  - `app/src/main/java/com/detai10/server/ui/ServerMainFrame.java`
- **Mô tả chức năng chi tiết:**
  - **Chu trình xử lý trọn gói (End-to-End Processing Pipeline):**
    1. Tiếp nhận gói tin `DATA|<mode>|<fileName>|<content>` từ Client.
    2. Bóc tách an toàn 4 phần tử bằng `UdpProtocol.parsePacket()`.
    3. Gọi `VigenereCipher.encrypt(content, serverKey)` để mã hóa nội dung.
    4. Ghi nội dung đã mã hóa xuống thư mục lưu trữ của Server theo chế độ `mode`:
       - `TEXT`: Ghi bằng `FileHelper.writeTextFile()`.
       - `BINARY`: Ghi bằng `FileHelper.writeBinaryFile()`.
    5. Đọc lại nội dung từ file vừa ghi để kiểm chứng tính toàn vẹn (Round-trip verification).
    6. Giải mã nội dung đọc từ file bằng `VigenereCipher.decrypt(readContent, serverKey)` thu lại chuỗi gốc.
    7. Quét tìm tất cả các vị trí xuất hiện của từ khóa `"HELLO"` trong chuỗi bằng `KeywordFinder.findPositions()`.
    8. Tạo đối tượng `HistoryRecord` và lưu vào CSDL qua `DatabaseHelper.saveHistory()`.
    9. Đóng gói kết quả thành `RESULT|FOUND|<positions>` (hoặc `RESULT|NOT_FOUND|...`) và gửi ngược lại cho Client qua `DatagramSocket.send()`.
    10. Đẩy dữ liệu lên bảng `JTable` và ghi console log trên `ServerMainFrame`.

---

<a id="ui-01"></a>
### UI-01: Thiết kế & xử lý màn hình Kết nối Client (ConnectFrame)
- **Mã Task:** `UI-01` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:**
  - `app/src/main/java/com/detai10/client/ui/ConnectFrame.java`
  - `app/src/main/java/com/detai10/client/ClientApp.java`
- **Mô tả chức năng chi tiết:**
  - **Giao diện thăm dò kết nối:**
    - Cửa sổ nhỏ gọn, ở giữa màn hình.
    - Ô nhập `Server IP` (giá trị mặc định: `127.0.0.1`).
    - Ô nhập `Port` (giá trị mặc định: `9876`).
    - Nút bấm: "Kết nối" (Connect) và "Thoát" (Exit).
  - **Cơ chế Healthcheck Ping/Pong:**
    - Khi bấm "Kết nối": Vô hiệu hóa nút bấm, mở socket gửi gói `PING` tới Server.
    - Cấu hình thời gian chờ tối đa 3 giây: `socket.setSoTimeout(3000)`.
    - **Thành công:** Khi nhận được `PONG`, hiển thị thông báo "Kết nối Server thành công!", đóng `ConnectFrame` và mở `ClientMainFrame` truyền kèm thông tin kết nối.
    - **Thất bại:** Nếu quá 3 giây không có phản hồi hoặc xảy ra lỗi mạng: Hiển thị thông báo `JOptionPane.showMessageDialog` cảnh báo Server không hoạt động hoặc sai địa chỉ, khôi phục trạng thái nút bấm để người dùng thử lại.

---

<a id="ui-02"></a>
### UI-02: Thiết kế màn hình làm việc chính Client (ClientMainFrame)
- **Mã Task:** `UI-02` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Vị trí file:** `app/src/main/java/com/detai10/client/ui/ClientMainFrame.java`
- **Mô tả chức năng chi tiết:**
  - **Bố cục giao diện nhập liệu Client:**
    - Thanh thông tin kết nối: Hiển thị Server IP và Port đang kết nối.
    - Tùy chọn Chế độ lưu file: 2 Radio Button `File Văn bản (.txt)` và `File Nhị phân (.dat)` (được gom nhóm bằng `ButtonGroup`).
    - Ô nhập Tên file: `JTextField` cho phép đặt tên file sẽ lưu trên Server (mặc định: `data.txt` hoặc `data.dat`).
    - Vùng nhập văn bản: `JTextArea` trong `JScrollPane` cho phép người dùng gõ văn bản tùy ý.
    - Nút chức năng file: Nút "Chọn file từ máy..." (`JFileChooser`) để tải nội dung văn bản từ một file có sẵn vào `JTextArea`.
    - Vùng hiển thị kết quả từ Server: `JTextArea` hiển thị trạng thái kết quả và danh sách các vị trí tìm thấy từ khóa `"HELLO"`.
    - Các nút điều khiển: "Gửi dữ liệu" (Send), "Xóa trắng" (Clear), "Đổi Server" (Quay lại ConnectFrame).

---

<a id="ui-03"></a>
### UI-03: Xử lý gửi gói DATA & nhận kết quả trên Client
- **Mã Task:** `UI-03` | **Sprint:** 3 | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:** `app/src/main/java/com/detai10/client/ui/ClientMainFrame.java`
- **Mô tả chức năng chi tiết:**
  - **Kiểm tra dữ liệu đầu vào (Validation):**
    - Kiểm tra tên file không được để trống.
    - Kiểm tra nội dung văn bản không được để trống.
  - **Gửi và nhận phi đồng bộ (Background SwingWorker):**
    - Chạy tác vụ gửi UDP trên `SwingWorker` để giữ giao diện luôn mượt mà.
    - Đóng gói dữ liệu bằng `UdpProtocol.createDataPacket(mode, fileName, content)`.
    - Mở `DatagramSocket`, gửi gói tin tới Server và thiết lập timeout 5 giây (`setSoTimeout(5000)`).
    - Đợi nhận gói `RESULT` hoặc `ERROR` phản hồi từ Server.
  - **Hiển thị kết quả:**
    - Khi nhận được `RESULT|FOUND|<positions>`: Hiển thị thông báo tìm thấy kèm danh sách index nổi bật trên giao diện.
    - Khi nhận được `RESULT|NOT_FOUND|...`: Hiển thị thông báo không tìm thấy từ khóa "HELLO".
    - Khi gặp lỗi hoặc timeout: Hiển thị hộp thoại cảnh báo lỗi rõ ràng.

---

<a id="ver-03"></a>
### VER-03: Kiểm chứng Sprint 3 (Client – Server E2E)
- **Mã Task:** `VER-03` | **Sprint:** 3 (M3-udp-app) | **Độ ưu tiên:** Trung bình | **Story Points:** 2 SP
- **Chức năng chính:**
  - Nghiệm thu Milestone 3 (Client – Server E2E Integration).
  - Kiểm thử kịch bản:
    1. Khởi động Client khi Server chưa bật $\rightarrow$ Báo lỗi timeout chính xác trong 3s.
    2. Bật Server $\rightarrow$ Kết nối Client thành công.
    3. Gửi văn bản có chứa từ "HELLO" ở chế độ `TEXT` $\rightarrow$ Server mã hóa, lưu file, ghi CSDL, trả về đúng danh sách vị trí.
    4. Gửi văn bản ở chế độ `BINARY` $\rightarrow$ File nhị phân lưu đúng, CSDL ghi nhận đúng.
  - Ghi nhận biên bản nghiệm thu vào `architecture/verification/M3-udp-app.md`.

---

# SPRINT 4: ĐÓNG GÓI RELEASE & BÁO CÁO

<a id="dep-05"></a>
### DEP-05: Cấu hình đóng gói 2 Executable Fat JAR
- **Mã Task:** `DEP-05` | **Sprint:** 4 | **Độ ưu tiên:** Cao | **Story Points:** 2 SP
- **Vị trí file:** `app/build.gradle` (hoặc `build.gradle`)
- **Mô tả chức năng chi tiết:**
  - Cấu hình 2 Gradle task tùy biến để đóng gói ứng dụng:
    1. `serverJar`:
       - Main-Class: `com.detai10.server.ServerApp`
       - Tên file xuất ra: `udp-server.jar`
       - Tích hợp toàn bộ runtime dependencies (H2 Database Driver, MySQL Connector).
    2. `clientJar`:
       - Main-Class: `com.detai10.client.ClientApp`
       - Tên file xuất ra: `udp-client.jar`
  - Đảm bảo người dùng có thể khởi chạy trực tiếp trên bất kỳ máy nào có cài JDK 17 bằng lệnh:
    `java -jar udp-server.jar` và `java -jar udp-client.jar`.

---

<a id="dep-06"></a>
### DEP-06: Xây dựng CD Workflow (java-cd-release.yml)
- **Mã Task:** `DEP-06` | **Sprint:** 4 | **Độ ưu tiên:** Trung bình | **Story Points:** 2 SP
- **Vị trí file:** `.github/workflows/java-cd-release.yml`
- **Mô tả chức năng chi tiết:**
  - Xây dựng quy trình tự động phân phối (Continuous Deployment - CD).
  - **Sự kiện kích hoạt (Trigger):** Tự động kích hoạt khi có Git Tag mới theo định dạng phiên bản `v*.*.*` (ví dụ: `v1.0.0`).
  - **Các bước thực thi:**
    1. Checkout source code.
    2. Thiết lập môi trường JDK 17 Eclipse Temurin.
    3. Biên dịch và đóng gói 2 Fat JAR bằng lệnh `./gradlew serverJar clientJar`.
    4. Sử dụng GitHub Action `softprops/action-gh-release` để tự động tạo một GitHub Release mới tương ứng với tag, đính kèm 2 file `udp-server.jar` và `udp-client.jar`.

---

<a id="dep-07"></a>
### DEP-07: Hoàn thiện README.md, START.md & tài liệu báo cáo đồ án
- **Mã Task:** `DEP-07` | **Sprint:** 4 | **Độ ưu tiên:** Thấp | **Story Points:** 2 SP
- **Vị trí file:** `README.md`, `START.md`
- **Mô tả chức năng chi tiết:**
  - **`START.md` (Quick Start Guide):** Hướng dẫn từng bước cách biên dịch, thiết lập CSDL, khởi chạy Server, khởi chạy Client và kịch bản chạy thử nghiệm thực tế trong vòng dưới 3 phút.
  - **`README.md`:** Trình bày tổng quan dự án Đề tài 10, sơ đồ kiến trúc, bảng phân công nhiệm vụ 24 tasks theo Sprint, danh mục công nghệ sử dụng, và hướng dẫn đóng góp.
  - Đảm bảo toàn bộ tài liệu đồng bộ 100% với mã nguồn thực tế.

---

<a id="ver-04"></a>
### VER-04: Kiểm thử tải nhẹ & Nghiệm thu bản Release
- **Mã Task:** `VER-04` | **Sprint:** 4 (M4-release) | **Độ ưu tiên:** Cao | **Story Points:** 3 SP
- **Vị trí file:** `architecture/verification/M4-release.md`
- **Mô tả chức năng chi tiết:**
  - **Kiểm thử tải nhẹ (Lightweight Stress Test):**
    - Gửi liên tục 100 gói tin UDP từ nhiều luồng Client đồng thời.
    - Đánh giá khả năng xử lý không nghẽn của `UdpServerWorker`.
    - Kiểm tra tính toàn vẹn và không thất thoát bản ghi trong bảng `lich_su_xu_ly`.
    - Đo lường thời gian xử lý trung bình mỗi gói tin (đạt tiêu chuẩn NFR-01: < 200ms).
  - **Nghiệm thu bản Release:**
    - Kiểm tra khởi chạy độc lập 2 file Fat JAR `udp-server.jar` và `udp-client.jar`.
    - Hoàn tất biên bản nghiệm thu tổng thể Milestone 4 (Release Ready).
