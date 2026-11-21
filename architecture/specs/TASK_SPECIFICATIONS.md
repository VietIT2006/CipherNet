# TÀI LIỆU MÔ TẢ CHỨC NĂNG CHI TIẾT CỦA 30 TASK — DỰ ÁN 5 THÀNH VIÊN

> **Dự án:** Vigen UDP System — Hệ thống Client – Server Quản lý & Truyền nhận Dữ liệu Mã hóa qua Giao thức UDP  
> **Quy mô nhóm:** 5 thành viên (Mỗi thành viên phụ trách đúng **6 task**, tổng khối lượng ~**10-12 SP**)  
> **Nguyên tắc phân rã:** Tuyệt đối không để task nào làm quá nhiều việc (100% các task đều là atomic task: 1-2 SP)  

---

## BẢNG TỔNG HỢP PHÂN CÔNG 5 THÀNH VIÊN

| Thành viên | Vai trò chính | Số task | Tổng SP | Danh sách task phụ trách |
| :--- | :--- | :---: | :---: | :--- |
| **Thành viên 1** | DevOps & Tech Lead | **6** | **12 SP** | `DEP-01`, `DEP-04`, `CORE-01`, `VER-03`, `DEP-05`, `DEP-06` |
| **Thành viên 2** | Backend & Cryptography / I/O | **6** | **12 SP** | `DEP-02`, `CORE-02`, `CORE-03`, `CORE-04`, `SRV-03`, `DEP-07` |
| **Thành viên 3** | Database Engineer & Search | **6** | **12 SP** | `DEP-03`, `CORE-05`, `CORE-06`, `SRV-04`, `UI-04`, `VER-04` |
| **Thành viên 4** | Server Networking & Server UI | **6** | **11 SP** | `VER-01`, `CORE-07`, `SRV-01`, `SRV-02`, `SRV-05`, `DEP-08` |
| **Thành viên 5** | Client UI & Integration QA | **6** | **10 SP** | `DOC-01`, `CORE-08`, `VER-02`, `UI-01`, `UI-02`, `UI-03` |
| **TỔNG CỘNG** | **Cả nhóm 5 người** | **30 tasks** | **57 SP** | **Phân bổ đều 100%, không task nào quá 2 SP** |

---



# SPRINT 1: KHỞI TẠO NỀN TẢNG & CI/CD (Milestone 1)

---

# DEP-01 — Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh

- **Mã Task:** `DEP-01`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Khởi tạo cấu trúc dự án chuẩn Clucky theo mô hình Spec-driven & AI-ready, thiết lập các file ngữ cảnh và khung dự án Java 17.

## 2. File liên quan (Affected Files)
- `architecture/, app/, AGENTS.md, CONTEXT.md, START.md, .gitignore, build.gradle`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Tạo cây thư mục architecture/ (specs, tasks, diagrams, database, verification) và app/src/main/java, app/src/test/java. Cấu hình Gradle build với JDK 17, JUnit 5, JDBC driver.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Cây thư mục hoàn chỉnh, các file tài liệu nền tảng sẵn sàng, Gradle build thành công.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
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

# DEP-02 — Viết tài liệu đặc tả & sơ đồ Mermaid

- **Mã Task:** `DEP-02`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Quy chuẩn hóa tài liệu kỹ thuật về giao thức UDP (PING/PONG, DATA), thuật toán Vigenère, luồng File I/O và các sơ đồ kiến trúc.

## 2. File liên quan (Affected Files)
- `architecture/specs/SRS.md, udp-protocol.md, vigenere-and-io.md, architecture/diagrams/*.mmd`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Viết định dạng Markdown và Mermaid .mmd trực quan hóa kiến trúc hệ thống, luồng tuần tự gói tin, quy trình xử lý tại Server và mã hóa Vigenère.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Có đủ 3 file đặc tả kỹ thuật và 4 sơ đồ Mermaid .mmd.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Soạn thảo 3 tài liệu đặc tả:
    1. `SRS.md`: Đặc tả yêu cầu chức năng (FR-C01..C05 cho Client, FR-S01..S08 cho Server) và yêu cầu phi chức năng (NFR-01..04).
    2. `udp-protocol.md`: Đặc tả chi tiết cấu trúc 4 loại gói UDP (`PING`, `PONG`, `DATA|<mode>|<fileName>|<content>`, `RESULT|...`, `ERROR|...`), cơ chế Safe Parsing và xử lý timeout.
    3. `vigenere-and-io.md`: Đặc tả thuật toán mã hóa/giải mã Vigenère bảo toàn ký tự và cơ chế I/O 2 chế độ (`TEXT` UTF-8 vs `BINARY` DataStream).
  - Thiết kế sơ đồ Mermaid:
    - Sequence Diagram: Quy trình tương tác Ping/Pong và gửi nhận gói Data.
    - Architecture Flowchart: Kiến trúc phân tầng và luồng dữ liệu Client $\leftrightarrow$ Server $\leftrightarrow$ Database/File.

---

# DOC-01 — Thiết kế sơ đồ kiến trúc & luồng dữ liệu Mermaid

