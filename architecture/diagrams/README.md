# Danh Mục Sơ Đồ Kiến Trúc Hệ Thống (System Architecture Diagrams)

Thư mục này chứa toàn bộ các sơ đồ thiết kế hệ thống dưới định dạng chuẩn **Mermaid (`.mmd`)**, trực quan hóa toàn bộ luồng dữ liệu, giao thức mạng, cấu trúc CSDL và mô hình đa luồng của dự án **Vigen (Đề tài 10)**.

---

## Danh Sách Các Sơ Đồ Kỹ Thuật

| STT | Tên file | Loại sơ đồ | Nội dung mô tả |
| :---: | :--- | :--- | :--- |
| **01** | [01-system-architecture.mmd](file:///d:/đề_tài_số_10/architecture/diagrams/01-system-architecture.mmd) | `graph TB` | **Kiến trúc tổng thể hệ thống:** Phân tầng ứng dụng Client Swing, mạng truyền dẫn UDP, ứng dụng Server, tầng Core Logic độc lập và hệ thống lưu trữ (MySQL & File). |
| **02** | [02-udp-protocol-flow.mmd](file:///d:/đề_tài_số_10/architecture/diagrams/02-udp-protocol-flow.mmd) | `sequenceDiagram` | **Luồng tuần tự Giao thức UDP:** Hai giai đoạn trao đổi gói tin: Giai đoạn 1 bắt tay Healthcheck (`PING`/`PONG` timeout 3s) và Giai đoạn 2 truyền nhận dữ liệu (`DATA`/`RESULT`). |
| **03** | [03-server-processing-flow.mmd](file:///d:/đề_tài_số_10/architecture/diagrams/03-server-processing-flow.mmd) | `flowchart TD` | **Lưu đồ xử lý gói tin tại Server:** Chu trình 8 bước tuần tự từ lúc nhận gói tin UDP, phân loại, ghi DB, mã hóa Vigenère, ghi/đọc file kiểm chứng, giải mã, tìm kiếm `"HELLO"`, cập nhật GUI và phản hồi kết quả. |
| **04** | [04-vigenere-cipher-flow.mmd](file:///d:/đề_tài_số_10/architecture/diagrams/04-vigenere-cipher-flow.mmd) | `flowchart LR` | **Quy trình Thuật toán Vigenère:** Mô tả chi tiết việc phân loại ký tự, áp dụng modulo 26 cho `[A-Za-z]` và bảo toàn nguyên vẹn số, khoảng trắng, dấu câu. |
| **05** | [05-database-erd.mmd](file:///d:/đề_tài_số_10/architecture/diagrams/05-database-erd.mmd) | `erDiagram` | **Lược đồ Thực thể - Mối quan hệ (ERD):** Cấu trúc chi tiết các trường dữ liệu và ràng buộc của bảng `lich_su_xu_ly` và bảng bổ trợ `sinh_vien`. |
| **06** | [06-client-state-machine.mmd](file:///d:/đề_tài_số_10/architecture/diagrams/06-client-state-machine.mmd) | `stateDiagram-v2` | **Máy trạng thái Vòng đời Client:** Quá trình chuyển đổi trạng thái giữa màn hình `ConnectFrame` và `ClientMainFrame`, kiểm soát trạng thái gửi/chờ phản hồi. |
| **07** | [07-thread-concurrency.mmd](file:///d:/đề_tài_số_10/architecture/diagrams/07-thread-concurrency.mmd) | `sequenceDiagram` | **Kiến trúc Đa luồng Non-blocking:** Tương tác bất đồng bộ giữa Event Dispatch Thread (EDT) của Swing, `SwingWorker` ở Client và `UdpServerWorker` ở Server. |

---

## Hướng Dẫn Xem và Render Sơ Đồ
1. **Xem trực tiếp:** Hầu hết các trình xem Markdown hiện đại (GitHub, VS Code, Antigravity IDE) đều hỗ trợ render trực tiếp mã Mermaid.
2. **Xuất ảnh PNG / SVG:** Bạn có thể copy mã nguồn `.mmd` dán vào [Mermaid Live Editor](https://mermaid.live) để tùy biến màu sắc và tải về file ảnh chất lượng cao.
