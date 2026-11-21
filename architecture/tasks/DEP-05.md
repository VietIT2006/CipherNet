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
