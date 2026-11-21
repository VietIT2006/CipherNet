# SRV-03 — Tích hợp Core Processing Pipeline trên Server (Cipher, I/O, Search)

- **Mã Task:** `SRV-03`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 2 (Backend / Security)

---

## 1. Mục tiêu (Goal / User Story)
Tích hợp chuỗi xử lý nghiệp vụ Core trên Server: Tiếp nhận dữ liệu gói tin -> Mã hóa Vigenère -> Lưu & Đọc kiểm chứng File -> Giải mã -> Tìm kiếm từ khóa "HELLO".

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`
- `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`
- `app/src/main/java/com/detai10/core/io/FileHelper.java`
- `app/src/main/java/com/detai10/core/search/KeywordFinder.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Nhận mảng tham số bóc tách từ gói `DATA|<mode>|<fileName>|<content>`.
- Thực hiện mã hóa nội dung bằng `VigenereCipher.encrypt(content, serverKey)`.
- Ghi file đã mã hóa theo chế độ `mode` (`TEXT` hoặc `BINARY`) vào thư mục `server_storage/` bằng `FileHelper`.
- Đọc lại file vừa ghi để kiểm chứng tính toàn vẹn (Round-trip verification).
- Giải mã lại nội dung đọc từ file bằng `VigenereCipher.decrypt(readContent, serverKey)`.
- Quét tìm tất cả các vị trí xuất hiện của từ khóa `"HELLO"` trong chuỗi bằng `KeywordFinder.findPositions()`.
- Trả về đối tượng kết quả trung gian để bàn giao cho khâu lưu CSDL và phản hồi mạng (SRV-04).

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Xử lý trọn vẹn chu trình Cipher -> File I/O -> Search trong thời gian < 100ms.
- [ ] Bảo toàn toàn vẹn nội dung khi ghi xuống và đọc ngược lại từ file.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Khâu xử lý cốt lõi của Server: Đảm bảo dữ liệu nhận được từ Client luôn được mã hóa an toàn trước khi ghi đĩa, đồng thời kiểm tra lại tính đúng đắn trước khi bàn giao cho module lưu CSDL.
