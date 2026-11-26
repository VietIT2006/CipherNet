# Hồ Sơ Kiểm Chứng Sprint 2: Core Logic, DAO & Unit Test (M2-core-logic)

## 1. Thông tin tổng quan
- **Mã Milestone:** `M2-core-logic`
- **Sprint:** 2
- **Tổng Story Points:** 15 SP
- **Trạng thái:** Đã nghiệm thu (Accepted)

## 2. Danh mục Task & Tiêu chí hoàn thành (DoD)

| Mã Task | Hạng mục | Tiêu chí nghiệm thu | Trạng thái |
| :--- | :--- | :--- | :---: |
| `CORE-01` | Bộ phân tích gói tin UDP (`UdpProtocol`) | Đóng gói và tách chuỗi an toàn `PING`, `PONG`, `DATA\|mode\|file\|content` (`split("\\|", 4)`), chặn `../` (`NFR-02`), kiểm soát `<= 30.000` bytes UTF-8 (`EX-03`), buffer `65.507` bytes. | ✅ Đạt |
| `CORE-02` | Thuật toán Vigenère (`VigenereCipher`) | Test đúng chiều xuôi/ngược, bảo toàn chữ hoa/thường, số, khoảng trắng, dấu câu, ném `IllegalArgumentException` khi khóa rỗng (`EX-04`). | ✅ Đạt |
| `CORE-03` | Đọc/ghi File Văn bản (`FileHelper` Text) | Sử dụng `BufferedWriter`/`BufferedReader` UTF-8, kiểm tra bằng `@TempDir`. | ✅ Đạt |
| `CORE-04` | Đọc/ghi File Nhị phân (`FileHelper` Binary) | Dùng `DataOutputStream`/`DataInputStream` bọc `BufferedStream` với `writeUTF`/`readUTF`. | ✅ Đạt |
| `CORE-05` | Tìm vị trí từ khóa `"HELLO"` (`KeywordFinder`) | Trả về danh sách đầy đủ các index 0-based (không phân biệt hoa thường), không bỏ sót khi lặp lại. | ✅ Đạt |
| `CORE-06` | Tầng thao tác CSDL (`DatabaseHelper`) | Sử dụng `PreparedStatement` với cột `io_mode` lưu bản ghi lịch sử xử lý (`insertLog` & `saveHistory`). | ✅ Đạt |
| `CORE-07` | Bộ Unit Test JUnit 5 & `CoreSelfCheck` | 100% test pass trên môi trường Headless không có Swing (`UdpProtocolTest`, `VigenereCipherTest`, `FileHelperTest`, `FileModeTest`, `KeywordFinderTest`, `DatabaseHelperTest`). | ✅ Đạt |
| `VER-02` | Kiểm chứng Sprint 2 | Toàn bộ Unit Test tầng Core chạy xanh qua `./gradlew test --no-daemon`. | ✅ Đạt |
