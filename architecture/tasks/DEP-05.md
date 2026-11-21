# DEP-05 — Cấu hình đóng gói 2 Executable Fat JAR (udp-server.jar & udp-client.jar)

- **Mã Task:** `DEP-05`
- **Sprint:** 4
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Cấu hình build Gradle (ShadowJar / Application) để xuất ra 2 file JAR độc lập kèm phụ thuộc.

## 2. File liên quan (Affected Files)
- `app/build.gradle, scripts/build-jars.bat`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Cấu hình 2 manifest riêng biệt chỉ định Main-Class cho Server và Client; bundle kèm JDBC Driver để chạy độc lập.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Tạo ra udp-server.jar và udp-client.jar có thể chạy trực tiếp bằng java -jar.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Cấu hình 2 Gradle task tùy biến để đóng gói ứng dụng:
    1. `serverJar`:
       - Main-Class: `com.detai10.server.ServerApp`
       - Tên file xuất ra: `udp-server.jar`
       - Tích hợp toàn bộ runtime dependencies (H2 Database Driver, MySQL Connector).
    2. `clientJar`:
       - Main-Class: `com.detai10.client.ClientApp`
       - Tên file xuất ra: `udp-client.jar`
  - Đảm bảo người dùng có thể khởi chạy trực tiếp trên bất kỳ máy nào có cài JDK 17 bằng lệnh:
    `java -jar udp-server.jar` và `java -jar udp-client.jar`.
