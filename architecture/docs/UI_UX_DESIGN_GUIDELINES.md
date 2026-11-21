# Hướng Dẫn Thiết Kế & Lập Trình Giao Diện Java Swing (UI/UX Design Guidelines)

> **Dự án:** Vigen (Đề tài 10 - UDP Java System)  
> **Framework:** Java Swing (JDK 17 LTS)  
> **Mục tiêu:** Xây dựng giao diện máy bàn trực quan, mượt mà, hỗ trợ tiếng Việt UTF-8 chuẩn xác và không bị treo luồng khi truyền nhận mạng.

---

## 1. Kiến trúc phân tầng Giao diện (UI Architecture)

```
┌─────────────────────────────────────────────────────────────────┐
│                    Event Dispatch Thread (EDT)                  │
│       - Quản lý vẽ giao diện, bắt sự kiện chuột & bàn phím      │
│       - ServerMainFrame, ConnectFrame, ClientMainFrame          │
└────────────────────────────────┬────────────────────────────────┘
                                 │
           SwingUtilities.       │  Khởi tạo Worker Thread /
           invokeLater(...)      │  SwingWorker<T, V>
                                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Background Worker Threads                    │
│       - UdpServerWorker (Vòng lặp DatagramSocket.receive)       │
│       - ClientUdpSender (Gửi DATA packet & chờ RESULT)          │
│       - Thao tác I/O File & CSDL MySQL                          │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. Bảng mã màu & Phông chữ (Design Tokens)

### 2.1. Bảng màu giao diện (Color Palette)
- **Primary Color:** `#1976D2` (Xanh dương đậm — nút chính, tiêu đề)
- **Success Color:** `#388E3C` (Xanh lá — trạng thái Server Running, kết nối thành công)
- **Warning Color:** `#F57C00` (Cam — cảnh báo timeout, trạng thái kết nối lại)
- **Error Color:** `#D32F2F` (Đỏ — trạng thái Server Stopped, lỗi mạng)
- **Background:** `#F5F5F5` (Xám rất nhạt cho nền khung)
- **Highlight Color:** `#FFF59D` (Vàng nhạt — highlight các từ khóa `"HELLO"` được tìm thấy)

### 2.2. Phông chữ (Typography)
- Phông chữ mặc định: `Segoe UI` (trên Windows) hoặc `SansSerif` (đa nền tảng).
- Font log: `Consolas` hoặc `Monospaced` (cỡ chữ 12pt).

---

## 3. Chi tiết thiết kế các màn hình

### 3.1. Màn hình Kết nối Client (`ConnectFrame`)
- **Kích thước:** Cố định `420 x 260` px, không cho resize (`setResizable(false)`).
- **Vị trí:** Giữa màn hình (`setLocationRelativeTo(null)`).
- **Thành phần:**
  - Tiêu đề: *Vigen UDP Client — Kết nối Máy chủ*
  - Ô nhập `IP Máy chủ`: Mặc định `127.0.0.1`.
  - Ô nhập `Cổng (Port)`: Mặc định `9876`.
  - Nút `Kết nối`: Nút mặc định khi bấm Enter.
  - Nhãn trạng thái: Hiển thị phản hồi *Đang kết nối... (chờ tối đa 3 giây)*.

### 3.2. Màn hình làm việc chính Client (`ClientMainFrame`)
- **Kích thước:** `850 x 600` px, hỗ trợ resize linh hoạt.
- **Bố cục 3 phần:**
  - **Phần trên (Header & File config):**
    - Ô nhập tên file (ví dụ: `dulieu_sinhvien.txt`).
    - Nhóm radio button chọn chế độ lưu trữ: `○ Văn bản (TEXT)` hoặc `○ Nhị phân (BINARY)`.
  - **Phần giữa (Văn bản & Thao tác):**
    - `JTextArea` nhập văn bản nguồn (có `JScrollPane`).
    - Thanh công cụ phụ: Nút *Xóa trắng*, Nút *Nạp file mẫu*, Đếm số ký tự/từ.
    - Nút lớn: **[ Gửi dữ liệu tới Server ]** (Primary Button).
  - **Phần dưới (Kết quả & Vị trí từ khóa):**
    - Bảng thông báo kết quả trả về từ Server.
    - Hiển thị danh sách các chỉ mục (index) tìm thấy của từ khóa `"HELLO"`.
    - Tự động dùng `Highlighter` để tô sáng các từ `"HELLO"` ngay trên khung văn bản.

### 3.3. Màn hình Quản trị Server (`ServerMainFrame`)
- **Kích thước:** `950 x 650` px.
- **Thành phần:**
  - **Thanh cấu hình Server:**
    - Ô nhập `Cổng lắng nghe`: `9876`.
    - Ô nhập `Khóa giải mã Vigenère`: Mặc định `KEYSECRET`.
    - Nút `Khởi động (Start)` & Nút `Dừng (Stop)`.
    - Đèn trạng thái trực quan: Chấm tròn xanh lá (`Đang lắng nghe`) / Chấm tròn đỏ (`Đã dừng`).
  - **Khu vực hiển thị Log & Dữ liệu:**
    - Tab 1: **Lịch sử giao dịch (JTable)**: Các cột `ID`, `Thời gian`, `IP Client`, `Tên file`, `Chế độ`, `Vị trí HELLO`, `Trạng thái DB`.
    - Tab 2: **Console Log chi tiết (JTextArea)**: Ghi lại từng gói tin raw gửi/nhận qua UDP.
    - Nút chức năng phụ: *Xóa log*, *Làm mới bảng*, *Xuất file log*.

---

## 4. Danh sách kiểm tra chất lượng giao diện (UI DoD Checklist)
- [ ] Giao diện không bị đóng băng khi gửi nhận mạng UDP.
- [ ] Mọi thông báo lỗi đều dùng hộp thoại tiếng Việt rõ ràng, dễ hiểu.
- [ ] Không sử dụng `setLayout(null)`; co giãn cửa sổ không bị mất component.
- [ ] Đóng ứng dụng có hộp thoại xác nhận và dọn dẹp tài nguyên socket an toàn.
