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
