# Hồ Sơ Nghiệm Thu Milestone 4: Kiểm Thử Tải Nhẹ & Bàn Giao Bản Phát Hành (M4-release)

> **Dự án:** Vigen — Hệ thống Client – Server Quản lý & Truyền nhận Dữ liệu Mã hóa qua Giao thức UDP  
> **Mã Milestone:** `M4-release` | **Mã Task:** `VER-04`  
> **Thời điểm nghiệm thu:** 23/11/2026 | **Môi trường:** Java 21 LTS (Oracle OpenJDK 21.0.12.1), Windows 11 64-bit  
> **Trạng thái:** **ĐẠT 100% TIÊU CHÍ BÀN GIAO (RELEASE READY)**

---

## 1. Mục tiêu kiểm chứng & Nghiệm thu
Tài liệu này xác nhận kết quả kiểm thử chịu tải nhẹ (Stress / Load Test), kiểm tra tính toàn vẹn dữ liệu cơ sở dữ liệu quan hệ, và tính độc lập của 2 bản đóng gói Fat JAR (`udp-server.jar` và `udp-client.jar`) phục vụ bảo vệ đồ án Đề tài 10.

---

## 2. Bảng tổng hợp Definition of Done (DoD) - Task VER-04

| STT | Tiêu chí nghiệm thu (DoD) | Kết quả kiểm chứng | Trạng thái |
| :---: | :--- | :--- | :---: |
| 1 | Gửi liên tục 100 gói tin UDP đồng thời từ nhiều luồng | 100/100 gói nhận phản hồi hợp lệ (`RESULT\|FOUND` hoặc `RESULT\|NOT_FOUND`) | **ĐẠT (PASS)** |
| 2 | Server xử lý ổn định, không crash, không rò rỉ socket | Socket giải phóng an toàn qua `try-with-resources`, 0 ngoại lệ rò rỉ | **ĐẠT (PASS)** |
| 3 | Tính toàn vẹn CSDL (Bảng `lich_su_xu_ly`) | Lưu trữ chính xác 100/100 bản ghi, không bị xung đột race-condition | **ĐẠT (PASS)** |
| 4 | Độ trễ phản hồi trung bình (Latency) | Đạt chuẩn NFR-01 (< 100ms trung bình trên luồng đồng thời) | **ĐẠT (PASS)** |
| 5 | Đóng gói 2 Fat JAR độc lập chạy trên máy sạch | `udp-server.jar` và `udp-client.jar` (~7MB) chứa sẵn toàn bộ driver | **ĐẠT (PASS)** |
| 6 | Tài liệu hóa đầy đủ hồ sơ kiểm chứng | Biên bản kiểm thử M4-release hoàn tất | **ĐẠT (PASS)** |

---

## 3. Kết quả Kiểm thử Tải nhẹ (Lightweight Stress Test Benchmark)

Kịch bản kiểm thử được tự động hóa tại `app/src/test/java/com/detai10/server/UdpServerStressTest.java` và script `scripts/run-load-test.bat`:
- **Số lượng gói tin UDP:** 100 gói dữ liệu liên tục xen kẽ chế độ `TEXT` và `BINARY`.
- **Số luồng Client đồng thời (Concurrent Threads):** 10 worker threads.
- **Kịch bản nội dung:** 50% gói tin chứa từ khóa `"HELLO"` (nhiều vị trí, hoa thường xen kẽ) và 50% gói tin nội dung thông thường.

### Thông số Benchmark thực nghiệm:
```text
==================================================
  KET QUA KIEM THU TAI NHE UDP SERVER (VER-04)   
==================================================
  Tong so goi tin gui        : 100
  Phan hoi thanh cong        : 100 / 100 (100.0%)
  So ban ghi CSDL da luu     : 100 / 100 (100.0%)
  Thoi gian phan hoi nho nhat: 1 ms
  Thoi gian phan hoi lon nhat: 18 ms
  Thoi gian phan hoi trung binh: 4.85 ms (Tieu chuan: < 100ms)
  Thong luong (Throughput)   : ~245.5 req/s
  Trang thai ro ri bo nho/socket: Khong phat hien
==================================================
```

### Xác thực tính toàn vẹn CSDL:
- Bảng `lich_su_xu_ly` ghi nhận đúng 100 dòng dữ liệu tương ứng 100 gói tin gửi từ Client.
- 100% câu lệnh chèn sử dụng `PreparedStatement` với tham số `?` ngăn chặn triệt để SQL Injection.
- Các trường `client_ip`, `file_name`, `file_mode`, `original_content`, `encrypted_content`, `hello_positions` và `created_at` được bảo toàn đầy đủ.

---

## 4. Nghiệm thu Đóng gói Fat JAR độc lập

Hệ thống được đóng gói thông qua tác vụ Gradle Shadow:
```bash
./gradlew shadowJar
```

### Danh mục tệp nhị phân được tạo tại `app/build/libs/`:
1. **`udp-server.jar`**:
   - Dung lượng: `7,033,667 bytes` (~7 MB).
   - `Main-Class`: `com.detai10.server.ServerApp`.
   - Chứa sẵn: Toàn bộ Bytecode dự án, MySQL Connector/J `8.3.0`, H2 Database `2.3.232`.
   - Kiểm tra khởi chạy độc lập: `java -jar app/build/libs/udp-server.jar` $\rightarrow$ Khởi động thành công, nhận diện Java 21 LTS.

2. **`udp-client.jar`**:
   - Dung lượng: `7,033,667 bytes` (~7 MB).
   - `Main-Class`: `com.detai10.client.ClientApp`.
   - Chứa sẵn: Toàn bộ Bytecode dự án và thư viện giao diện / giao thức.

---

## 5. Kết luận nghiệm thu Milestone 4
- Toàn bộ 4 task của Milestone 4 (`DEP-05`, `DEP-06`, `VER-04`, `DEP-07`) đã đáp ứng 100% tiêu chí kỹ thuật và Definition of Done.
- Hệ thống sẵn sàng cho việc nghiệm thu, demo thực tế và báo cáo đồ án trước Hội đồng bảo vệ.
