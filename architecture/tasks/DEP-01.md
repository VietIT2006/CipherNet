# DEP-01 — Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh

- **Mã Task:** `DEP-01`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Khởi tạo cấu trúc dự án chuẩn Clucky theo mô hình Spec-driven & AI-ready, thiết lập các file ngữ cảnh và khung dự án Java 17.

## 2. File liên quan (Affected Files)
- `architecture/, app/, AGENTS.md, CONTEXT.md, START.md, .gitignore, build.gradle`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Tạo cây thư mục architecture/ (specs, tasks, diagrams, database, verification) và app/src/main/java, app/src/test/java. Cấu hình Gradle build với JDK 17, JUnit 5, JDBC driver.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Cây thư mục hoàn chỉnh, các file tài liệu nền tảng sẵn sàng, Gradle build thành công.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Thiết lập cấu trúc kho lưu trữ Git theo quy chuẩn Clucky Spec-driven development.
  - Tạo cấu trúc thư mục:
    - `architecture/specs/`: Tài liệu đặc tả kỹ thuật (SRS, Protocol, Cipher & I/O).
    - `architecture/tasks/`: Bộ 24 file task atomic.
    - `architecture/database/`: Scripts DDL và dữ liệu mẫu CSDL.
    - `architecture/verification/`: Checklist nghiệm thu từng Sprint (M1 đến M4).
    - `app/src/main/java/com/detai10/`: Mã nguồn phân tách thành 3 package `client`, `server`, `core`.
    - `app/src/test/java/com/detai10/core/`: Bộ Unit Test kiểm thử độc lập cho Core.
  - Cấu hình tệp `build.gradle` với JDK 17, JUnit 5 Jupiter, H2 Database Driver, MySQL Connector.
  - Khởi tạo các file quy chuẩn: `AGENTS.md` (Agent Constitution), `CONTEXT.md`, `START.md`, `.gitignore`.
