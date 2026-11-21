# Hồ Sơ Kiểm Chứng Sprint 3: Ứng Dụng Server & Client (M3-udp-app)

## 1. Thông tin tổng quan
- **Mã Milestone:** `M3-udp-app`
- **Sprint:** 3
- **Tổng Story Points:** 21 SP
- **Trạng thái:** Kế hoạch thực thi (Planned)

## 2. Danh mục Task & Tiêu chí kiểm chứng (DoD)

| Mã Task | Hạng mục | Tiêu chí nghiệm thu |
| :--- | :--- | :--- |
| `SRV-01` | Giao diện Server (`ServerMainFrame`) | Đầy đủ trường Port, Key Vigenère, Start/Stop, Log table hiển thị chi tiết. |
| `SRV-02` | Luồng nền lắng nghe UDP (`UdpServerWorker`) | Chạy trên Thread độc lập, non-blocking GUI, phản hồi PONG tức thời. |
| `SRV-03` | Luồng xử lý gói DATA trên Server | Tích hợp trơn tru: Lưu DB $\rightarrow$ Mã hóa $\rightarrow$ Ghi file $\rightarrow$ Đọc lại $\rightarrow$ Giải mã $\rightarrow$ Tìm "HELLO" $\rightarrow$ Gửi Client. |
| `UI-01` | Màn hình kết nối Client (`ConnectFrame`) | Nhập IP/Port, gửi PING timeout 3s, nhận PONG chuyển sang ClientMainFrame. |
| `UI-02` | Màn hình chính Client (`ClientMainFrame`) | Nhập file name, text content, chọn radio TEXT/BINARY, hiển thị kết quả. |
| `UI-03` | Gửi DATA & Nhận kết quả Client | Validate dữ liệu, gửi gói UDP, nhận và hiển thị index từ khóa `"HELLO"`. |
| `VER-03` | Kiểm chứng Sprint 3 | Chạy thực tế E2E Client – Server không xảy ra lỗi, CSDL ghi nhận chính xác. |
