# QUY TẮC 8: BẤT BIẾN THƯ MỤC ARCHITECTURE (STRICT READ-ONLY)

## Phạm vi áp dụng
Toàn bộ thư mục `architecture/` và tất cả các thư mục con:
- `architecture/specs/` (Tài liệu đặc tả hệ thống SRS, giao thức UDP, thuật toán Vigenère & I/O)
- `architecture/tasks/` (Đặc tả các Task backlog `CORE-*`, `DEP-*`, `SRV-*`, `UI-*`, `VER-*`)
- `architecture/database/` (Lược đồ CSDL `schema.sql`, dữ liệu mẫu `sample_data.sql`)
- `architecture/docs/` (Quy chuẩn thiết kế UI/UX, Coding Standards, Testing Guide)
- `architecture/verification/` (Hồ sơ nghiệm thu các Milestone)

## Yêu cầu bắt buộc đối với AI Agent
1. **Tuyệt đối không sửa đổi (Frozen & Immutable):**
   - Nghiêm cấm mọi hành vi chỉnh sửa, ghi đè, thêm mới, xóa hoặc đổi tên bất kỳ file/thư mục nào trong `architecture/`.
   - Các file trong `architecture/` chỉ được mở ở chế độ **Read-Only** để làm nguồn tham chiếu kỹ thuật duy nhất (Single Source of Truth).
2. **Khu vực được phép thao tác code:**
   - Mọi mã nguồn phát triển và kiểm thử chỉ được đặt tại:
     - `app/src/main/java/`
     - `app/src/main/resources/`
     - `app/src/test/java/`
     - `scripts/`
     - Các file cấu hình root được cho phép (`build.gradle`, `settings.gradle`, `gradle.properties`).
3. **Xử lý khi phát hiện sai khác hoặc thiếu sót:**
   - Nếu phát hiện mâu thuẫn giữa yêu cầu thực tế và tài liệu trong `architecture/`, Agent phải báo cáo rõ ràng với User chứ không được tự ý sửa file trong `architecture/`.