- **Mã Task:** `DOC-01`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 5 (QA / Client Dev)

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng hệ thống sơ đồ kiến trúc hệ thống và sơ đồ tuần tự (Sequence Diagram) bằng ngôn ngữ Mermaid để trực quan hóa luồng trao đổi dữ liệu.

## 2. File liên quan (Affected Files)
- `architecture/diagrams/01-system-architecture.mmd`
- `architecture/diagrams/02-udp-protocol-flow.mmd`
- `architecture/diagrams/03-server-processing-flow.mmd`
- `architecture/diagrams/04-vigenere-cipher-flow.mmd`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Vẽ Sequence Diagram mô tả quá trình gửi Ping/Pong và trao đổi gói DATA/RESULT giữa Client và Server.
- Vẽ Flowchart kiến trúc phân tầng (Client GUI, Network UDP, Server Worker, Core Engine, Database).
- Kiểm tra tính tương thích cú pháp Mermaid trên GitHub Markdown.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Tối thiểu 3 sơ đồ Mermaid hiển thị chuẩn xác không lỗi cú pháp.
- [ ] Trực quan hóa đầy đủ chu trình xử lý và kiến trúc hệ thống.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Thiết kế sơ đồ kiến trúc phân tầng: Tách biệt rõ ràng Client (GUI, DatagramSocket), Mạng UDP, Server (Worker Thread, UI), Tầng Core (Cipher, IO, Search, DB).
- Thiết kế sơ đồ tuần tự: Thể hiện rõ thứ tự gói tin PING -> PONG, DATA -> RESULT, và các nhánh ngoại lệ Timeout, ERROR.

---

# DEP-03 — Thiết kế & khởi tạo CSDL

- **Mã Task:** `DEP-03`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Thiết kế lược đồ cơ sở dữ liệu lưu lịch sử xử lý gói tin và dữ liệu sinh viên kiểm thử.

## 2. File liên quan (Affected Files)
- `architecture/database/schema.sql, sample_data.sql, README.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Tạo bảng lich_su_xu_ly lưu IP Client, tên file, chế độ, nội dung gốc, nội dung mã hóa, vị trí HELLO, thời gian; bảng bổ trợ sinh_vien. Hỗ trợ UTF-8 (utf8mb4).

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Chạy thành công schema.sql và sample_data.sql trên MySQL/SQL Server không lỗi.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Thiết kế lược đồ CSDL phục vụ ghi log lịch sử và quản lý sinh viên:
    1. Bảng `lich_su_xu_ly`: Lưu `id` (PK, Auto-increment), `client_ip`, `file_name`, `file_mode` (`TEXT`/`BINARY`), `original_content`, `encrypted_content`, `hello_positions`, `created_at` (Timestamp).
    2. Bảng `sinh_vien`: Quản lý thông tin sinh viên gồm `ma_sv` (PK), `ho_ten`, `lop`, `email`, `ngay_sinh`.
  - Viết file `schema.sql` (tương thích cả MySQL và H2 Embedded/In-memory) và `sample_data.sql` với các bản ghi mẫu hợp lệ.

---

# DEP-04 — Thiết lập GitHub Actions CI & Branch Protection

- **Mã Task:** `DEP-04`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Thiết lập quy trình tích hợp liên tục tự động kiểm tra cú pháp, biên dịch và chạy JUnit 5 khi có thay đổi code.

## 2. File liên quan (Affected Files)
- `.github/workflows/java-ci.yml`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Viết workflow chạy trên ubuntu-latest, cài đặt JDK 17, thực thi ./gradlew test, upload test report artifact.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Workflow CI được kích hoạt và chạy xanh thành công khi push/PR vào nhánh develop/main.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Xây dựng file cấu hình CI `.github/workflows/java-ci.yml`.
  - Tự động kích hoạt khi có `push` hoặc `pull_request` vào nhánh `main`.
  - Môi trường thực thi: Ubuntu Latest (Headless Runner), cài đặt JDK 17 Eclipse Temurin.
  - Các bước thực thi: Checkout code, cấp quyền thực thi cho `./gradlew`, biên dịch mã nguồn, chạy toàn bộ Unit Test bằng `./gradlew test --no-daemon`, tải lên Test Report Artifacts.
  - Đảm bảo kiểm thử headless không phụ thuộc vào màn hình hiển thị đồ họa.

---

# VER-01 — Kiểm chứng Sprint 1 (Setup & CI)

- **Mã Task:** `VER-01`
- **Sprint:** 1 (M1-foundation)
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 1 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm chứng toàn diện các kết quả chuyển giao của Sprint 1, đảm bảo hạ tầng CI và tài liệu kiến trúc đạt chuẩn.

## 2. File liên quan (Affected Files)
- `architecture/verification/M1-foundation.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Kiểm tra tính đầy đủ của bộ tài liệu đặc tả, sơ đồ Mermaid và DDL database.
- Xác nhận workflow CI trên GitHub Actions chạy xanh 100%.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [x] Lưu bằng chứng chạy xanh workflow CI đầu tiên và import CSDL thành công vào file `architecture/verification/M1-foundation.md`.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Nghiệm thu chất lượng giai đoạn Milestone 1 (Foundation).
  - Kiểm tra tính đầy đủ của tài liệu đặc tả, tính đúng đắn của schema SQL.
  - Kiểm tra tính hợp lệ của workflow CI trên GitHub Actions và đảm bảo pipeline pass 100%.
  - Cập nhật kết quả nghiệm thu, commit SHA và bằng chứng vào `architecture/verification/M1-foundation.md`.

