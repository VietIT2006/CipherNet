# SRV-04 — Tích hợp lưu trữ CSDL & Gửi gói phản hồi RESULT/ERROR

- **Mã Task:** `SRV-04`
- **Sprint:** 3
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 3 (Database Engineer)

---

## 1. Mục tiêu (Goal / User Story)
Tích hợp khâu lưu trữ bản ghi giao dịch vào CSDL và gửi gói tin phản hồi UDP (`RESULT` hoặc `ERROR`) về lại địa chỉ IP và Port của Client.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/server/worker/UdpServerWorker.java`
- `app/src/main/java/com/detai10/core/db/DatabaseHelper.java`
- `app/src/main/java/com/detai10/core/db/HistoryRecord.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Khởi tạo đối tượng `HistoryRecord` chứa: `clientIp`, `fileName`, `fileMode`, `originalContent`, `encryptedContent`, `helloPositions`.
- Gọi `DatabaseHelper.saveHistory(record)` lưu vào CSDL an toàn bằng `PreparedStatement`.
- Đóng gói bản tin phản hồi:
  - Nếu tìm thấy từ khóa: `RESULT|FOUND|<danh_sách_vị_trí>`
  - Nếu không tìm thấy: `RESULT|NOT_FOUND|Khong tim thay tu khoa HELLO`
  - Nếu gặp lỗi: `ERROR|<thông_điệp_lỗi>`
- Gửi gói tin qua `DatagramSocket.send()` về đúng IP và Port của Client yêu cầu.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Dữ liệu lưu đúng và đủ các cột trong bảng `lich_su_xu_ly`.
- [ ] Client nhận được gói tin phản hồi chính xác trong thời gian < 100ms.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Nhận kết quả từ luồng xử lý core (SRV-03), chuẩn bị dữ liệu lưu trữ.
- Gọi phương thức DAO bất đồng bộ hoặc trong worker thread để không làm nghẽn socket nhận tiếp theo.
- Tạo gói `DatagramPacket` chứa mảng byte UTF-8 của bản tin phản hồi và phát qua mạng.
