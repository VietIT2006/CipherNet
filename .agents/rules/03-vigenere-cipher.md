# QUY TẮC 3: THUẬT TOÁN MÃ HÓA & GIẢI MÃ VIGENÈRE

## Phạm vi áp dụng
Lớp `com.detai10.core.cipher.VigenereCipher` và các bài kiểm thử liên quan.

## Yêu cầu bắt buộc
1. **Phạm vi dịch chuyển ký tự:**
   - Chỉ dịch chuyển chữ cái tiếng Anh `A-Z` và `a-z` theo modulo 26.
   - Chữ hoa mã hóa thành chữ hoa; chữ thường mã hóa thành chữ thường.
2. **Bảo toàn ký tự bất biến (Invariant Preservation):**
   - Ký tự số (`0-9`), khoảng trắng, dấu câu (`. , ! ?`), dấu xuống dòng (`\n`) và ký tự đặc biệt **phải được giữ nguyên 100%**.
   - Con trỏ khóa (`keyIndex`) **tuyệt đối không được tăng bước** khi gặp các ký tự không phải chữ cái.
3. **Chuẩn hóa khóa (Key Normalization):**
   - Khóa Vigenère phải được lọc chỉ giữ lại chữ cái `[A-Za-z]` và chuyển về chữ hoa.
   - Nếu khóa rỗng hoặc không có chữ cái, ném ngoại lệ `IllegalArgumentException` có thông báo rõ ràng.
4. **Tính khả nghịch:** `decrypt(encrypt(text, key), key)` phải khớp tuyệt đối với `text` ban đầu.
