# Hồ Sơ Kiểm Chứng Sprint 4: Đóng Gói CD, Kiểm Thử & Bàn Giao (M4-release)

## 1. Thông tin tổng quan
- **Mã Milestone:** `M4-release`
- **Sprint:** 4
- **Tổng Story Points:** 11 SP
- **Trạng thái:** Kế hoạch thực thi (Planned)

## 2. Danh mục Task & Tiêu chí kiểm chứng (DoD)

| Mã Task | Hạng mục | Tiêu chí nghiệm thu |
| :--- | :--- | :--- |
| `DEP-05` | Cấu hình đóng gói 2 Fat JAR | Xuất ra `udp-server.jar` và `udp-client.jar` kèm sẵn JDBC driver, chạy trực tiếp `java -jar`. |
| `DEP-06` | CD Workflow (`java-cd-release.yml`) | Tự động build và phát hành GitHub Release khi push tag `v*`. |
| `VER-04` | Kiểm thử tải nhẹ & Nghiệm thu | 2–3 Client gửi đồng thời không làm sập Server; kiểm thử trường hợp Key sai. |
| `DEP-07` | Hoàn thiện tài liệu báo cáo | Đầy đủ `README.md`, `START.md`, hướng dẫn demo vấn đáp cho bảo vệ đồ án. |
