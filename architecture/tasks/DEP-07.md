# DEP-07 — Hoàn thiện README.md & START.md hướng dẫn chạy dưới 3 phút

- **Mã Task:** `DEP-07`
- **Sprint:** 4
- **Độ ưu tiên:** Thấp
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 2 (Backend / Security)

---

## 1. Mục tiêu (Goal / User Story)
Viết tài liệu hướng dẫn người dùng cuối và lập trình viên: `START.md` (Quickstart Guide) và `README.md` (Tổng quan dự án).

## 2. File liên quan (Affected Files)
- `README.md`
- `START.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- `START.md`: Hướng dẫn chi tiết từng bước:
  1. Yêu cầu môi trường (JDK 17).
  2. Cách biên dịch: `./gradlew build`.
  3. Cách chạy Server: `java -jar build/libs/udp-server.jar` hoặc `./gradlew runServer`.
  4. Cách chạy Client: `java -jar build/libs/udp-client.jar` hoặc `./gradlew runClient`.
  5. Kịch bản chạy thử nghiệm thực tế (Mã hóa văn bản chứa "HELLO", kiểm tra kết quả).
- `README.md`: Giới thiệu kiến trúc hệ thống, danh mục công nghệ, bảng phân công nhiệm vụ 5 thành viên.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Người dùng mới có thể đọc START.md và khởi chạy thành công trong vòng 3 phút.
- [ ] Thông tin trong tài liệu đồng bộ 100% với mã nguồn thực tế.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Tài liệu hóa trải nghiệm của người dùng (Developer Experience & User Onboarding): Giúp giảng viên và người đánh giá nghiệm thu sản phẩm dễ dàng nhất.
