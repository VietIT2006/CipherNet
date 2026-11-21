# SRV-03 — Tích hợp luồng xử lý gói DATA trên Server

- **Mã Task:** `SRV-03`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 5 SP

---

## 1. Mục tiêu (Goal / User Story)
Kết nối toàn diện quy trình xử lý dữ liệu hoàn chỉnh khi nhận gói DATA từ Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/ServerApp.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Luồng xử lý: Nhận gói -> Lưu DB -> Mã hóa Vigenère -> Ghi file theo mode -> Đọc lại file -> Giải mã bằng Key trên UI -> Tìm vị trí HELLO -> Log UI -> Gửi gói RESULT phản hồi Client.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Toàn bộ chu trình hoạt động chính xác từ lúc nhận gói đến khi phản hồi cho Client.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Chu trình xử lý trọn gói (End-to-End Processing Pipeline):**
    1. Tiếp nhận gói tin `DATA|<mode>|<fileName>|<content>` từ Client.
    2. Bóc tách an toàn 4 phần tử bằng `UdpProtocol.parsePacket()`.
    3. Gọi `VigenereCipher.encrypt(content, serverKey)` để mã hóa nội dung.
    4. Ghi nội dung đã mã hóa xuống thư mục lưu trữ của Server theo chế độ `mode`:
       - `TEXT`: Ghi bằng `FileHelper.writeTextFile()`.
       - `BINARY`: Ghi bằng `FileHelper.writeBinaryFile()`.
    5. Đọc lại nội dung từ file vừa ghi để kiểm chứng tính toàn vẹn (Round-trip verification).
    6. Giải mã nội dung đọc từ file bằng `VigenereCipher.decrypt(readContent, serverKey)` thu lại chuỗi gốc.
    7. Quét tìm tất cả các vị trí xuất hiện của từ khóa `"HELLO"` trong chuỗi bằng `KeywordFinder.findPositions()`.
    8. Tạo đối tượng `HistoryRecord` và lưu vào CSDL qua `DatabaseHelper.saveHistory()`.
    9. Đóng gói kết quả thành `RESULT|FOUND|<positions>` (hoặc `RESULT|NOT_FOUND|...`) và gửi ngược lại cho Client qua `DatagramSocket.send()`.
    10. Đẩy dữ liệu lên bảng `JTable` và ghi console log trên `ServerMainFrame`.