---

# SPRINT 2: CORE LOGIC & KIỂM THỬ ĐƠN VỊ

<a id="core-01"></a>

---


# SPRINT 2: CORE LOGIC & KIỂM THỬ ĐƠN VỊ (Milestone 2)

---

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

---

# CORE-02 — Cài đặt thuật toán mã hóa & giải mã Vigenère (VigenereCipher)

- **Mã Task:** `CORE-02`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Hiện thực thuật toán mã hóa và giải mã Vigenère đối xứng bằng Java.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Hàm encrypt(text, key) và decrypt(cipherText, key). Biến đổi chữ cái hoa/thường theo mod 26; bảo toàn nguyên vẹn số, dấu cách, ký tự đặc biệt.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Văn bản sau khi mã hóa rồi giải mã khớp hoàn toàn với văn bản gốc.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
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

# CORE-03 — Cài đặt đọc/ghi file chế độ Văn bản (FileHelper – Text Mode)

- **Mã Task:** `CORE-03`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng hàm đọc và ghi file văn bản thuần túy theo chuẩn UTF-8.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/io/FileHelper.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Sử dụng BufferedWriter và BufferedReader. Tự động tạo thư mục cha nếu chưa tồn tại; đảm bảo đóng luồng an toàn bằng try-with-resources.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Ghi nội dung văn bản ra đĩa và đọc lại toàn vẹn nội dung không bị lỗi font.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Ghi file văn bản (`writeTextFile(File file, String content)`):**
    - Sử dụng `BufferedWriter` kết hợp `OutputStreamWriter` chỉ định bảng mã tường minh `StandardCharsets.UTF_8` (hoặc `Files.newBufferedWriter(path, StandardCharsets.UTF_8)`).
    - Ghi toàn bộ nội dung chuỗi xuống đĩa, gọi `flush()` và đóng luồng tự động bằng `try-with-resources`.
  - **Đọc file văn bản (`readTextFile(File file)`):**
    - Sử dụng `BufferedReader` kết hợp `InputStreamReader` bảng mã `StandardCharsets.UTF_8`.
    - Đọc toàn bộ nội dung dòng kèm ký tự xuống dòng `\n`, bảo toàn nguyên vẹn tiếng Việt có dấu.
  - **Bảo đảm:** Ném `IOException` kèm thông báo ngữ cảnh khi file không tồn tại hoặc lỗi quyền truy cập.

---

# CORE-04 — Cài đặt đọc/ghi file chế độ Nhị phân (FileHelper – Binary Mode)

- **Mã Task:** `CORE-04`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng hàm đọc và ghi file định dạng nhị phân có đệm.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/io/FileHelper.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Sử dụng luồng định kiểu DataOutputStream(BufferedOutputStream) với writeUTF() và DataInputStream(BufferedInputStream) với readUTF().

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Ghi dữ liệu nhị phân và đọc lại nguyên vẹn chuỗi dữ liệu gốc.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
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

# CORE-05 — Cài đặt thuật toán tìm tất cả vị trí từ HELLO (KeywordFinder)

- **Mã Task:** `CORE-05`
- **Sprint:** 2
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 1 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng thuật toán quét toàn bộ văn bản để tìm tất cả các vị trí xuất hiện của từ khóa HELLO.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/search/KeywordFinder.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Hàm findKeywordIndices(text, "HELLO") trả về List<Integer> đại diện cho các index 0-based. Xử lý trường hợp không tìm thấy và phân biệt/không phân biệt hoa thường theo đặc tả.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Trả về danh sách chính xác các vị trí xuất hiện của HELLO.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Tìm kiếm không phân biệt chữ hoa/chữ thường (Case-insensitive):**
    - Từ khóa tìm kiếm: `"HELLO"` (khớp với `"Hello"`, `"hello"`, `"HeLLo"`, ...).
  - **Thuật toán quét vị trí:**
    - Sử dụng `String.indexOf("HELLO", fromIndex)` trên chuỗi đã chuyển thành uppercase (hoặc dùng Regex `Pattern.compile("(?i)HELLO")`).
    - Lặp tìm kiếm bắt đầu từ `index = 0` và tăng bước `fromIndex = foundIndex + 1` cho đến khi hết chuỗi.
  - **Đầu ra:**
    - Trả về `List<Integer>` chứa tất cả các chỉ số index (0-based).
    - Cung cấp phương thức `formatPositions(List<Integer> positions)` trả về chuỗi `"0, 15, 32"` nếu tìm thấy hoặc `"NOT_FOUND"` nếu danh sách rỗng.

---

# CORE-06 — Xây dựng tầng thao tác CSDL (DatabaseHelper)

- **Mã Task:** `CORE-06`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Hiện thực tầng truy xuất dữ liệu JDBC để ghi log xử lý gói tin của Server.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Kết nối JDBC MySQL/SQL Server; hàm insertLog(...) sử dụng PreparedStatement để chống SQL Injection; quản lý kết nối an toàn.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Thực thi thêm mới bản ghi vào bảng lich_su_xu_ly thành công từ Java code.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
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

