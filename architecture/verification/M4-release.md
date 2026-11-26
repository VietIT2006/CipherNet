# Hồ Sơ Nghiệm Thu Milestone 4: Kiểm Thử Tải Nhẹ & Bàn Giao Bản Phát Hành (M4-release)

> **Dự án:** Vigen — Hệ thống Client – Server Quản lý & Truyền nhận Dữ liệu Mã hóa qua Giao thức UDP  
> **Mã Milestone:** `M4-release` | **Mã Task:** `VER-04`, `DEP-05`, `DEP-06`, `DEP-07`  
> **Trạng thái:** **ĐẠT 100% TIÊU CHÍ BÀN GIAO (RELEASE READY)**

---

## 1. Mục tiêu kiểm chứng & Nghiệm thu
Xác nhận kết quả kiểm thử chịu tải nhẹ (`UdpServerStressTest`), kiểm tra tính toàn vẹn dữ liệu CSDL (`lich_su_xu_ly` với cột `io_mode`), kiểm thử tự động E2E Client–Server (`UdpClientServiceTest` & `UdpServerWorkerTest`), và đóng gói 2 Fat JAR độc lập (`udp-server.jar`, `udp-client.jar`) biên dịch chuẩn bytecode Java 17 (`--release 17`).

---

## 2. Bảng tổng hợp Definition of Done (DoD)

| STT | Hạng mục kiểm chứng | Kết quả thực tế | Trạng thái |
| :---: | :--- | :--- | :---: |
| 1 | `UdpServerStressTest` (100 gói UDP từ 10 luồng đồng thời) | 100/100 gói nhận `RESULT`, 100/100 bản ghi lưu trong `lich_su_xu_ly`, độ trễ TB < 100ms | **ĐẠT (PASS)** |
| 2 | `UdpClientServiceTest` & `UdpServerWorkerTest` (E2E & `EX-01..06`) | Kiểm chứng PING/PONG, timeout 3s, pipe `\|`, path traversal `../`, trùng tên file đồng thời | **ĐẠT (PASS)** |
| 3 | `CoreSelfCheck` trên Fat JAR (`udp-server.jar`) | Pass 5/5 module Core Logic + H2 fallback driver nạp thành công trong Fat JAR | **ĐẠT (PASS)** |
| 4 | Đóng gói 2 Fat JAR độc lập (`./gradlew shadowJar`) | Xuất ra `app/build/libs/udp-server.jar` và `app/build/libs/udp-client.jar` (~7 MB) | **ĐẠT (PASS)** |
| 5 | Tương thích môi trường CI/CD (`.github/workflows/`) | Cấu hình JDK 17 LTS cho `java-ci.yml`, `java-cd-release.yml`, `codeql.yml` | **ĐẠT (PASS)** |
