# Quy Chuẩn Lập Trình Dành Cho AI Agent (Coding Standards & Best Practices)

Tài liệu này quy định các tiêu chuẩn kỹ thuật chi tiết khi viết mã nguồn Java trong dự án **Vigen (Đề tài 10)**.

---

## 1. Chuẩn Java 17 & Phong cách viết mã (Coding Style)
- **Tên Package:** Chữ thường toàn bộ, phân cấp rõ ràng (ví dụ: `com.detai10.core.cipher`).
- **Tên Lớp (Class / Interface / Enum):** Danh từ, viết theo kiểu `PascalCase` (ví dụ: `VigenereCipher`, `UdpServerWorker`, `FileMode`).
- **Tên Phương thức (Method):** Động từ hoặc cụm động từ, viết theo kiểu `camelCase` (ví dụ: `encrypt()`, `parsePacket()`, `findKeywordIndices()`).
- **Hằng số (Constant):** Viết hoa toàn bộ, phân cách bằng dấu gạch dưới (ví dụ: `DEFAULT_PORT`, `BUFFER_SIZE`, `MAX_PACKET_LENGTH`).

---

## 2. Tiêu chuẩn Giao tiếp Mạng UDP Socket

### 2.1. Quản lý Bộ đệm (Buffer Allocation)
- Luôn cấp phát buffer đủ lớn để tiếp nhận gói tin:
  ```java
  byte[] buffer = new byte[8192]; // Bộ đệm 8KB tiêu chuẩn
  DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
  socket.receive(packet);
  
  // Trích xuất dữ liệu chính xác theo độ dài thực nhận
  String message = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.UTF_8);
  ```

### 2.2. Kiểm soát Timeout
- Client luôn thiết lập timeout để tránh treo vĩnh viễn khi gửi gói thăm dò:
  ```java
  socket.setSoTimeout(3000); // 3 giây timeout
  ```

---

## 3. Tiêu chuẩn Thuật toán Mật mã Vigenère

### 3.1. Chuẩn hóa Khóa
- Loại bỏ các ký tự không phải chữ cái và chuyển về chữ hoa:
  ```java
  public static String normalizeKey(String key) {
      if (key == null || key.trim().isEmpty()) {
          throw new IllegalArgumentException("Khóa Vigenère không được để trống!");
      }
      String cleanKey = key.replaceAll("[^a-zA-Z]", "").toUpperCase();
      if (cleanKey.isEmpty()) {
          throw new IllegalArgumentException("Khóa Vigenère phải chứa ít nhất một chữ cái!");
      }
      return cleanKey;
  }
  ```

### 3.2. Bảo toàn Ký tự Invariant
- Thuật toán phải duyệt từng ký tự:
  - Nếu là `A-Z`: $C = (P - 'A' + K - 'A') \pmod{26} + 'A'$
  - Nếu là `a-z`: $C = (P - 'a' + K - 'A') \pmod{26} + 'a'$
  - Nếu là ký tự khác: Giữ nguyên $C = P$, **không tăng chỉ số ký tự của khóa (key pointer)**.

---

## 4. Tiêu chuẩn Thao tác File I/O (FileHelper)

### 4.1. Chế độ Văn bản (Text Mode)
```java
// Ghi file Text
public static void writeTextFile(Path filePath, String content) throws IOException {
    Files.createDirectories(filePath.getParent());
    try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
        writer.write(content);
        writer.flush();
    }
}

// Đọc file Text
public static String readTextFile(Path filePath) throws IOException {
    StringBuilder sb = new StringBuilder();
    try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
        String line;
        while ((line = reader.readLine()) != null) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(line);
        }
    }
    return sb.toString();
}
```

### 4.2. Chế độ Nhị phân (Binary Mode)
```java
// Ghi file Binary có đệm
public static void writeBinaryFile(Path filePath, String content) throws IOException {
    Files.createDirectories(filePath.getParent());
    try (DataOutputStream dos = new DataOutputStream(
            new BufferedOutputStream(Files.newOutputStream(filePath)))) {
        dos.writeUTF(content);
        dos.flush();
    }
}

// Đọc file Binary có đệm
public static String readBinaryFile(Path filePath) throws IOException {
    try (DataInputStream dis = new DataInputStream(
            new BufferedInputStream(Files.newInputStream(filePath)))) {
        return dis.readUTF();
    }
}
```

---

## 5. Tiêu chuẩn Thao tác CSDL JDBC (DatabaseHelper)
- Luôn sử dụng `PreparedStatement` với tham số ràng buộc `?`.
- Cấu trúc mẫu ghi bản ghi:
  ```java
  String sql = "INSERT INTO lich_su_xu_ly (client_ip, file_name, file_mode, original_content, encrypted_content, hello_positions) "
             + "VALUES (?, ?, ?, ?, ?, ?)";
  try (Connection conn = getConnection();
       PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setString(1, clientIp);
      ps.setString(2, fileName);
      ps.setString(3, mode);
      ps.setString(4, originalContent);
      ps.setString(5, encryptedContent);
      ps.setString(6, helloPositions);
      ps.executeUpdate();
  }
  ```
