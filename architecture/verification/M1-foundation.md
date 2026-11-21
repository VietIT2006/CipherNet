# Hồ Sơ Kiểm Chứng Sprint 1: Setup, Đặc Tả, CSDL & Hạ Tầng CI (M1-foundation)

## 1. Thông tin tổng quan
- **Mã Milestone:** `M1-foundation`
- **Sprint:** 1
- **Tổng Story Points:** 9 SP
- **Trạng thái:** Đã nghiệm thu (Accepted)

## 2. Danh mục Task & Tiêu chí hoàn thành (DoD)

| Mã Task | Tên Task | Trạng thái | Tiêu chí kiểm chứng |
| :--- | :--- | :---: | :--- |
| `DEP-01` | Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh | ✅ Đạt | Đủ cây thư mục `architecture/`, `app/`, bộ file `AGENTS.md`, `CONTEXT.md`, `START.md`, `.gitignore`, `build.gradle`. |
| `DEP-02` | Viết tài liệu đặc tả & sơ đồ Mermaid | ✅ Đạt | Có đầy đủ `SRS.md`, `udp-protocol.md`, `vigenere-and-io.md` và 4 file sơ đồ `.mmd`. |
| `DEP-03` | Thiết kế & khởi tạo CSDL | ✅ Đạt | File `schema.sql` và `sample_data.sql` thực thi thành công, tạo bảng `lich_su_xu_ly` và `sinh_vien`. |
| `DEP-04` | Thiết lập GitHub Actions CI & Branch Protection | ✅ Đạt | File `.github/workflows/java-ci.yml` cấu hình đúng trigger cho `main` và `develop`, build JDK 17. |
| `VER-01` | Kiểm chứng Sprint 1 | ✅ Đạt | Hồ sơ nghiệm thu M1 được lập đầy đủ. |

## 3. Bằng chứng kiểm thử (Evidence)
- Cấu trúc thư mục được tổ chức theo chuẩn Spec-driven & AI-ready.
- Syntax schema MySQL được kiểm tra hợp lệ, hỗ trợ UTF-8 (utf8mb4).
