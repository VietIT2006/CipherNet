# VER-03 — Kiểm chứng Sprint 3 (Client – Server E2E)

- **Mã Task:** `VER-03`
- **Sprint:** 3 (M3-udp-app)
- **Độ ưu tiên:** Trung bình
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Kiểm chứng luồng tích hợp đầu-cuối (End-to-End) giữa Client và Server trên giao thức UDP.

## 2. File liên quan (Affected Files)
- `architecture/verification/M3-udp-app.md`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Kiểm thử các kịch bản: Server bật/tắt, Client gửi PING nhận PONG, Client gửi DATA với cả 2 chế độ TEXT và BINARY.
- Kiểm tra tính nhất quán dữ liệu được ghi vào CSDL và file trên đĩa.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Bằng chứng kiểm thử E2E thực tế được lưu vào `architecture/verification/M3-udp-app.md`.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Nghiệm thu Milestone 3 (Client – Server E2E Integration).
  - Kiểm thử kịch bản:
    1. Khởi động Client khi Server chưa bật $\rightarrow$ Báo lỗi timeout chính xác trong 3s.
    2. Bật Server $\rightarrow$ Kết nối Client thành công.
    3. Gửi văn bản có chứa từ "HELLO" ở chế độ `TEXT` $\rightarrow$ Server mã hóa, lưu file, ghi CSDL, trả về đúng danh sách vị trí.
    4. Gửi văn bản ở chế độ `BINARY` $\rightarrow$ File nhị phân lưu đúng, CSDL ghi nhận đúng.
  - Ghi nhận biên bản nghiệm thu vào `architecture/verification/M3-udp-app.md`.

---

# SPRINT 4: ĐÓNG GÓI RELEASE & BÁO CÁO

<a id="dep-05"></a>
