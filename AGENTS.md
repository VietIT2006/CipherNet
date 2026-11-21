# Đề Tài 10 — Hướng Dẫn & Quy Chuẩn Phát Triển (Agent Instructions)

## 1. Phạm vi & Mục tiêu
Dự án Đề tài 10 xây dựng hệ thống Client – Server bằng Java thông qua giao thức UDP Socket, hiện thực mã hóa/giải mã Vigenère, lưu trữ File 2 chế độ (Văn bản & Nhị phân), lưu trữ lịch sử xử lý vào CSDL qua JDBC, và tìm kiếm từ khóa `"HELLO"`. Áp dụng phương pháp làm việc chuẩn Clucky: Spec-driven & AI-ready development.

## 2. Cấu trúc thư mục (Repository Layout)
- `architecture/`: Nguồn sự thật duy nhất (Single Source of Truth) về kiến trúc, tài liệu đặc tả, cơ sở dữ liệu, sơ đồ và nhiệm vụ.
  - `specs/`: Tài liệu đặc tả kỹ thuật (`SRS.md`, `udp-protocol.md`, `vigenere-and-io.md`).
  - `tasks/`: Danh sách các atomic tasks được đánh mã độc lập (`DEP-xx`, `CORE-xx`, `SRV-xx`, `UI-xx`, `VER-xx`).
  - `diagrams/`: Sơ đồ thiết kế hệ thống định dạng Mermaid (`.mmd`).
  - `database/`: Schema DDL (`schema.sql`) và dữ liệu kiểm thử mẫu (`sample_data.sql`).
  - `verification/`: Bằng chứng nghiệm thu từng Sprint (`M1` đến `M4`).
- `app/`: Toàn bộ mã nguồn ứng dụng (Core logic, Server GUI, Client GUI, Unit Test).
- `.github/workflows/`: Quy trình CI/CD tự động hóa kiểm thử và đóng gói.
- `scripts/`: Bộ kịch bản hỗ trợ khởi chạy, biên dịch và kiểm thử nhanh.

## 3. Quy tắc kiến trúc (Architecture Rules)
- **Tách biệt Core Logic khỏi UI:** Tất cả logic xử lý dữ liệu (UDP parser, Vigenère cipher, File I/O, Keyword finder, Database DAO) phải nằm hoàn toàn trong package `core` và không được phụ thuộc vào Java Swing hay AWT. Điều này đảm bảo Unit Test chạy độc lập và xanh 100% trên môi trường CI Linux Headless.
- **Không chặn luồng giao diện (Non-blocking GUI):** Việc lắng nghe socket UDP (`DatagramSocket.receive`) và xử lý I/O nặng trên Server phải thực hiện trên Worker Thread riêng biệt. Mọi cập nhật lên giao diện phải thông qua `SwingUtilities.invokeLater`.
- **Giao thức UDP phi kết nối nhưng có kiểm soát:** Client thực hiện cơ chế Healthcheck Ping/Pong với timeout 3 giây trước khi cho phép vào giao diện chính.
- **Bảo toàn dữ liệu I/O:**
  - Chế độ Văn bản (`TEXT`): Sử dụng `BufferedWriter`/`BufferedReader` với charset `UTF-8`.
  - Chế độ Nhị phân (`BINARY`): Sử dụng luồng định kiểu bọc đệm `DataOutputStream(BufferedOutputStream)` và `DataInputStream(BufferedInputStream)` với `writeUTF()`/`readUTF()`.
- **An toàn CSDL:** Sử dụng `PreparedStatement` cho mọi thao tác SQL để chống tấn công SQL Injection.

## 4. Quy tắc lập trình & Kiểm thử (Code & Test Rules)
- **Chuẩn Java:** Sử dụng Java 17 LTS. Đặt tên package, class, method tuân thủ chuẩn Java Naming Conventions.
- **Unit Test trước (TDD mindset):** Mỗi module thuộc Core Logic phải có Unit Test tương ứng bằng JUnit 5. Sử dụng `@TempDir` để test các hàm đọc/ghi file, không tạo file rác trong workspace khi chạy test.
- **Xử lý ngoại lệ (Exception Handling):** Không nuốt ngoại lệ (empty catch block). Ghi log rõ ràng kèm thông tin ngữ cảnh.
- **Quy ước Commit & Branch:**
  - Tên nhánh: `feat/<TASK_ID>-<short-description>` (Ví dụ: `feat/CORE-02-vigenere-cipher`).
  - Commit message: `[<TASK_ID>] <Action in imperative mood>` (Ví dụ: `[CORE-02] Implement Vigenere encryption and decryption`).

## 5. Quy trình xác minh & Bàn giao (Verification)
- Sau mỗi Sprint, thực hiện kiểm chứng theo tiêu chí Definition of Done (DoD) được mô tả trong từng file `architecture/verification/M<x>-*.md`.
- Bản phát hành cuối cùng (Release) được đóng gói thành 2 Fat JAR độc lập: `udp-server.jar` và `udp-client.jar`, kèm hướng dẫn chạy chi tiết trong `START.md` và `README.md`.
