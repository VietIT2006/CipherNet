# Hồ Sơ Kiểm Chứng Sprint 2: Core Logic, DAO & Unit Test (M2-core-logic)

## 1. Thông tin tổng quan
- **Mã Milestone:** `M2-core-logic`
- **Sprint:** 2
- **Tổng Story Points:** 15 SP
- **Trạng thái:** Kế hoạch thực thi (Planned)

## 2. Danh mục Task & Tiêu chí kiểm chứng (DoD)

| Mã Task | Hạng mục | Tiêu chí nghiệm thu |
| :--- | :--- | :--- |
| `CORE-01` | Bộ phân tích gói tin UDP (`UdpProtocol`) | Đóng gói và tách chuỗi an toàn `PING`, `PONG`, `DATA\|mode\|file\|content` (hỗ trợ content chứa `\|`). |
| `CORE-02` | Thuật toán Vigenère (`VigenereCipher`) | Test đúng chiều xuôi/ngược, bảo toàn chữ hoa/thường, số, khoảng trắng, dấu câu. |
| `CORE-03` | Đọc/ghi File Văn bản (`FileHelper` Text) | Sử dụng `BufferedWriter`/`BufferedReader` UTF-8, kiểm tra bằng `@TempDir`. |
| `CORE-04` | Đọc/ghi File Nhị phân (`FileHelper` Binary) | Dùng `DataOutputStream`/`DataInputStream` bọc `BufferedStream` với `writeUTF`/`readUTF`. |
| `CORE-05` | Tìm vị trí từ khóa `"HELLO"` (`KeywordFinder`) | Trả về danh sách đầy đủ các index 0-based, không bỏ sót khi từ khóa lặp lại nhiều lần. |
| `CORE-06` | Tầng thao tác CSDL (`DatabaseHelper`) | Sử dụng `PreparedStatement` lưu bản ghi lịch sử xử lý thành công. |
| `CORE-07` | Bộ Unit Test JUnit 5 & `CoreSelfCheck` | 100% test pass trên môi trường Linux Headless CI không có Swing. |
| `VER-02` | Kiểm chứng Sprint 2 | Toàn bộ Unit Test chạy xanh trên GitHub Actions. |
