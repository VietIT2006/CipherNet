# Sổ Tay Tác Nghiệp Dành Cho AI Agent (Agent Operational Workflow)

Tài liệu này hướng dẫn chi tiết quy trình từng bước cho Agent khi giải quyết một Task trong dự án **Vigen (Đề tài 10)**.

---

## 1. Chu trình phát triển Task (Task Lifecycle)

```
[Nhận Task ID]
       │
       ▼
[1. Đọc Spec & Task File] ──► Kiểm tra dependencies & Definition of Done (DoD)
       │
       ▼
[2. Tạo Git Branch]       ──► feat/<TASK_ID>-<short-description>
       │
       ▼
[3. Viết Unit Test (TDD)] ──► JUnit 5 tại app/src/test/java/com/detai10/core/
       │
       ▼
[4. Hiện thực Mã nguồn]   ──► app/src/main/java/com/detai10/
       │
       ▼
[5. Xác thực Headless]    ──► ./gradlew test --no-daemon (100% Pass)
       │
       ▼
[6. Commit & Hoàn tất]    ──► [<TASK_ID>] <Action in imperative mood>
```

---

## 2. Chi tiết từng bước thực thi

### Bước 1: Khảo sát Spec & Phân tích Task
- Mở file `architecture/tasks/<TASK_ID>.md` tương ứng.
- Đọc kỹ 4 mục:
  1. *Mục tiêu (Goal / User Story)*
  2. *File liên quan (Affected Files)*
  3. *Yêu cầu kỹ thuật chi tiết (Technical Requirements)*
  4. *Tiêu chí hoàn thành (Definition of Done - DoD)*
- Đọc chéo các file đặc tả trong `architecture/specs/` (`udp-protocol.md`, `vigenere-and-io.md`, `SRS.md`) để nắm rõ contract.

### Bước 2: Quản lý Nhánh Git
- Tạo và chuyển sang nhánh làm việc từ nhánh `develop` (hoặc `main`):
  ```bash
  git checkout -b feat/<TASK_ID>-<short-description>
  # Ví dụ: git checkout -b feat/CORE-02-vigenere-cipher
  ```

### Bước 3: Áp dụng TDD (Test-Driven Development)
- Tạo lớp test tương ứng trong `app/src/test/java/com/detai10/core/`.
- Bao phủ các trường hợp:
  - Trường hợp chuẩn (Happy path)
  - Trường hợp biên (Edge cases): Chuỗi rỗng, chuỗi chỉ chứa số/ký tự đặc biệt, gói tin bị khuyết phần tử.
  - Trường hợp I/O file: Dùng `@TempDir Path tempDir` của JUnit 5 để tạo file tạm. **Không ghi file trực tiếp vào thư mục mã nguồn**.

### Bước 4: Hiện thực Mã nguồn
- Tuân thủ nghiêm ngặt **6 Quy tắc bất khả xâm phạm** trong [AGENTS.md](file:///d:/đề_tài_số_10/AGENTS.md).
- Code phải có JavaDoc và chú thích ngắn gọn, súc tích bằng tiếng Việt hoặc tiếng Anh chuẩn.
- Đảm bảo `try-with-resources` với toàn bộ luồng vào ra và socket.

### Bước 5: Chạy Kiểm thử & Nghiệm thu cục bộ
- Chạy toàn bộ test suite bằng lệnh:
  ```bash
  ./gradlew test --no-daemon
  ```
  *(Trên Windows có thể dùng: `.\scripts\test-headless.bat`)*
- Nếu có bất kỳ test nào fail, sửa ngay lập tức trước khi chuyển bước tiếp theo.
- Tự đánh dấu checklist trong file `architecture/tasks/<TASK_ID>.md`:
  - `[x] Tiêu chí hoàn thành đã đạt`

### Bước 6: Commit chuẩn mực
- Cú pháp commit message:
  ```text
  [<TASK_ID>] <Mô tả hành động ở thể mệnh lệnh>
  
  Chi tiết thay đổi (nếu có):
  - Hiện thực hàm ...
  - Bổ sung unit test ...
  ```
- **Ví dụ đúng:**
  - `[CORE-01] Implement UDP packet serializer and parser with safe delimiter splitting`
  - `[CORE-02] Implement Vigenere cipher encryption and decryption algorithms`
- **Ví dụ sai:**
  - `fixed bugs`, `update code`, `wip`, `done task`
