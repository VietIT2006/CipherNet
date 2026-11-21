# QUY TẮC 6: QUY TRÌNH PHÁT TRIỂN & QUY ƯỚC GIT COMMIT

## Phạm vi áp dụng
Mọi hành động tạo nhánh, viết mã nguồn, viết Unit Test và commit trên Git.

## Yêu cầu bắt buộc
1. **Tuân thủ quy trình 5 bước:**
   - Bước 1: Đọc spec và task file `architecture/tasks/<TASK_ID>.md`.
   - Bước 2: Tạo nhánh tính năng `feat/<TASK_ID>-<short-description>` từ nhánh chính.
   - Bước 3: Viết Unit Test trước (TDD) trong `app/src/test/java/com/detai10/core/`.
   - Bước 4: Hiện thực mã nguồn trong `app/src/main/java/com/detai10/`.
   - Bước 5: Chạy `./gradlew test --no-daemon`, đảm bảo pass 100% trước khi commit.
2. **Quy chuẩn Commit Message:**
   - Định dạng: `[<TASK_ID>] <Hành động ở thể mệnh lệnh (tiếng Việt không dấu hoặc tiếng Anh)>`
   - Kèm theo bullet points mô tả ngắn gọn thay đổi chính.
   - Ví dụ:
     ```text
     [CORE-02] Cai dat thuat toan ma hoa va giai ma Vigenere

     - Hien thuc encrypt va decrypt bao toan ky tu bat bien
     - Bo sung bo Unit Test VigenereCipherTest voi 10 ca kiem thu
     ```
3. **Cấm commit trực tiếp lên main/master khi chưa test:** Mọi mã nguồn phải được kiểm chứng xanh trên local trước khi push.
