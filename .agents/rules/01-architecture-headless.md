# QUY TẮC 1: TÁCH BIỆT CORE LOGIC KHỎI UI (HEADLESS FIRST)

## Phạm vi áp dụng
Tất cả các lớp Java trong package `com.detai10.core.*` (`protocol`, `cipher`, `io`, `search`, `db`).

## Yêu cầu bắt buộc
1. **Tuyệt đối không import thư viện giao diện:** Cấm sử dụng `java.awt.*`, `javax.swing.*`, `javafx.*` bên trong `com.detai10.core.*`.
2. **Khả năng kiểm thử Headless:** Mọi hàm xử lý nghiệp vụ phải chạy độc lập trên môi trường console không có màn hình đồ họa (Linux Headless CI / GitHub Actions).
3. **Xử lý đa luồng an toàn (Non-blocking):**
   - Vòng lặp `DatagramSocket.receive()` và các tác vụ I/O/DB trên Server phải chạy trên Worker Thread riêng (`UdpServerWorker`).
   - Mọi thao tác cập nhật dữ liệu lên bảng/giao diện Swing bắt buộc bọc trong:
     ```java
     SwingUtilities.invokeLater(() -> {
         // Cập nhật UI
     });
     ```