# CORE-07 — Viết bộ Unit Test JUnit 5 cho toàn bộ tầng Core

- **Mã Task:** `CORE-07`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 4 (Server / Network)

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng bộ kiểm thử đơn vị tự động bằng JUnit 5 cho tất cả các lớp trong tầng Core Logic, đảm bảo độ bao phủ cao và chạy độc lập trên môi trường Linux Headless CI.

## 2. File liên quan (Affected Files)
- `app/src/test/java/com/detai10/core/protocol/UdpProtocolTest.java`
- `app/src/test/java/com/detai10/core/cipher/VigenereCipherTest.java`
- `app/src/test/java/com/detai10/core/io/FileHelperTextTest.java`
- `app/src/test/java/com/detai10/core/io/FileHelperBinaryTest.java`
- `app/src/test/java/com/detai10/core/search/KeywordFinderTest.java`
- `app/src/test/java/com/detai10/core/db/DatabaseHelperTest.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Kiểm thử toàn diện các ca biên: Chuỗi rỗng, null, ký tự đặc biệt, tiếng Việt UTF-8.
- Sử dụng `@TempDir` của JUnit 5 cho các test đọc/ghi file để tự động xóa sạch file rác sau khi chạy.
- Sử dụng CSDL H2 in-memory cho `DatabaseHelperTest` để không phụ thuộc vào dịch vụ CSDL ngoài.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Chạy lệnh `./gradlew test` xanh 100%.
- [ ] Tỷ lệ bao phủ kiểm thử (Test Coverage) tầng Core đạt trên 85%.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Nền tảng đảm bảo chất lượng phần mềm theo tôn chỉ Spec-driven: Mọi logic nghiệp vụ cốt lõi đều được tự động xác minh trước khi bước vào giai đoạn ghép nối giao diện.

---

# CORE-08 — Xây dựng lớp CoreSelfCheck kiểm tra nhanh độc lập trên console

- **Mã Task:** `CORE-08`
- **Sprint:** 2
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 1 SP
- **Người thực hiện:** Thành viên 5 (QA / Client Dev)

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng lớp `CoreSelfCheck` có phương thức `main` cho phép kiểm tra nhanh toàn bộ chức năng tầng Core trực tiếp trên dòng lệnh terminal mà không cần khởi động giao diện Swing.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/CoreSelfCheck.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Thực thi kiểm tra tự động lần lượt:
  1. `UdpProtocol`: Đóng gói và bóc tách gói tin DATA.
  2. `VigenereCipher`: Mã hóa và giải mã chuỗi mẫu.
  3. `FileHelper`: Ghi và đọc file Text UTF-8, Binary DataStream.
  4. `KeywordFinder`: Tìm vị trí từ khóa "HELLO".
  5. `DatabaseHelper`: Khởi tạo bảng và lưu thử bản ghi trên CSDL H2.
- In ra màn hình console định dạng trực quan: `[PASS] <Tên kiểm tra>` hoặc `[FAIL] <Lỗi chi tiết>`.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Chạy lệnh `java com.detai10.core.CoreSelfCheck` in ra màn hình 100% `[PASS]`.
- [ ] Không phụ thuộc vào thư viện giao diện hay môi trường đồ họa.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Lớp `CoreSelfCheck` phục vụ mục đích kiểm tra nhanh tính toàn vẹn (Sanity Check) của hệ thống trong môi trường dòng lệnh (headless).
- Trả về mã thoát (exit code) 0 khi toàn bộ kiểm tra thành công, 1 khi có lỗi.

---

# VER-02 — Kiểm chứng Sprint 2 (Core Unit Test)

- **Mã Task:** `VER-02`
- **Sprint:** 2 (M2-core-logic)
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 1 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm chứng toàn bộ các lớp thuộc Core Logic (UdpProtocol, VigenereCipher, FileHelper, KeywordFinder, DatabaseHelper) thông qua kiểm thử tự động.

## 2. File liên quan (Affected Files)
- `architecture/verification/M2-core-logic.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Chạy toàn bộ test suite JUnit 5.
- Đảm bảo tách biệt khỏi Java Swing để chạy ổn định trên Linux Headless CI.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Báo cáo test JUnit 5 đạt 100% pass trên GitHub Actions CI và được lưu lại trong `architecture/verification/M2-core-logic.md`.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Nghiệm thu Milestone 2 (Core Logic).
  - Chạy lệnh `./gradlew test` trên console, kiểm tra 100% test cases đều pass (xanh).
  - Đo lường độ bao phủ (code coverage) của các lớp Core.
  - Cập nhật log kiểm thử chi tiết và trạng thái nghiệm thu vào `architecture/verification/M2-core-logic.md`.

---

# SPRINT 3: GIAO DIỆN SWING & TÍCH HỢP UDP SOCKET

<a id="srv-01"></a>

---


# SPRINT 3: GIAO DIỆN SWING & TÍCH HỢP UDP SOCKET (Milestone 3)

---

# SRV-01 — Thiết kế giao diện Server (ServerMainFrame)

