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
| 1 | `UdpServerStressTest` (100 gói UDP từ 10 luồng đồng thời) | 100/100 gói nhận `RESULT`, 100/100 bản ghi CSDL, tổng `212 ms`, min `9 ms`, max `48 ms`, TB `17.82 ms`, `471.70 req/s` | **ĐẠT (PASS)** |
| 2 | `UdpClientServiceTest` & `UdpServerWorkerTest` (E2E & `EX-01..06`) | Kiểm chứng PING/PONG, timeout 3s, pipe `\|`, path traversal `../`, trùng tên file đồng thời | **ĐẠT (PASS)** |
| 3 | Kiểm thử E2E trên Swing GUI + MySQL `MySQL80` thật (`AC-01`..`AC-06`, `EX-01`..`EX-06`) | Timeout `ConnectFrame` `3311 ms`, `TEXT` & `BINARY` round-trip (`BR-01`), 3 Client đồng thời 15/15 gói lưu đủ vào `detai10_db.lich_su_xu_ly` | **ĐẠT (PASS)** |
| 4 | Đóng gói & khởi chạy 2 Fat JAR độc lập (`java -jar`) | `udp-server.jar` và `udp-client.jar` (~7 MB) khởi chạy trực tiếp `running=True` kèm JDBC MySQL/H2 | **ĐẠT (PASS)** |
| 5 | Tương thích môi trường CI/CD (`.github/workflows/`) | Cấu hình JDK 17 LTS cho `java-ci.yml`, `java-cd-release.yml`, `codeql.yml` | **ĐẠT (PASS)** |
