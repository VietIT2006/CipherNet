package com.detai10.core.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Tầng truy xuất cơ sở dữ liệu (DAO) quản lý kết nối JDBC, khởi tạo bảng tự động
 * và lưu trữ an toàn lịch sử xử lý gói tin UDP từ Client.
 * <p>
 * Yêu cầu kỹ thuật & Kiến trúc:
 * - Hỗ trợ cả MySQL (Localhost:3306) cho môi trường triển khai thực tế và H2 Database (In-memory/Embedded)
 *   cho môi trường kiểm thử tự động (Unit Test / CI Headless).
 * - Tự động chạy DDL khởi tạo bảng {@code lich_su_xu_ly} và {@code sinh_vien} nếu chưa tồn tại.
 * - Sử dụng 100% {@link PreparedStatement} với tham số {@code ?} để chống triệt để tấn công SQL Injection.
 * - Quản lý an toàn tài nguyên mạng và DB qua cú pháp {@code try-with-resources}.
 * - Tuân thủ Quy tắc 1: Không import thư viện giao diện AWT/Swing.
 */
public class DatabaseHelper implements AutoCloseable {

    private static final String DEFAULT_H2_URL = "jdbc:h2:mem:detai10_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";
    private static final String DEFAULT_H2_USER = "sa";
    private static final String DEFAULT_H2_PASSWORD = "";

    private static final String SQL_CREATE_TABLE_HISTORY =
            "CREATE TABLE IF NOT EXISTS lich_su_xu_ly (" +
            "    id INT AUTO_INCREMENT PRIMARY KEY," +
            "    client_ip VARCHAR(50) NOT NULL," +
            "    file_name VARCHAR(255) NOT NULL," +
            "    file_mode VARCHAR(10) NOT NULL," +
            "    original_content TEXT NOT NULL," +
            "    encrypted_content TEXT NOT NULL," +
            "    hello_positions VARCHAR(255) DEFAULT NULL," +
            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");";

    private static final String SQL_CREATE_TABLE_SINHVIEN =
            "CREATE TABLE IF NOT EXISTS sinh_vien (" +
            "    ma_sv VARCHAR(20) PRIMARY KEY," +
            "    ho_ten VARCHAR(100) NOT NULL," +
            "    lop VARCHAR(20) NOT NULL," +
            "    email VARCHAR(100) DEFAULT NULL," +
            "    ngay_sinh DATE DEFAULT NULL" +
            ");";

    private static final String SQL_INSERT_HISTORY =
            "INSERT INTO lich_su_xu_ly (client_ip, file_name, file_mode, original_content, encrypted_content, hello_positions) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL_HISTORY =
            "SELECT id, client_ip, file_name, file_mode, original_content, encrypted_content, hello_positions, created_at " +
            "FROM lich_su_xu_ly ORDER BY created_at DESC, id DESC";

    private static final String SQL_SELECT_RECENT_HISTORY =
            "SELECT id, client_ip, file_name, file_mode, original_content, encrypted_content, hello_positions, created_at " +
            "FROM lich_su_xu_ly ORDER BY created_at DESC, id DESC LIMIT ?";

    private static final String SQL_CLEAR_HISTORY = "DELETE FROM lich_su_xu_ly";

    private static DatabaseHelper defaultInstance;

    private final String jdbcUrl;
    private final String username;
    private final String password;

    /**
     * Khởi tạo DatabaseHelper với thông tin kết nối chỉ định tường minh.
     * Tự động khởi tạo cấu trúc bảng (initDatabase).
     *
     * @param jdbcUrl  Chuỗi kết nối JDBC
     * @param username Tên đăng nhập CSDL
     * @param password Mật khẩu đăng nhập CSDL
     */
    public DatabaseHelper(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        initDatabase();
    }

    /**
     * Phương thức khởi tạo tĩnh cho môi trường kiểm thử / In-Memory (H2 Database).
     */
    public static DatabaseHelper createInMemoryHelper() {
        return new DatabaseHelper(DEFAULT_H2_URL, DEFAULT_H2_USER, DEFAULT_H2_PASSWORD);
    }

