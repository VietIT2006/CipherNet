# CORE-02 — Cài đặt thuật toán mã hóa & giải mã Vigenère (VigenereCipher)

- **Mã Task:** `CORE-02`
- **Sprint:** 2
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 3 SP

---

## 1. Mục tiêu (Goal / User Story)
Hiện thực thuật toán mã hóa và giải mã Vigenère đối xứng bằng Java.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/cipher/VigenereCipher.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Hàm encrypt(text, key) và decrypt(cipherText, key). Biến đổi chữ cái hoa/thường theo mod 26; bảo toàn nguyên vẹn số, dấu cách, ký tự đặc biệt.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Văn bản sau khi mã hóa rồi giải mã khớp hoàn toàn với văn bản gốc.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- **Quy tắc toán học:**
    - Mã hóa: $C_i = (P_i + K_j) \pmod{26}$.
    - Giải mã: $P_i = (C_i - K_j + 26) \pmod{26}$.
  - **Bảo toàn ký tự (Character Preservation):**
    - Chữ hoa `A-Z`: Dịch vòng tròn trong khoảng `['A', 'Z']`.
    - Chữ thường `a-z`: Dịch vòng tròn trong khoảng `['a', 'z']`.
    - Ký tự số (`0-9`), ký tự có dấu, ký hiệu đặc biệt, dấu câu, khoảng trắng, ký tự xuống dòng: **Giữ nguyên 100% không biến đổi**.
    - **Quan trọng:** Con trỏ bước nhảy khóa ($j$) **không được tăng** khi gặp các ký tự không phải chữ cái tiếng Anh.
  - **Xử lý Khóa (Key):**
    - Khóa là chuỗi chữ cái không phân biệt hoa thường. Tự động chuẩn hóa khóa (lọc bỏ các ký tự không hợp lệ).
    - Ném ngoại lệ `IllegalArgumentException` nếu khóa rỗng (`null` hoặc `""`).
