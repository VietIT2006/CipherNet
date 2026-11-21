import os

tasks = [
    {
        'id': 'DEP-01',
        'title': 'Khởi tạo Repo, cấu trúc thư mục chuẩn & bộ file ngữ cảnh',
        'sprint': 1, 'sp': 2, 'priority': 'Cao',
        'files': 'architecture/, app/, AGENTS.md, CONTEXT.md, START.md, .gitignore, build.gradle',
        'desc': 'Khởi tạo cấu trúc dự án chuẩn Clucky theo mô hình Spec-driven & AI-ready, thiết lập các file ngữ cảnh và khung dự án Java 17.',
        'tech': 'Tạo cây thư mục architecture/ (specs, tasks, diagrams, database, verification) và app/src/main/java, app/src/test/java. Cấu hình Gradle build với JDK 17, JUnit 5, JDBC driver.',
        'dod': 'Cây thư mục hoàn chỉnh, các file tài liệu nền tảng sẵn sàng, Gradle build thành công.'
    },
    {
        'id': 'DEP-02',
        'title': 'Viết tài liệu đặc tả & sơ đồ Mermaid',
        'sprint': 1, 'sp': 2, 'priority': 'Cao',
        'files': 'architecture/specs/SRS.md, udp-protocol.md, vigenere-and-io.md, architecture/diagrams/*.mmd',
        'desc': 'Quy chuẩn hóa tài liệu kỹ thuật về giao thức UDP (PING/PONG, DATA), thuật toán Vigenère, luồng File I/O và các sơ đồ kiến trúc.',
        'tech': 'Viết định dạng Markdown và Mermaid .mmd trực quan hóa kiến trúc hệ thống, luồng tuần tự gói tin, quy trình xử lý tại Server và mã hóa Vigenère.',
        'dod': 'Có đủ 3 file đặc tả kỹ thuật và 4 sơ đồ Mermaid .mmd.'
    },
    {
        'id': 'DEP-03',
        'title': 'Thiết kế & khởi tạo CSDL',
        'sprint': 1, 'sp': 2, 'priority': 'Cao',
        'files': 'architecture/database/schema.sql, sample_data.sql, README.md',
        'desc': 'Thiết kế lược đồ cơ sở dữ liệu lưu lịch sử xử lý gói tin và dữ liệu sinh viên kiểm thử.',
        'tech': 'Tạo bảng lich_su_xu_ly lưu IP Client, tên file, chế độ, nội dung gốc, nội dung mã hóa, vị trí HELLO, thời gian; bảng bổ trợ sinh_vien. Hỗ trợ UTF-8 (utf8mb4).',
        'dod': 'Chạy thành công schema.sql và sample_data.sql trên MySQL/SQL Server không lỗi.'
    },
    {
        'id': 'DEP-04',
        'title': 'Thiết lập GitHub Actions CI & Branch Protection',
        'sprint': 1, 'sp': 2, 'priority': 'Cao',
        'files': '.github/workflows/java-ci.yml',
        'desc': 'Thiết lập quy trình tích hợp liên tục tự động kiểm tra cú pháp, biên dịch và chạy JUnit 5 khi có thay đổi code.',
        'tech': 'Viết workflow chạy trên ubuntu-latest, cài đặt JDK 17, thực thi ./gradlew test, upload test report artifact.',
        'dod': 'Workflow CI được kích hoạt và chạy xanh thành công khi push/PR vào nhánh develop/main.'
    },
    {
        'id': 'CORE-01',
        'title': 'Cài đặt bộ phân tích gói tin UDP (UdpProtocol)',
        'sprint': 2, 'sp': 1, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/core/protocol/UdpProtocol.java',
        'desc': 'Xây dựng module đóng gói và bóc tách gói tin giao tiếp UDP giữa Client và Server.',
        'tech': 'Xử lý 2 loại gói: PING/PONG và DATA|<mode>|<fileName>|<content>. Bóc tách an toàn bằng split("\\\\|", 4) để nội dung có thể chứa ký tự |.',
        'dod': 'Đóng gói và phân tích chính xác 100% các loại gói tin, Unit Test bao phủ các ca biên.'
    },
    {
        'id': 'CORE-02',
        'title': 'Cài đặt thuật toán mã hóa & giải mã Vigenère (VigenereCipher)',
        'sprint': 2, 'sp': 3, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/core/cipher/VigenereCipher.java',
        'desc': 'Hiện thực thuật toán mã hóa và giải mã Vigenère đối xứng bằng Java.',
        'tech': 'Hàm encrypt(text, key) và decrypt(cipherText, key). Biến đổi chữ cái hoa/thường theo mod 26; bảo toàn nguyên vẹn số, dấu cách, ký tự đặc biệt.',
        'dod': 'Văn bản sau khi mã hóa rồi giải mã khớp hoàn toàn với văn bản gốc.'
    },
    {
        'id': 'CORE-03',
        'title': 'Cài đặt đọc/ghi file chế độ Văn bản (FileHelper – Text Mode)',
        'sprint': 2, 'sp': 2, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/core/io/FileHelper.java',
        'desc': 'Xây dựng hàm đọc và ghi file văn bản thuần túy theo chuẩn UTF-8.',
        'tech': 'Sử dụng BufferedWriter và BufferedReader. Tự động tạo thư mục cha nếu chưa tồn tại; đảm bảo đóng luồng an toàn bằng try-with-resources.',
        'dod': 'Ghi nội dung văn bản ra đĩa và đọc lại toàn vẹn nội dung không bị lỗi font.'
    },
    {
        'id': 'CORE-04',
        'title': 'Cài đặt đọc/ghi file chế độ Nhị phân (FileHelper – Binary Mode)',
        'sprint': 2, 'sp': 2, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/core/io/FileHelper.java',
        'desc': 'Xây dựng hàm đọc và ghi file định dạng nhị phân có đệm.',
        'tech': 'Sử dụng luồng định kiểu DataOutputStream(BufferedOutputStream) với writeUTF() và DataInputStream(BufferedInputStream) với readUTF().',
        'dod': 'Ghi dữ liệu nhị phân và đọc lại nguyên vẹn chuỗi dữ liệu gốc.'
    },
    {
        'id': 'CORE-05',
        'title': 'Cài đặt thuật toán tìm tất cả vị trí từ HELLO (KeywordFinder)',
        'sprint': 2, 'sp': 1, 'priority': 'Trung bình',
        'files': 'app/src/main/java/com/detai10/core/search/KeywordFinder.java',
        'desc': 'Xây dựng thuật toán quét toàn bộ văn bản để tìm tất cả các vị trí xuất hiện của từ khóa HELLO.',
        'tech': 'Hàm findKeywordIndices(text, "HELLO") trả về List<Integer> đại diện cho các index 0-based. Xử lý trường hợp không tìm thấy và phân biệt/không phân biệt hoa thường theo đặc tả.',
        'dod': 'Trả về danh sách chính xác các vị trí xuất hiện của HELLO.'
    },
    {
        'id': 'CORE-06',
        'title': 'Xây dựng tầng thao tác CSDL (DatabaseHelper)',
        'sprint': 2, 'sp': 3, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/core/db/DatabaseHelper.java',
        'desc': 'Hiện thực tầng truy xuất dữ liệu JDBC để ghi log xử lý gói tin của Server.',
        'tech': 'Kết nối JDBC MySQL/SQL Server; hàm insertLog(...) sử dụng PreparedStatement để chống SQL Injection; quản lý kết nối an toàn.',
        'dod': 'Thực thi thêm mới bản ghi vào bảng lich_su_xu_ly thành công từ Java code.'
    },
    {
        'id': 'CORE-07',
        'title': 'Viết bộ Unit Test (JUnit 5) & lớp CoreSelfCheck',
        'sprint': 2, 'sp': 3, 'priority': 'Cao',
        'files': 'app/src/test/java/com/detai10/core/**, app/src/main/java/com/detai10/core/CoreSelfCheck.java',
        'desc': 'Xây dựng bộ kiểm thử tự động toàn diện cho các module Core Logic.',
        'tech': 'JUnit 5 cho UdpProtocolTest, VigenereCipherTest, FileHelperTest (dùng @TempDir), KeywordFinderTest, DatabaseHelperTest. Lớp CoreSelfCheck hỗ trợ chạy kiểm tra nhanh không cần test runner.',
        'dod': '100% test pass trên Linux Headless CI không có Swing.'
    },
    {
        'id': 'SRV-01',
        'title': 'Thiết kế giao diện Server (ServerMainFrame)',
        'sprint': 3, 'sp': 3, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/server/ui/ServerMainFrame.java',
        'desc': 'Xây dựng giao diện đồ họa quản trị Server bằng Java Swing.',
        'tech': 'Gồm JTextField nhập Port, JTextField nhập Key Vigenère, JButton Start/Stop, JTable hoặc JTextArea hiển thị log gói tin đến và kết quả phân tích.',
        'dod': 'Giao diện hiển thị trực quan, nhập liệu thuận tiện, quản lý trạng thái Server rõ ràng.'
    },
    {
        'id': 'SRV-02',
        'title': 'Xây dựng luồng nền lắng nghe UDP (UdpServerWorker)',
        'sprint': 3, 'sp': 3, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/server/worker/UdpServerWorker.java',
        'desc': 'Xây dựng Worker Thread chuyên trách việc lắng nghe socket UDP và phân phối gói tin.',
        'tech': 'DatagramSocket.receive() chạy trên luồng phụ để không làm đơ giao diện; phản hồi gói PONG tức thì khi nhận PING; cập nhật UI qua SwingUtilities.invokeLater.',
        'dod': 'Server lắng nghe ổn định, phản hồi PONG tức thời, giao diện không bị giật lag.'
    },
    {
        'id': 'SRV-03',
        'title': 'Tích hợp luồng xử lý gói DATA trên Server',
        'sprint': 3, 'sp': 5, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/server/ServerApp.java',
        'desc': 'Kết nối toàn diện quy trình xử lý dữ liệu hoàn chỉnh khi nhận gói DATA từ Client.',
        'tech': 'Luồng xử lý: Nhận gói -> Lưu DB -> Mã hóa Vigenère -> Ghi file theo mode -> Đọc lại file -> Giải mã bằng Key trên UI -> Tìm vị trí HELLO -> Log UI -> Gửi gói RESULT phản hồi Client.',
        'dod': 'Toàn bộ chu trình hoạt động chính xác từ lúc nhận gói đến khi phản hồi cho Client.'
    },
    {
        'id': 'UI-01',
        'title': 'Thiết kế & xử lý màn hình Kết nối Client (ConnectFrame)',
        'sprint': 3, 'sp': 3, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/client/ui/ConnectFrame.java',
        'desc': 'Xây dựng màn hình nhập cấu hình kết nối ban đầu cho Client.',
        'tech': 'Nhập IP và Port Server; nút Kết nối gửi gói PING với timeout 3 giây. Nếu nhận PONG -> mở ClientMainFrame; nếu timeout -> hiện hộp thoại thông báo lỗi.',
        'dod': 'Kiểm tra kết nối chính xác, chuyển cảnh màn hình mượt mà.'
    },
    {
        'id': 'UI-02',
        'title': 'Thiết kế màn hình làm việc chính Client (ClientMainFrame)',
        'sprint': 3, 'sp': 2, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/client/ui/ClientMainFrame.java',
        'desc': 'Xây dựng giao diện gửi nhận dữ liệu chính cho Client.',
        'tech': 'JTextField tên file, JTextArea nội dung văn bản, JRadioButton chọn TEXT hoặc BINARY, JButton Gửi dữ liệu, JTextArea hiển thị kết quả từ Server.',
        'dod': 'Bố cục rõ ràng, trực quan, hỗ trợ đầy đủ các thao tác người dùng.'
    },
    {
        'id': 'UI-03',
        'title': 'Xử lý gửi gói DATA & nhận kết quả trên Client',
        'sprint': 3, 'sp': 3, 'priority': 'Cao',
        'files': 'app/src/main/java/com/detai10/client/ClientApp.java',
        'desc': 'Lập trình sự kiện gửi gói tin UDP DATA và tiếp nhận phản hồi từ Server.',
        'tech': 'Kiểm tra hợp lệ dữ liệu (không để trống tên file/nội dung); đóng gói DATA|mode|fileName|content; gửi qua UDP; nhận và hiển thị danh sách index HELLO lên giao diện.',
        'dod': 'Dữ liệu gửi đi và hiển thị kết quả phản hồi chuẩn xác.'
    },
    {
        'id': 'DEP-05',
        'title': 'Cấu hình đóng gói 2 Executable Fat JAR (udp-server.jar & udp-client.jar)',
        'sprint': 4, 'sp': 2, 'priority': 'Cao',
        'files': 'app/build.gradle, scripts/build-jars.bat',
        'desc': 'Cấu hình build Gradle (ShadowJar / Application) để xuất ra 2 file JAR độc lập kèm phụ thuộc.',
        'tech': 'Cấu hình 2 manifest riêng biệt chỉ định Main-Class cho Server và Client; bundle kèm JDBC Driver để chạy độc lập.',
        'dod': 'Tạo ra udp-server.jar và udp-client.jar có thể chạy trực tiếp bằng java -jar.'
    },
    {
        'id': 'DEP-06',
        'title': 'Xây dựng CD Workflow (java-cd-release.yml)',
        'sprint': 4, 'sp': 2, 'priority': 'Trung bình',
        'files': '.github/workflows/java-cd-release.yml',
        'desc': 'Thiết lập quy trình tự động đóng gói và xuất bản release trên GitHub khi có tag mới.',
        'tech': 'GitHub Actions kích hoạt khi push tag v*, build 2 Fat JAR và tạo GitHub Release đính kèm file JAR.',
        'dod': 'Tự động tạo Release kèm 2 file .jar khi đẩy tag phiên bản.'
    },
    {
        'id': 'DEP-07',
        'title': 'Hoàn thiện README.md, START.md & tài liệu báo cáo đồ án',
        'sprint': 4, 'sp': 2, 'priority': 'Thấp',
        'files': 'README.md, START.md, architecture/docs/',
        'desc': 'Hoàn thiện toàn bộ tài liệu dự án phục vụ báo cáo và bảo vệ đồ án.',
        'tech': 'Hướng dẫn chi tiết, sơ đồ hệ thống, bằng chứng CI/CD, kịch bản demo và câu hỏi vấn đáp thường gặp.',
        'dod': 'Bộ tài liệu hoàn chỉnh, sẵn sàng cho buổi bảo vệ.'
    }
]

os.makedirs('architecture/tasks', exist_ok=True)

for t in tasks:
    content = f"""# {t['id']} — {t['title']}

- **Mã Task:** `{t['id']}`
- **Sprint:** {t['sprint']}
- **Độ ưu tiên:** {t['priority']}
- **Story Points (SP):** {t['sp']} SP

---

## 1. Mục tiêu (Goal / User Story)
{t['desc']}

## 2. File liên quan (Affected Files)
- `{t['files']}`

## 3. Yêu cầu kỹ thuật chi tiết (Technical Requirements)
{t['tech']}

## 4. Tiêu chí hoàn thành (Definition of Done)
- [ ] {t['dod']}
- [ ] Đảm bảo code sạch, có comment giải thích rõ ràng và tuân thủ quy chuẩn dự án.
"""
    filepath = f"architecture/tasks/{t['id']}.md"
    with open(filepath, 'w', encoding='utf-8') as f:
        f.write(content)

print(f"Generated {len(tasks)} task files successfully.")
