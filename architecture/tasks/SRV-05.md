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
