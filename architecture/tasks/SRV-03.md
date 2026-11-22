# SRV-03 — Tích hợp luồng xử lý gói DATA trên Server

- **Mã Task:** `SRV-03`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 5 SP

---

## 1. Mục tiêu (Goal / User Story)
Tích hợp luồng xử lý toàn diện gói tin DATA trên Server: Nhận gói -> Lưu DB -> Mã hóa Vigenère -> Ghi file theo chế độ -> Đọc lại file -> Giải mã bằng Key trên UI -> Tìm "HELLO" -> Hiển thị lên UI Server & gửi kết quả về Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/ServerApp.java`
- `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`
- `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`
- `app/src/main/java/com/detai10/core/io/FileHelper.java`
- `app/src/main/java/com/detai10/core/search/KeywordFinder.java`
- `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Nhận mảng tham số bóc tách từ gói `DATA|<mode>|<fileName>|<content>`.
- Ghi nhận vào CSDL qua `DatabaseHelper.insertLog(...)`.
- Thực hiện mã hóa nội dung bằng `VigenereCipher.encrypt(content, serverKey)`.
- Ghi file đã mã hóa theo chế độ `mode` (`TEXT` hoặc `BINARY`) bằng `FileHelper`.
- Đọc lại file vừa ghi để kiểm chứng tính toàn vẹn (Round-trip verification).
- Giải mã lại nội dung đọc từ file bằng `VigenereCipher.decrypt(readContent, serverKey)`.
- Quét tìm tất cả các vị trí xuất hiện của từ khóa `"HELLO"` trong chuỗi bằng `KeywordFinder.findIndices()`.
- Cập nhật thông tin lên bảng log Server (`SwingUtilities.invokeLater`).
- Đóng gói phản hồi `RESULT|FOUND|<indices>` (hoặc `RESULT|NOT_FOUND|...`) gửi về Client qua UDP.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Toàn bộ chu trình từ nhận gói đến khi phản hồi cho Client hoạt động chính xác.
- [ ] Bảo toàn toàn vẹn nội dung khi ghi xuống và đọc ngược lại từ file.
