# QUY TẮC 7: NGUYÊN TẮC THIẾT KẾ & LẬP TRÌNH GIAO DIỆN SWING (UI/UX GUIDELINES)

## Phạm vi áp dụng
Tất cả các lớp thuộc tầng giao diện trong package `com.detai10.server.ui.*` và `com.detai10.client.ui.*`.

---

## 1. Nguyên tắc An toàn Đa luồng Swing (Concurrency & Threading)

### Quy tắc 1.1: Mọi thao tác cập nhật UI phải chạy trên Event Dispatch Thread (EDT)
- Bất kỳ thao tác nào thay đổi trạng thái component (`setText`, `append`, `addRow`, `setEnabled`, `setVisible`) từ luồng mạng UDP hay worker thread **bắt buộc phải bọc trong `SwingUtilities.invokeLater()`**:
  ```java
  SwingUtilities.invokeLater(() -> {
      statusLabel.setText("Đang lắng nghe trên cổng " + port);
      tableModel.addRow(new Object[]{timestamp, clientIp, fileName, ioMode, status});
  });
  ```

### Quy tắc 1.2: Tuyệt đối không chặn EDT (Non-blocking EDT)
- Không bao giờ thực thi socket UDP (`DatagramSocket.receive`, `send`), `Thread.sleep()`, đọc/ghi tệp hoặc truy vấn CSDL trực tiếp trong action listener của nút bấm.
- Phía Client: Tác vụ gửi gói `PING`/`DATA` và chờ phản hồi qua `UdpClientService` phải dùng `SwingWorker<T, Void>` để giao diện không bị đóng băng, kèm trạng thái đang xử lý.
- Phía Server: Socket listener phải chạy trên Worker Thread riêng (`UdpServerWorker`).

---

## 2. Tiêu chuẩn Trải nghiệm Người dùng (UX & Visual Design)

### 2.1. System Look and Feel & Phông chữ chuẩn
- Khởi tạo giao diện theo giao diện hệ điều hành để bảo đảm hiển thị tự nhiên:
  ```java
  try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
  } catch (Exception ignored) {}
  ```
- **Phông chữ:** Sử dụng phông chữ hỗ trợ đầy đủ tiếng Việt UTF-8:
  - Nhãn, nút bấm, menu: `new Font("Segoe UI", Font.PLAIN, 13)`
  - Tiêu đề khung: `new Font("Segoe UI", Font.BOLD, 15)`
  - Khung Log / Văn bản kỹ thuật: `new Font("Consolas", Font.PLAIN, 12)` (hoặc `13`)

### 2.2. Bố cục linh hoạt (No Absolute Layout)
- **Tuyệt đối không dùng `setLayout(null)`** vì sẽ làm sai lệch giao diện trên các màn hình có độ phân giải và tỉ lệ scaling (DPI) khác nhau.
- Bắt buộc dùng layout managers chuẩn: `BorderLayout`, `GridBagLayout`, `FlowLayout`, `BoxLayout` kết hợp với khoảng đệm `EmptyBorder(10, 10, 10, 10)`.
- Với các nút bấm chính có màu nền tùy biến trên Windows Look-and-Feel, cần cấu hình tương phản rõ ràng (ví dụ `BasicButtonUI` + `setOpaque(true)` + chữ trắng trên nền `#1976D2`) để tránh mất chữ.

### 2.3. Khung nhìn có thanh cuộn (JScrollPane)
- Tất cả các vùng hiển thị nhiều dữ liệu (`JTextArea` log, `JTable` lịch sử, `JTextArea` nội dung văn bản) bắt buộc phải bọc trong `JScrollPane`.
- Bảo đảm tự động cuộn xuống cuối (Auto-scroll) cho khung log:
  ```java
  DefaultCaret caret = (DefaultCaret) logTextArea.getCaret();
  caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
  ```

---

## 3. Quy chuẩn màn hình Server (`ServerMainFrame`)

1. **Quản lý trạng thái Server:**
   - Khi Server đang chạy (`RUNNING`): Vô hiệu hóa ô nhập Port, ô nhập Key; nút Start chuyển sang trạng thái vô hiệu hóa, nút Stop kích hoạt; hiển thị trạng thái `ONLINE`.
   - Khi Server dừng (`STOPPED`): Kích hoạt lại ô nhập cấu hình; hiển thị trạng thái `OFFLINE`.
2. **Bảng Log dữ liệu trực quan:**
   - Hiển thị đầy đủ các cột: `ID`, `Thời gian`, `IP Client`, `Tên File`, `Chế độ`, `Nội dung gốc`, `Nội dung mã hóa`, `Vị trí HELLO`.

---

## 4. Quy chuẩn màn hình Client (`ConnectFrame` & `ClientMainFrame`)

1. **Màn hình kết nối (`ConnectFrame`):**
   - Đặt giữa màn hình: `setLocationRelativeTo(null)`.
   - Hỗ trợ phím `Enter` tự động kích hoạt nút Kết nối (`getRootPane().setDefaultButton(connectBtn)`).
   - Timeout 3 giây khi gửi `PING`: Nếu không nhận được `PONG`, hiển thị thông báo lỗi rõ ràng và mở lại nút kết nối.
2. **Màn hình chính (`ClientMainFrame`):**
   - **Validation trước khi gửi:**
     - Tên tệp không được để trống, không chứa ký tự cấm hoặc đường dẫn tương đối (`..`, `/`, `\`, `:`, `*`, `?`, `"`, `<`, `>`, `|`).
     - Nội dung văn bản không được để trống và dung lượng UTF-8 không vượt quá `30.000` bytes.
   - **Chống gửi trùng lặp:** Trong khi đang gửi gói tin UDP, vô hiệu hóa nút "Gửi dữ liệu" và hiển thị trạng thái đang chờ xử lý; kích hoạt lại sau khi nhận phản hồi hoặc quá hạn 5 giây.
   - **Tô sáng từ khóa:** Mọi vị trí xuất hiện của từ khóa `"HELLO"` (không phân biệt hoa/thường) phải được tô sáng nổi bật trên khung văn bản để người dùng dễ quan sát.

---

## 5. Đóng ứng dụng an toàn (Graceful Shutdown)
- Thiết lập sự kiện đóng cửa sổ (`WindowAdapter.windowClosing`):
  ```java
  setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
  addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
          int confirm = JOptionPane.showConfirmDialog(
              ServerMainFrame.this,
              "Bạn có chắc chắn muốn đóng ứng dụng không?",
              "Xác nhận thoát",
              JOptionPane.YES_NO_OPTION
          );
          if (confirm == JOptionPane.YES_OPTION) {
              // Dừng Worker, giải phóng tài nguyên socket
              dispose();
              System.exit(0);
          }
      }
  });
  ```