- **Mã Task:** `SRV-01`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng giao diện đồ họa quản trị Server bằng Java Swing.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/ui/ServerMainFrame.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Gồm JTextField nhập Port, JTextField nhập Key Vigenère, JButton Start/Stop, JTable hoặc JTextArea hiển thị log gói tin đến và kết quả phân tích.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Giao diện hiển thị trực quan, nhập liệu thuận tiện, quản lý trạng thái Server rõ ràng.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Bố cục giao diện quản trị Server:**
    - Vùng Cấu hình: Ô nhập Port lắng nghe (mặc định 9876), Ô nhập Khóa Vigenère bí mật của Server, Nút "Bắt đầu Server" (Start) và "Dừng Server" (Stop).
    - Vùng Trạng thái: Đèn tín hiệu hiển thị `ONLINE` (màu xanh lá) khi server đang chạy hoặc `OFFLINE` (màu đỏ) khi dừng.
    - Vùng Lịch sử xử lý: Bảng `JTable` hiển thị danh sách các gói tin đã xử lý gồm các cột: STT, IP Client, Tên file, Chế độ (`TEXT`/`BINARY`), Nội dung gốc, Nội dung mã hóa, Vị trí HELLO, Thời gian.
    - Vùng Console Log: `JTextArea` có thanh cuộn (`JScrollPane`) ghi vết thời gian thực mọi hoạt động nhận/gửi gói tin UDP.
  - **Đảm bảo:** Nút Start bị vô hiệu hóa khi Server đang chạy, nút Stop chỉ bật khi Server đang Online.

---

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

---

# SRV-03 — Tích hợp Core Processing Pipeline trên Server (Cipher, I/O, Search)

- **Mã Task:** `SRV-03`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 2 (Backend / Security)

---

## 1. Mục tiêu (Goal / User Story)
Tích hợp chuỗi xử lý nghiệp vụ Core trên Server: Tiếp nhận dữ liệu gói tin -> Mã hóa Vigenère -> Lưu & Đọc kiểm chứng File -> Giải mã -> Tìm kiếm từ khóa "HELLO".

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`
- `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`
- `app/src/main/java/com/detai10/core/io/FileHelper.java`
- `app/src/main/java/com/detai10/core/search/KeywordFinder.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Nhận mảng tham số bóc tách từ gói `DATA|<mode>|<fileName>|<content>`.
- Thực hiện mã hóa nội dung bằng `VigenereCipher.encrypt(content, serverKey)`.
- Ghi file đã mã hóa theo chế độ `mode` (`TEXT` hoặc `BINARY`) vào thư mục `server_storage/` bằng `FileHelper`.
- Đọc lại file vừa ghi để kiểm chứng tính toàn vẹn (Round-trip verification).
- Giải mã lại nội dung đọc từ file bằng `VigenereCipher.decrypt(readContent, serverKey)`.
- Quét tìm tất cả các vị trí xuất hiện của từ khóa `"HELLO"` trong chuỗi bằng `KeywordFinder.findPositions()`.
- Trả về đối tượng kết quả trung gian để bàn giao cho khâu lưu CSDL và phản hồi mạng (SRV-04).

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Xử lý trọn vẹn chu trình Cipher -> File I/O -> Search trong thời gian < 100ms.
- [ ] Bảo toàn toàn vẹn nội dung khi ghi xuống và đọc ngược lại từ file.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Khâu xử lý cốt lõi của Server: Đảm bảo dữ liệu nhận được từ Client luôn được mã hóa an toàn trước khi ghi đĩa, đồng thời kiểm tra lại tính đúng đắn trước khi bàn giao cho module lưu CSDL.

---

# SRV-04 — Tích hợp lưu trữ CSDL & Gửi gói phản hồi RESULT/ERROR

- **Mã Task:** `SRV-04`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 3 (Database Engineer)

---

## 1. Mục tiêu (Goal / User Story)
Tích hợp khâu lưu trữ bản ghi giao dịch vào CSDL và gửi gói tin phản hồi UDP (`RESULT` hoặc `ERROR`) về lại địa chỉ IP và Port của Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`
- `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`
- `app/src/main/java/com/detai10/core/db/HistoryRecord.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Khởi tạo đối tượng `HistoryRecord` chứa: `clientIp`, `fileName`, `fileMode`, `originalContent`, `encryptedContent`, `helloPositions`.
- Gọi `DatabaseHelper.saveHistory(record)` lưu vào CSDL an toàn bằng `PreparedStatement`.
- Đóng gói bản tin phản hồi:
  - Nếu tìm thấy từ khóa: `RESULT|FOUND|<danh_sách_vị_trí>`
  - Nếu không tìm thấy: `RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO`
  - Nếu gặp lỗi: `ERROR|<thông_điệp_lỗi>`
- Gửi gói tin qua `DatagramSocket.send()` về đúng IP và Port của Client yêu cầu.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Dữ liệu lưu đúng và đủ các cột trong bảng `lich_su_xu_ly`.
- [ ] Client nhận được gói tin phản hồi chính xác trong thời gian < 100ms.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Nhận kết quả từ luồng xử lý core (SRV-03), chuẩn bị dữ liệu lưu trữ.
- Gọi phương thức DAO bất đồng bộ hoặc trong worker thread để không làm nghẽn socket nhận tiếp theo.
- Tạo gói `DatagramPacket` chứa mảng byte UTF-8 của bản tin phản hồi và phát qua mạng.

