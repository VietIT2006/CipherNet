# DEP-02 — Viết tài liệu đặc tả & sơ đồ Mermaid

- **Mã Task:** `DEP-02`
- **Sprint:** 1
- **Độ ưu tiên:** Cao
- **Story Points (SP):** 2 SP

---

## 1. Mục tiêu (Goal / User Story)
Quy chuẩn hóa tài liệu kỹ thuật về giao thức UDP (PING/PONG, DATA), thuật toán Vigenère, luồng File I/O và các sơ đồ kiến trúc.

## 2. File liên quan (Affected Files)
- `architecture/specs/SRS.md, udp-protocol.md, vigenere-and-io.md, architecture/diagrams/*.mmd`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
Viết định dạng Markdown và Mermaid .mmd trực quan hóa kiến trúc hệ thống, luồng tuần tự gói tin, quy trình xử lý tại Server và mã hóa Vigenère.

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] Có đủ 3 file đặc tả kỹ thuật và 4 sơ đồ Mermaid .mmd.
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.

## 5. Mô tả chức năng chi tiết (Detailed Functional Specification)
- Soạn thảo 3 tài liệu đặc tả:
    1. `SRS.md`: Đặc tả yêu cầu chức năng (FR-C01..C05 cho Client, FR-S01..S08 cho Server) và yêu cầu phi chức năng (NFR-01..04).
    2. `udp-protocol.md`: Đặc tả chi tiết cấu trúc 4 loại gói UDP (`PING`, `PONG`, `DATA|<mode>|<fileName>|<content>`, `RESULT|...`, `ERROR|...`), cơ chế Safe Parsing và xử lý timeout.
    3. `vigenere-and-io.md`: Đặc tả thuật toán mã hóa/giải mã Vigenère bảo toàn ký tự và cơ chế I/O 2 chế độ (`TEXT` UTF-8 vs `BINARY` DataStream).
  - Thiết kế sơ đồ Mermaid:
    - Sequence Diagram: Quy trình tương tác Ping/Pong và gửi nhận gói Data.
    - Architecture Flowchart: Kiến trúc phân tầng và luồng dữ liệu Client $\leftrightarrow$ Server $\leftrightarrow$ Database/File.
