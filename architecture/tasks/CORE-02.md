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