---

# SRV-05 — Đồng bộ JTable dữ liệu & Realtime Console Log trên Server GUI

- **Mã Task:** `SRV-05`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 4 (Server / Network)

---

## 1. Mục tiêu (Goal / User Story)
Đồng bộ kết quả xử lý gói tin và thông tin giao dịch lên bảng `JTable` và ghi console log thời gian thực trên giao diện `ServerMainFrame` mà không gây giật lag giao diện.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/ui/ServerMainFrame.java`
- `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Định nghĩa phương thức callback hoặc event listener giữa `UdpServerWorker` và `ServerMainFrame`.
- Bắt buộc bọc mọi thao tác cập nhật GUI trong `SwingUtilities.invokeLater(() -> { ... })`.
- Thêm dòng mới vào `DefaultTableModel` của bảng lịch sử trên giao diện: STT, Client IP, Tên file, Chế độ, Nội dung gốc, Mã hóa, Vị trí HELLO, Thời gian.
- Ghi vết vào `JTextArea` Console Log: Thời gian kèm thông điệp nhận/gửi chi tiết (IP, Port, Byte count).
- Tự động cuộn xuống dòng mới nhất trên bảng và console log.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Giao diện Server cập nhật tức thì dữ liệu gói tin nhận được.
- [ ] UI mượt mà, không bị hiện tượng đơ chuột hay giật màn hình khi nhận nhiều gói liên tục.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Cơ chế non-blocking GUI updates: Tách biệt hoàn toàn luồng I/O mạng và luồng sự kiện đồ họa Event Dispatch Thread (EDT).
- Tự động giới hạn số dòng hiển thị trên Console Log để tránh tràn bộ nhớ khi chạy lâu dài.

---

# UI-01 — Thiết kế & xử lý màn hình Kết nối Client (ConnectFrame)

- **Mã Task:** `UI-01`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng màn hình nhập cấu hình kết nối ban đầu cho Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/client/ui/ConnectFrame.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Nhập IP và Port Server; nút Kết nối gửi gói PING với timeout 3 giây. Nếu nhận PONG -> mở ClientMainFrame; nếu timeout -> hiện hộp thoại thông báo lỗi.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Kiểm tra kết nối chính xác, chuyển cảnh màn hình mượt mà.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
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

# UI-02 — Thiết kế màn hình làm việc chính Client (ClientMainFrame)

- **Mã Task:** `UI-02`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng giao diện gửi nhận dữ liệu chính cho Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/client/ui/ClientMainFrame.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
JTextField tên file, JTextArea nội dung văn bản, JRadioButton chọn TEXT hoặc BINARY, JButton Gửi dữ liệu, JTextArea hiển thị kết quả từ Server.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Bố cục rõ ràng, trực quan, hỗ trợ đầy đủ các thao tác người dùng.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Bố cục giao diện nhập liệu Client:**
    - Thanh thông tin kết nối: Hiển thị Server IP và Port đang kết nối.
    - Tùy chọn Chế độ lưu file: 2 Radio Button `File Văn bản (.txt)` và `File Nhị phân (.dat)` (được gom nhóm bằng `ButtonGroup`).
    - Ô nhập Tên file: `JTextField` cho phép đặt tên file sẽ lưu trên Server (mặc định: `data.txt` hoặc `data.dat`).
    - Vùng nhập văn bản: `JTextArea` trong `JScrollPane` cho phép người dùng gõ văn bản tùy ý.
    - Nút chức năng file: Nút "Chọn file từ máy..." (`JFileChooser`) để tải nội dung văn bản từ một file có sẵn vào `JTextArea`.
    - Vùng hiển thị kết quả từ Server: `JTextArea` hiển thị trạng thái kết quả và danh sách các vị trí tìm thấy từ khóa `"HELLO"`.
    - Các nút điều khiển: "Gửi dữ liệu" (Send), "Xóa trắng" (Clear), "Đổi Server" (Quay lại ConnectFrame).

---

# UI-03 — Xử lý gửi gói DATA phi đồng bộ bằng SwingWorker trên Client

- **Mã Task:** `UI-03`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 5 (QA / Client Dev)

---

## 1. Mục tiêu (Goal / User Story)
Xử lý sự kiện nhấn nút "Gửi dữ liệu" trên Client: Kiểm tra dữ liệu hợp lệ, đóng gói `DATA`, gửi qua UDP Socket và chờ nhận phản hồi trên luồng nền `SwingWorker` để không làm đơ giao diện.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/client/ui/ClientMainFrame.java`
- `app/src/main/java/com/detai10/core/protocol/UdpProtocol.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Kiểm tra tính hợp lệ: Tên file và nội dung văn bản không được để trống.
- Khởi tạo `SwingWorker<String, Void>` để thực hiện giao tiếp mạng ngầm.
- Đóng gói bản tin bằng `UdpProtocol.createDataPacket(mode, fileName, content)`.
- Mở `DatagramSocket`, gửi gói tin tới Server và thiết lập timeout 5 giây (`socket.setSoTimeout(5000)`).
- Nhận gói tin phản hồi từ Server, chuyển giao dữ liệu chuỗi nhận được cho khâu xử lý hiển thị và highlight (UI-04).
- Xử lý các tình huống ngoại lệ mạng hoặc timeout hiển thị thông báo thân thiện.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Gửi gói tin mượt mà, giao diện không có hiện tượng giật đơ.
- [ ] Xử lý timeout 5s chuẩn xác khi Server không phản hồi.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Giữ giao diện Client luôn phản hồi linh hoạt: Vô hiệu hóa nút gửi trong khi đang truyền tin, hiển thị biểu tượng/thanh tiến trình chờ xử lý.

