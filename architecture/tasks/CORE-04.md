# CORE-04 — Cài đặt đọc/ghi file chế độ Nhị phân (FileHelper – Binary Mode)

- **Mã Task:** `CORE-04`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng hàm đọc và ghi file định dạng nhị phân có đệm.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/io/FileHelper.java`
- `app/src/test/java/com/detai10/core/io/FileHelperBinaryTest.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Sử dụng luồng định kiểu DataOutputStream(BufferedOutputStream) với writeUTF() và DataInputStream(BufferedInputStream) với readUTF().

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Ghi dữ liệu nhị phân và đọc lại nguyên vẹn chuỗi dữ liệu gốc.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Ghi file nhị phân (`writeBinaryFile(File file, String content)`):**
    - Sử dụng luồng định kiểu có đệm:
      ```java
      try (DataOutputStream dos = new DataOutputStream(
              new BufferedOutputStream(new FileOutputStream(file)))) {
          dos.writeUTF(content);
          dos.flush();
      }
      ```
    - Lưu chuỗi theo định dạng modified UTF-8 có tiền tố độ dài 2-byte, tối ưu tốc độ đọc/ghi nhị phân.
  - **Đọc file nhị phân (`readBinaryFile(File file)`):**
    - Sử dụng luồng định kiểu có đệm:
      ```java
      try (DataInputStream dis = new DataInputStream(
              new BufferedInputStream(new FileInputStream(file)))) {
          return dis.readUTF();
      }
      ```
    - Bắt và xử lý `EOFException` an toàn khi gặp cuối file.
