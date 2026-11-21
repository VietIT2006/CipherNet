# DOC-01 — Thiết kế sơ đồ kiến trúc & luồng dữ liệu Mermaid

- **Mã Task:** `DOC-01`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP
- **Người thực hiện:** Thành viên 5 (QA / Client Dev)

---

## 1. Mục tiêu (Goal / User Story)
Xây dựng hệ thống sơ đồ kiến trúc hệ thống và sơ đồ tuần tự (Sequence Diagram) bằng ngôn ngữ Mermaid để trực quan hóa luồng trao đổi dữ liệu.

## 2. File liên quan (Affected Files)
- `architecture/diagrams/01-system-architecture.mmd`
- `architecture/diagrams/02-udp-protocol-flow.mmd`
- `architecture/diagrams/03-server-processing-flow.mmd`
- `architecture/diagrams/04-vigenere-cipher-flow.mmd`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
- Vẽ Sequence Diagram mô tả quá trình gửi Ping/Pong và trao đổi gói DATA/RESULT giữa Client và Server.
- Vẽ Flowchart kiến trúc phân tầng (Client GUI, Network UDP, Server Worker, Core Engine, Database).
- Kiểm tra tính tương thích cú pháp Mermaid trên GitHub Markdown.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Tối thiểu 3 sơ đồ Mermaid hiển thị chuẩn xác không lỗi cú pháp.
- [ ] Trực quan hóa đầy đủ chu trình xử lý và kiến trúc hệ thống.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Thiết kế sơ đồ kiến trúc phân tầng: Tách biệt rõ ràng Client (GUI, DatagramSocket), Mạng UDP, Server (Worker Thread, UI), Tầng Core (Cipher, IO, Search, DB).
- Thiết kế sơ đồ tuần tự: Thể hiện rõ thứ tự gói tin PING -> PONG, DATA -> RESULT, và các nhánh ngoại lệ Timeout, ERROR.