---

# UI-04 — Xử lý bóc tách kết quả RESULT & Highlight vị trí từ khóa trên Client GUI

- **Mã Task:** `UI-04`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 3 (Database Engineer)

---

## 1. Mục tiêu (Goal / User Story)
Xử lý bóc tách chuỗi phản hồi `RESULT` nhận được từ Server, hiển thị kết quả trực quan và tô sáng (highlight) các vị trí xuất hiện của từ khóa `"HELLO"` trong vùng văn bản trên Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/client/ui/ClientMainFrame.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Bóc tách gói tin kết quả:
  - Nếu `RESULT|FOUND|<indices>`: Tách chuỗi chỉ số index (ví dụ: `0, 15, 32`).
  - Nếu `RESULT|NOT_FOUND|...`: Hiển thị thông báo không tìm thấy từ khóa.
  - Nếu `ERROR|<msg>`: Hiển thị cảnh báo lỗi màu đỏ.
- Tô màu nền (Highlight) từ khóa trong `JTextArea`:
  - Dùng `Highlighter` và `DefaultHighlighter.DefaultHighlightPainter` (màu vàng nhạt) tô sáng đúng đoạn văn bản tương ứng với các vị trí tìm thấy.
- Hiển thị thống kê tổng số lần xuất hiện của từ khóa trên thanh trạng thái.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Bóc tách chính xác danh sách vị trí từ chuỗi phản hồi UDP.
- [ ] Các vị trí từ khóa "HELLO" được highlight đúng tọa độ ký tự trên văn bản gốc.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Chức năng trực quan hóa kết quả tìm kiếm (Visual Result Inspection) giúp người dùng nhanh chóng nhận biết các vị trí xuất hiện của từ khóa mà không cần tự đối chiếu chỉ số index thủ công.

---

# VER-03 — Kiểm chứng Sprint 3 (Client – Server E2E)

- **Mã Task:** `VER-03`
- **Sprint:** 3 (M3-udp-app)
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm chứng luồng tích hợp đầu-cuối (End-to-End) giữa Client và Server trên giao thức UDP.

## 2. File liên quan (Affected Files)
- `architecture/verification/M3-udp-app.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Kiểm thử các kịch bản: Server bật/tắt, Client gửi PING nhận PONG, Client gửi DATA với cả 2 chế độ TEXT và BINARY.
- Kiểm tra tính nhất quán dữ liệu được ghi vào CSDL và file trên đĩa.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Bằng chứng kiểm thử E2E thực tế được lưu vào `architecture/verification/M3-udp-app.md`.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
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

---


# SPRINT 4: ĐÓNG GÓI RELEASE & BÁO CÁO (Milestone 4)

---

# DEP-05 — Cấu hình đóng gói 2 Executable Fat JAR (udp-server.jar & udp-client.jar)

- **Mã Task:** `DEP-05`
- **Sprint:** 4
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Cấu hình build Gradle (ShadowJar / Application) để xuất ra 2 file JAR độc lập kèm phụ thuộc.

## 2. File liên quan (Affected Files)
- `app/build.gradle, scripts/build-jars.bat`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Cấu hình 2 manifest riêng biệt chỉ định Main-Class cho Server và Client; bundle kèm JDBC Driver để chạy độc lập.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Tạo ra udp-server.jar và udp-client.jar có thể chạy trực tiếp bằng java -jar.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
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

# DEP-06 — Xây dựng CD Workflow (java-cd-release.yml)

- **Mã Task:** `DEP-06`
- **Sprint:** 4
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Thiết lập quy trình tự động đóng gói và xuất bản release trên GitHub khi có tag mới.

## 2. File liên quan (Affected Files)
- `.github/workflows/java-cd-release.yml`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
GitHub Actions kích hoạt khi push tag v*, build 2 Fat JAR và tạo GitHub Release đính kèm file JAR.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Tự động tạo Release kèm 2 file .jar khi đẩy tag phiên bản.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Xây dựng quy trình tự động phân phối (Continuous Deployment - CD).
  - **Sự kiện kích hoạt (Trigger):** Tự động kích hoạt khi có Git Tag mới theo định dạng phiên bản `v*.*.*` (ví dụ: `v1.0.0`).
  - **Các bước thực thi:**
    1. Checkout source code.
    2. Thiết lập môi trường JDK 17 Eclipse Temurin.
    3. Biên dịch và đóng gói 2 Fat JAR bằng lệnh `./gradlew serverJar clientJar`.
    4. Sử dụng GitHub Action `softprops/action-gh-release` để tự động tạo một GitHub Release mới tương ứng với tag, đính kèm 2 file `udp-server.jar` và `udp-client.jar`.

---

# DEP-07 — Hoàn thiện README.md & START.md hướng dẫn chạy dưới 3 phút

- **Mã Task:** `DEP-07`
- **Sprint:** 4
- **Độ ưu tiên:** Thấp
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 2 (Backend / Security)

---

## 1. Mục tiêu (Goal / User Story)
Viết tài liệu hướng dẫn người dùng cuối và lập trình viên: `START.md` (Quickstart Guide) và `README.md` (Tổng quan dự án).

## 2. File liên quan (Affected Files)
- `README.md`
- `START.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- `START.md`: Hướng dẫn chi tiết từng bước:
  1. Yêu cầu môi trường (JDK 17).
  2. Cách biên dịch: `./gradlew build`.
  3. Cách chạy Server: `java -jar build/libs/udp-server.jar` hoặc `./gradlew runServer`.
  4. Cách chạy Client: `java -jar build/libs/udp-client.jar` hoặc `./gradlew runClient`.
  5. Kịch bản chạy thử nghiệm thực tế (Mã hóa văn bản chứa "HELLO", kiểm tra kết quả).
