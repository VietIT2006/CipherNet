# Đặc Tả Thuật Toán Vigenère & Cơ Chế File I/O (Vigenère & I/O Specification)

## 1. Thuật toán mã hóa & giải mã Vigenère

### 1.1. Nguyên lý hoạt động
Mật mã Vigenère là phương pháp mã hóa văn bản bằng cách sử dụng xen kẽ nhiều bảng mã Caesar khác nhau, dựa trên các chữ cái của một từ khóa (Key).

- **Bảng chữ cái áp dụng:** Chữ cái tiếng Anh không dấu gồm 26 ký tự hoa (`A-Z`) và 26 ký tự thường (`a-z`).
- **Quy tắc bảo toàn:**
  - Chữ hoa được mã hóa thành chữ hoa.
  - Chữ thường được mã hóa thành chữ thường.
  - Tất cả các ký tự khác (chữ số `0-9`, khoảng trắng, dấu câu, ký tự đặc biệt, ký tự xuống dòng) **giữ nguyên không thay đổi** và **không làm tăng vị trí con trỏ của Khóa (Key pointer)**.
- **Xử lý Khóa:** Khóa là một chuỗi ký tự chữ cái (không phân biệt hoa thường khi tính bước dịch). Nếu khóa chứa ký tự không phải chữ cái, hệ thống sẽ tự động lọc chỉ giữ lại các ký tự chữ cái.

### 1.2. Công thức toán học
Gọi $P_i$ là giá trị số của ký tự bản rõ ($0 \le P_i < 26$), $K_j$ là giá trị số của ký tự khóa tương ứng ($0 \le K_j < 26$), và $C_i$ là ký tự bản mã:

- **Mã hóa (Encryption):**
  $$C_i = (P_i + K_j) \pmod{26}$$
- **Giải mã (Decryption):**
  $$P_i = (C_i - K_j + 26) \pmod{26}$$

---

## 2. Cơ chế Đọc / Ghi File 2 chế độ (Java I/O)

Server hỗ trợ lưu trữ nội dung đã mã hóa xuống file trên ổ đĩa và đọc lại để kiểm chứng tính toàn vẹn theo 2 chế độ:

### 2.1. Chế độ Văn bản (Text Mode - `TEXT`)
- **Mục tiêu:** Lưu trữ file văn bản thuần túy có thể mở và đọc được bằng các trình soạn thảo thông thường (Notepad, VS Code).
- **Lớp hiện thực:**
  - **Ghi file:** `java.io.BufferedWriter` kết hợp với `java.io.OutputStreamWriter` (hoặc `java.nio.file.Files.newBufferedWriter`) với bảng mã chuẩn `StandardCharsets.UTF_8`.
  - **Đọc file:** `java.io.BufferedReader` kết hợp với `java.io.InputStreamReader` (hoặc `java.nio.file.Files.newBufferedReader`) với bảng mã `StandardCharsets.UTF_8`.

### 2.2. Chế độ Nhị phân (Binary Mode - `BINARY`)
- **Mục tiêu:** Lưu trữ file dưới dạng nhị phân có cấu trúc, sử dụng luồng đệm tốc độ cao và luồng dữ liệu định kiểu (Data Stream).
- **Lớp hiện thực:**
  - **Ghi file:**
    ```java
    try (DataOutputStream dos = new DataOutputStream(
            new BufferedOutputStream(new FileOutputStream(targetFile)))) {
        dos.writeUTF(encryptedContent);
        dos.flush();
    }
    ```
  - **Đọc file:**
    ```java
    try (DataInputStream dis = new DataInputStream(
            new BufferedInputStream(new FileInputStream(targetFile)))) {
        String content = dis.readUTF();
    }
    ```
- **Lợi ích:** Đảm bảo toàn vẹn dữ liệu chuỗi khi lưu trữ ở mức nhị phân, tự động ghi tiền tố độ dài chuỗi 2-byte theo chuẩn UTF-8 sửa đổi (modified UTF-8) của Java.
