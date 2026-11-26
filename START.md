# Hướng Dẫn Bắt Đầu & Khởi Chạy Dự Án (Quickstart Guide)

Tài liệu này hướng dẫn chi tiết cách thiết lập môi trường, biên dịch, kiểm thử và khởi chạy ứng dụng Server & Client.

---

## 1. Yêu cầu tiên quyết (Prerequisites)
- **JDK 17 LTS trở lên:** Kiểm tra bằng lệnh:
  ```bash
  java -version
  # Yêu cầu: version "17" trở lên
  ```
- **Hệ điều hành:** Windows 10/11 hoặc Linux / macOS.
- **Cơ sở dữ liệu:** MySQL (phiên bản 8.0+) hoặc SQL Server. Đảm bảo dịch vụ DB đang chạy.

---

## 2. Cấu hình & Khởi tạo Cơ sở dữ liệu Cục bộ (MySQL Local trên máy)

> [!NOTE]
> Dự án sử dụng hệ quản trị CSDL **MySQL chạy trực tiếp cục bộ trên máy** (`localhost:3306`), **không deploy lên cloud**. Dịch vụ `MySQL80` trên máy được sử dụng để lưu trữ.

### Cách 1: Khởi tạo nhanh bằng script (Khuyên dùng trên Windows)
Chạy script tự động [scripts/init-db.bat](file:///d:/đề_tài_số_10/scripts/init-db.bat):
```cmd
.\scripts\init-db.bat
```
*(Script sẽ hỏi mật khẩu root của MySQL trên máy bạn, sau đó tự động tạo database `detai10_db` và nạp toàn bộ bảng và dữ liệu mẫu).*

### Cách 2: Khởi tạo thủ công qua MySQL CLI
1. Đăng nhập vào MySQL:
   ```bash
   mysql -u root -p
   ```
2. Tạo database và chạy script tạo bảng từ file [schema.sql](file:///d:/đề_tài_số_10/architecture/database/schema.sql):
   ```sql
   SOURCE architecture/database/schema.sql;
   SOURCE architecture/database/sample_data.sql;
   ```

### 3. Cấu hình file database.properties
Mở file [app/src/main/resources/database.properties](file:///d:/đề_tài_số_10/app/src/main/resources/database.properties) và điền mật khẩu MySQL local của bạn:
```properties
db.host=localhost
db.port=3306
db.name=detai10_db
db.user=root
db.password=YOUR_LOCAL_MYSQL_PASSWORD
```

---

## 3. Biên dịch và Chạy Unit Test

Tại thư mục gốc của dự án hoặc trong thư mục `app/`:

```bash
# Di chuyển vào thư mục app
cd app

# Chạy toàn bộ Unit Test (Core Logic)
./gradlew test    # Windows: .\gradlew.bat test
```

> [!TIP]
> Bạn cũng có thể chạy kiểm tra nhanh Core Logic trực tiếp không cần UI thông qua script tiện ích:
> ```bash
> .\scripts\test-headless.bat
> ```

---

## 4. Khởi chạy Ứng dụng

### 4.1. Chạy từ mã nguồn (Môi trường phát triển)
```bash
# Chạy Server
./gradlew runServer

# Mở một cửa sổ terminal mới để chạy Client
./gradlew runClient
```

### 4.2. Khởi chạy từ file Fat JAR độc lập
Sau khi hoàn tất đóng gói (hoặc tải từ bản GitHub Release):

1. **Khởi động Server:**
   ```bash
   java -jar app/build/libs/udp-server.jar
   # Hoặc: .\scripts\start-server.bat
   ```
   - Nhập cổng lắng nghe UDP (mặc định: `9876`).
   - Nhập từ khóa Vigenère (mặc định: `KEYSECRET`).
   - Nhấn **Start Server**. Giao diện sẽ hiển thị trạng thái `Server is listening on port 9876...`.

2. **Khởi động Client:**
   ```bash
   java -jar app/build/libs/udp-client.jar
   # Hoặc: .\scripts\start-client.bat
   ```
   - Tại màn hình kết nối (`ConnectFrame`): Nhập IP Server (`127.0.0.1` nếu cùng máy) và Port (`9876`). Nhấn **Kết nối**.
   - Client gửi gói `PING`, Server phản hồi `PONG` $\rightarrow$ Màn hình làm việc chính (`ClientMainFrame`) xuất hiện.
   - Nhập tên file (VD: `test.txt`), nhập nội dung văn bản (có chứa từ `"HELLO"` để kiểm thử).
   - Chọn chế độ: **TEXT** hoặc **BINARY**.
   - Nhấn **Gửi dữ liệu**. Kết quả danh sách các vị trí tìm thấy `"HELLO"` sẽ được Server phản hồi và hiển thị trực quan.

---

## 5. Đóng gói Release Fat JAR
Để xuất ra 2 file JAR độc lập chứa sẵn toàn bộ thư viện phụ thuộc (Fat JAR):
```bash
cd app
./gradlew shadowJar
```
Các file được tạo ra tại `app/build/libs/`:
- `udp-server.jar`
- `udp-client.jar`