    /**
     * Phương thức khởi tạo tĩnh cho MySQL cục bộ.
     */
    public static DatabaseHelper createMysqlHelper(String host, int port, String dbName, String user, String password) {
        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, dbName);
        return new DatabaseHelper(url, user, password);
    }

    /**
     * Lấy thể hiện Singleton mặc định của DatabaseHelper.
     * Tự động đọc cấu hình từ file database.properties nếu có;
     * nếu kết nối MySQL không khả dụng hoặc file không tồn tại, tự động chuyển sang H2 in-memory an toàn.
     */
    public static synchronized DatabaseHelper getInstance() {
        if (defaultInstance == null) {
            defaultInstance = createDefaultHelper();
        }
        return defaultInstance;
    }

    /**
     * Đặt lại thể hiện Singleton (phục vụ test).
     */
    public static synchronized void setInstance(DatabaseHelper instance) {
        defaultInstance = instance;
    }

    private static DatabaseHelper createDefaultHelper() {
        Properties props = new Properties();
        try (InputStream is = DatabaseHelper.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (is != null) {
                props.load(is);
                String host = props.getProperty("db.host", "localhost");
                int port = Integer.parseInt(props.getProperty("db.port", "3306"));
                String dbName = props.getProperty("db.name", "detai10_db");
                String user = props.getProperty("db.user", "root");
                String pass = props.getProperty("db.password", "");
                String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                        host, port, dbName);

                try {
                    DatabaseHelper helper = new DatabaseHelper(url, user, pass);
                    if (helper.isConnectionAlive()) {
                        System.out.println("[INFO] Kết nối MySQL cục bộ thành công: " + url);
                        return helper;
                    }
                } catch (Exception e) {
                    System.out.println("[WARN] Không kết nối được MySQL local, tự động chuyển sang H2 In-Memory DB: " + e.getMessage());
                }
            }
        } catch (Exception ignored) {
        }

        System.out.println("[INFO] Sử dụng H2 In-Memory Database cho phiên làm việc.");
        return createInMemoryHelper();
    }

    /**
     * Lấy một kết nối JDBC mới từ cấu hình.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * Kiểm tra trạng thái kết nối tới CSDL.
     */
    public boolean isConnectionAlive() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Tự động chạy DDL khởi tạo bảng lich_su_xu_ly và sinh_vien nếu chưa tồn tại.
     */
    public final void initDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(SQL_CREATE_TABLE_HISTORY);
            stmt.execute(SQL_CREATE_TABLE_SINHVIEN);
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi khi khởi tạo CSDL: " + e.getMessage());
        }
    }

    /**
     * Lưu một bản ghi lịch sử xử lý vào CSDL bằng PreparedStatement parameterized query (chống SQL Injection 100%).
     * Tự động gán khóa chính tự tăng (id) vào đối tượng {@link HistoryRecord}.
     *
     * @param record Đối tượng chứa thông tin giao dịch cần lưu
     * @return {@code true} nếu lưu thành công, {@code false} nếu xảy ra lỗi
     */
    public boolean saveHistory(HistoryRecord record) {
        if (record == null) {
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_HISTORY, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, record.getClientIp() != null ? record.getClientIp() : "UNKNOWN");
            ps.setString(2, record.getFileName() != null ? record.getFileName() : "unnamed.dat");
            ps.setString(3, record.getFileMode() != null ? record.getFileMode() : "TEXT");
            ps.setString(4, record.getOriginalContent() != null ? record.getOriginalContent() : "");
            ps.setString(5, record.getEncryptedContent() != null ? record.getEncryptedContent() : "");
            ps.setString(6, record.getHelloPositions() != null ? record.getHelloPositions() : "NOT_FOUND");

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi khi lưu bản ghi lịch sử: " + e.getMessage());
        }
        return false;
    }

    /**
     * Lấy danh sách toàn bộ lịch sử xử lý gói tin, sắp xếp giảm dần theo thời gian (ORDER BY created_at DESC, id DESC).
     *
     * @return Danh sách các bản ghi {@link HistoryRecord}
     */
    public List<HistoryRecord> getAllHistory() {
        List<HistoryRecord> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL_HISTORY);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi khi truy vấn toàn bộ lịch sử: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy danh sách lịch sử xử lý gần nhất với số lượng giới hạn, phục vụ JTable hiển thị trên Server.
     *
     * @param limit Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các bản ghi gần nhất
     */
    public List<HistoryRecord> getRecentHistory(int limit) {
        List<HistoryRecord> list = new ArrayList<>();
        int effectiveLimit = Math.max(limit, 1);
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_RECENT_HISTORY)) {

            ps.setInt(1, effectiveLimit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToRecord(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi khi truy vấn lịch sử gần nhất: " + e.getMessage());
        }
        return list;
    }

    /**
     * Xóa sạch toàn bộ dữ liệu trong bảng lich_su_xu_ly (phục vụ reset trạng thái kiểm thử).
     */
    public void clearHistory() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(SQL_CLEAR_HISTORY);
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi khi xóa bảng lịch sử: " + e.getMessage());
        }
    }

    private HistoryRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String clientIp = rs.getString("client_ip");
        String fileName = rs.getString("file_name");
        String fileMode = rs.getString("file_mode");
        String originalContent = rs.getString("original_content");
        String encryptedContent = rs.getString("encrypted_content");
        String helloPositions = rs.getString("hello_positions");
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new HistoryRecord(id, clientIp, fileName, fileMode, originalContent, encryptedContent, helloPositions, createdAt);
    }

    @Override
    public void close() {
        // Trong kiến trúc Connection per request, các connection đóng tự động qua try-with-resources.
    }
}
