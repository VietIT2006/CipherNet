# CORE-08 — Xây dựng lớp CoreSelfCheck kiểm tra nhanh độc lập trên console

- **Mã Task:** `CORE-08`
- **Sprint:** 2
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 1 SP
- **Người thực hiện:** Thành viên 5 (QA / Client Dev)

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng lớp `CoreSelfCheck` có phương thức `main` cho phép kiểm tra nhanh toàn bộ chức năng tầng Core trực tiếp trên dòng lệnh terminal mà không cần khởi động giao diện Swing.

## 2. File liên quan (Affected Files)
- `app/src/main/java/com/detai10/core/CoreSelfCheck.java`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Thực thi kiểm tra tự động lần lượt:
  1. `UdpProtocol`: Đóng gói và bóc tách gói tin DATA.
  2. `VigenereCipher`: Mã hóa và giải mã chuỗi mẫu.
  3. `FileHelper`: Ghi và đọc file Text UTF-8, Binary DataStream.
  4. `KeywordFinder`: Tìm vị trí từ khóa "HELLO".
  5. `DatabaseHelper`: Khởi tạo bảng và lưu thử bản ghi trên CSDL H2.
- In ra màn hình console định dạng trực quan: `[PASS] <Tên kiểm tra>` hoặc `[FAIL] <Lỗi chi tiết>`.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Chạy lệnh `java com.detai10.core.CoreSelfCheck` in ra màn hình 100% `[PASS]`.
- [ ] Không phụ thuộc vào thư viện giao diện hay môi trường đồ họa.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Lớp `CoreSelfCheck` phục vụ mục đích kiểm tra nhanh tính toàn vẹn (Sanity Check) của hệ thống trong môi trường dòng lệnh (headless).
- Trả về mã thoát (exit code) 0 khi toàn bộ kiểm tra thành công, 1 khi có lỗi.
