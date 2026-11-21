# QUY TẮC 4: THAO TÁC FILE I/O 2 CHẾ ĐỘ & QUẢN LÝ TÀI NGUYÊN

## Phạm vi áp dụng
Lớp `com.detai10.core.io.FileHelper` và các bài kiểm thử file.

## Yêu cầu bắt buộc
1. **Chế độ Văn bản (Text Mode - `TEXT`):**
   - Bắt buộc dùng `BufferedWriter` / `BufferedReader` với bảng mã tường minh `StandardCharsets.UTF_8`.
   - Tuyệt đối không dùng bảng mã mặc định của hệ điều hành (Default Charset).
2. **Chế độ Nhị phân (Binary Mode - `BINARY`):**
   - Bắt buộc dùng luồng định kiểu bọc luồng đệm:
     ```java
     // Ghi:
     new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path))); // writeUTF()
     // Đọc:
     new DataInputStream(new BufferedInputStream(Files.newInputStream(path)));     // readUTF()
     ```
3. **Đóng tài nguyên an toàn:** Luôn sử dụng cú pháp `try-with-resources`.
4. **Kiểm thử không để lại rác:** Khi viết Unit Test cho FileHelper, bắt buộc sử dụng `@TempDir Path tempDir` của JUnit 5. Không tạo file rác trong cây thư mục workspace.
