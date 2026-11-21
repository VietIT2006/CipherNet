# AGENTS.md — Quy Chuẩn & Chỉ Dẫn Dành Riêng Cho AI Agent (Agent Constitution)

Tài liệu này là **Hiến pháp Tác nghiệp (Agent Constitution)** của dự án **Đề tài 10 (Vigen UDP System)**. Mọi AI Agent (Gemini, Antigravity, Claude Code, Cursor, GitHub Copilot) khi tham gia phát triển dự án này **bắt buộc phải đọc và tuân thủ 100% các điều khoản dưới đây**.

---

## 1. Phạm vi & Tuyên ngôn dự án (Mission Statement)
- **Tên dự án:** Vigen — Hệ thống Client – Server Quản lý & Truyền nhận Dữ liệu Mã hóa qua Giao thức UDP.
- **Phương pháp phát triển:** Spec-driven & AI-ready Development (phỏng theo chuẩn Clucky).
- **Trọng tâm kỹ thuật:**
  1. Giao thức mạng phi kết nối UDP (`DatagramSocket`, `DatagramPacket`).
  2. Thuật toán mã hóa & giải mã Vigenère (bảo toàn ký tự số, ký hiệu và khoảng trắng).
  3. Thao tác File I/O 2 chế độ: Văn bản (Text UTF-8) và Nhị phân định kiểu có đệm (Binary DataStream).
  4. Thuật toán tìm kiếm tất cả vị trí xuất hiện của từ khóa `"HELLO"`.
  5. Quản lý lịch sử xử lý và dữ liệu sinh viên qua JDBC (`PreparedStatement`).

---

## 2. Nguồn sự thật duy nhất (Single Source of Truth)
Agent không được tự suy đoán hoặc sáng tạo ngoài phạm vi đã được đặc tả:
1. **Đặc tả yêu cầu:** `architecture/specs/SRS.md`
2. **Đặc tả giao thức UDP:** `architecture/specs/udp-protocol.md`
3. **Đặc tả thuật toán & I/O:** `architecture/specs/vigenere-and-io.md`
4. **Lược đồ CSDL:** `architecture/database/schema.sql`
5. **Nhiệm vụ cụ thể từng task:** `architecture/tasks/<TASK_ID>.md`

---

## 3. Các quy tắc bất khả xâm phạm (Ironclad Architecture Rules)

### 🔴 Quy tắc 1: Tách biệt hoàn toàn Core Logic khỏi UI (Headless First)
- Toàn bộ các lớp trong package `com.detai10.core.*` (`UdpProtocol`, `VigenereCipher`, `FileHelper`, `KeywordFinder`, `DatabaseHelper`) **tuyệt đối không được import bất kỳ thư viện UI nào** (`java.awt.*`, `javax.swing.*`).
- *Mục đích:* Đảm bảo Unit Test tự động (JUnit 5) chạy độc lập và xanh 100% trên môi trường CI Linux Headless (GitHub Actions).

### 🔴 Quy tắc 2: Không chặn luồng giao diện (Non-blocking GUI)
- Vòng lặp lắng nghe socket UDP (`DatagramSocket.receive`) và các tác vụ I/O/DB nặng trên Server bắt buộc phải thực thi trên một **Worker Thread riêng biệt** (`UdpServerWorker`).
- Mọi thao tác cập nhật trạng thái, log, dữ liệu lên giao diện Swing phải được bọc trong:
  ```java
  SwingUtilities.invokeLater(() -> {
      // Cập nhật JTable, JTextArea, JLabel tại đây
  });
  ```

### 🔴 Quy tắc 3: Bóc tách gói tin an toàn (Safe Payload Parsing)
- Khi phân tích gói tin dữ liệu `DATA|<mode>|<fileName>|<content>`, phải sử dụng:
  ```java
  String[] parts = rawMessage.split("\\|", 4);
  ```
- *Lý do:* Giới hạn tối đa 4 phần tử để cho phép `<content>` chứa các ký tự phân tách `|` mà không làm hỏng cấu trúc gói tin.

### 🔴 Quy tắc 4: Bảo toàn ký tự trong Vigenère Cipher
- Thuật toán mã hóa Vigenère **chỉ dịch chuyển chữ cái tiếng Anh** (`A-Z`, `a-z`).
- Các ký tự số (`0-9`), khoảng trắng, dấu xuống dòng, dấu câu và ký tự đặc biệt **phải được giữ nguyên vẹn**, và con trỏ khóa (key index) **không được tăng bước** khi gặp các ký tự này.

### 🔴 Quy tắc 5: Bảo toàn toàn vẹn dữ liệu trong File I/O
- **Chế độ TEXT:** Sử dụng `BufferedWriter`/`BufferedReader` với bảng mã chỉ định tường minh `StandardCharsets.UTF_8`.
- **Chế độ BINARY:** Sử dụng luồng định kiểu có đệm:
  ```java
  new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file))) // writeUTF
  new DataInputStream(new BufferedInputStream(new FileInputStream(file)))    // readUTF
  ```

### 🔴 Quy tắc 6: An toàn CSDL (Zero SQL Injection)
- Mọi câu lệnh SQL truy vấn và ghi dữ liệu phải dùng `PreparedStatement` với tham số `?`. Tuyệt đối không cộng chuỗi SQL.

---

## 4. Quy trình tác nghiệp 5 bước cho Agent (Agent Workflow)

Mỗi khi nhận nhiệm vụ thực hiện một Task:
1. **Bước 1 - Đọc Spec & Task:** Đọc file `architecture/tasks/<TASK_ID>.md` và các spec liên quan trong `architecture/specs/`.
2. **Bước 2 - Tạo nhánh Git:** Tạo nhánh tính năng theo cú pháp `feat/<TASK_ID>-<mo-ta-ngan>`.
3. **Bước 3 - Viết Test trước (TDD):** Viết unit test tương ứng trong `app/src/test/java/com/detai10/core/` (sử dụng `@TempDir` cho file test).
4. **Bước 4 - Hiện thực Code:** Viết mã nguồn đáp ứng test và tuân thủ các quy tắc kiến trúc.
5. **Bước 5 - Chạy Test & Nghiệm thu:**
   - Chạy lệnh `./gradlew test` (hoặc `.\scripts\test-headless.bat`).
   - Đảm bảo 100% test pass.
   - Đối chiếu Definition of Done (DoD) trong task file.
   - Commit với thông điệp: `[<TASK_ID>] <Hành động ở thể mệnh lệnh>`.

---

## 5. Quy tắc xử lý ngoại lệ (Exception Handling)
- **Không nuốt ngoại lệ:** Tuyệt đối không để khối `catch` trống hoặc chỉ in `e.printStackTrace()`.
- Ghi log có ngữ cảnh rõ ràng: `System.err.println("[ERROR] Lỗi khi xử lý gói tin UDP từ " + clientAddress + ": " + e.getMessage());`
- Đóng tài nguyên an toàn: Sử dụng cú pháp `try-with-resources` cho toàn bộ Socket, Connection, Statement, ResultSet và Stream I/O.
