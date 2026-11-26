# Hồ Sơ Kiểm Chứng Sprint 3: Ứng Dụng Server & Client (M3-udp-app)

## 1. Thông tin tổng quan
- **Mã Milestone:** `M3-udp-app`
- **Sprint:** 3
- **Tổng Story Points:** 21 SP
- **Trạng thái:** Đã nghiệm thu (Accepted)

## 2. Danh mục Task & Tiêu chí hoàn thành (DoD)

| Mã Task | Hạng mục | Tiêu chí nghiệm thu | Trạng thái |
| :--- | :--- | :--- | :---: |
| `SRV-01` | Giao diện Server (`ServerMainFrame`) | Đầy đủ trường Port, Key Vigenère, Start/Stop, trạng thái ONLINE/OFFLINE, `JTable` lịch sử & console log, cập nhật qua `SwingUtilities.invokeLater()`. | ✅ Đạt |
| `SRV-02` | Luồng nền lắng nghe UDP (`UdpServerWorker`) | Chạy trên Thread độc lập, non-blocking GUI, phản hồi `PONG` tức thời khi nhận `PING`. | ✅ Đạt |
| `SRV-03` | Luồng xử lý gói DATA trên Server | Tích hợp trơn tru: Bóc tách -> Mã hóa -> Ghi file (đồng bộ theo tên file `EX-06`) -> Đọc lại từ đĩa (`BR-01`) -> Giải mã -> Tìm `"HELLO"` -> Lưu DB (`io_mode`) -> Gửi `RESULT\|OK\|...` / `RESULT\|NOT_FOUND\|...` về Client. | ✅ Đạt |
| `UI-01` | Kết nối Client (`ConnectFrame` + `UdpClientService`) | Nhập IP/Port, gửi `PING` qua `UdpClientService` (timeout 3s), bắt `SocketTimeoutException` khi Server tắt (`AC-01`, `EX-01`), nhận `PONG` chuyển sang `ClientMainFrame`. | ✅ Đạt |
| `UI-02` | Màn hình chính Client (`ClientMainFrame`) | Nhập tên file, văn bản UTF-8, chọn radio `TEXT`/`BINARY`, nạp file từ máy, hiển thị kết quả & highlight `"HELLO"`. | ✅ Đạt |
| `UI-03` | Gửi DATA & Nhận kết quả Client (`SwingWorker`) | Validate dữ liệu (`EX-03`, `NFR-02`), gửi gói UDP qua `UdpClientService` trên `SwingWorker` (timeout 5s), hiển thị vị trí `"HELLO"` & highlight. | ✅ Đạt |
| `VER-03` | Kiểm chứng Sprint 3 (E2E) | Xác nhận tự động bằng `UdpClientServiceTest` & `UdpServerWorkerTest` (PING/PONG, timeout 3s, DATA TEXT/BINARY, pipe `\|`, trùng tên file đồng thời, CSDL `lich_su_xu_ly`). | ✅ Đạt |