- `README.md`: Giới thiệu kiến trúc hệ thống, danh mục công nghệ, bảng phân công nhiệm vụ 5 thành viên.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Người dùng mới có thể đọc START.md và khởi chạy thành công trong vòng 3 phút.
- [ ] Thông tin trong tài liệu đồng bộ 100% với mã nguồn thực tế.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Tài liệu hóa trải nghiệm của người dùng (Developer Experience & User Onboarding): Giúp giảng viên và người đánh giá nghiệm thu sản phẩm dễ dàng nhất.

---

# DEP-08 — Soạn thảo Báo cáo kỹ thuật đồ án & Slide thuyết trình

- **Mã Task:** `DEP-08`
- **Sprint:** 4
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 4 (Server / Network)

---

## 1. Mục tiêu (Goal / User Story)
Tổng hợp toàn bộ tài liệu kiến trúc, kết quả thử nghiệm, bảng phân công nhiệm vụ và biên soạn Báo cáo kỹ thuật đồ án (Word/PDF/Markdown) cùng Slide thuyết trình báo cáo môn học.

## 2. File liên quan (Affected Files)
- `docs/BaoCao_DeTai10.md`
- `docs/Slide_ThuyetTrinh_DeTai10.pptx` (hoặc `.md`)

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Cấu trúc Báo cáo:
  1. Giới thiệu đề tài & Bài toán thực tế.
  2. Kiến trúc hệ thống Client – Server qua UDP.
  3. Thuật toán mã hóa Vigenère & Cơ chế File I/O (Text vs Binary).
  4. Cơ sở dữ liệu và bảo mật (JDBC PreparedStatement).
  5. Đánh giá kiểm thử (Unit Test, Load Test, E2E).
  6. Kết luận & Hướng phát triển.
- Slide thuyết trình: Tối thiểu 12-15 slide trực quan, có ảnh chụp màn hình demo Server/Client thực tế.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Báo cáo đồ án đầy đủ các chương mục kỹ thuật, định dạng chuyên nghiệp.
- [ ] Slide thuyết trình súc tích, làm nổi bật được các điểm cốt lõi của đề tài.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Tài liệu hóa hoàn chỉnh toàn bộ thành quả của 5 thành viên trong nhóm, phục vụ nghiệm thu và chấm điểm đồ án.

---

# VER-04 — Kiểm thử tải nhẹ & Nghiệm thu bản Release

- **Mã Task:** `VER-04`
- **Sprint:** 4 (M4-release)
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm thử chịu tải nhẹ khi nhiều Client kết nối gửi dữ liệu đồng thời và nghiệm thu 2 file Fat JAR trên GitHub Release.

## 2. File liên quan (Affected Files)
- `architecture/verification/M4-release.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Chạy 2-3 Client đồng thời gửi liên tục các gói tin lớn nhỏ.
- Xác nhận Server xử lý đa luồng an toàn, không crash, không xung đột I/O.
- Tải trực tiếp `udp-server.jar` và `udp-client.jar` từ GitHub Release để chạy thử nghiệm.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Hồ sơ nghiệm thu bản phát hành hoàn thiện tại `architecture/verification/M4-release.md`.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Kiểm thử tải nhẹ (Lightweight Stress Test):**
    - Gửi liên tục 100 gói tin UDP từ nhiều luồng Client đồng thời.
    - Đánh giá khả năng xử lý không nghẽn của `UdpServerWorker`.
    - Kiểm tra tính toàn vẹn và không thất thoát bản ghi trong bảng `lich_su_xu_ly`.
    - Đo lường thời gian xử lý trung bình mỗi gói tin (đạt tiêu chuẩn NFR-01: < 200ms).
  - **Nghiệm thu bản Release:**
    - Kiểm tra khởi chạy độc lập 2 file Fat JAR `udp-server.jar` và `udp-client.jar`.
    - Hoàn tất biên bản nghiệm thu tổng thể Milestone 4 (Release Ready).

---
