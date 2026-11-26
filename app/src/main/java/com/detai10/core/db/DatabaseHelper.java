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
 * Khớp lược đồ SRS Mục 3.2 (bảng {@code lich_su_xu_ly} với cột {@code io_mode}).
 * Sử dụng 100% {@link PreparedStatement} với tham số {@code ?} để chống SQL Injection.
 * Không import thư viện giao diện AWT/Swing (Headless First).
 */
public class DatabaseHelper implements AutoCloseable {

    private static final String DEFAULT_H2_URL = "jdbc:h2:mem:detai10_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";
    private static final String DEFAULT_H2_USER = "sa";
    private static final String DEFAULT_H2_PASSWORD = "";

    private static final String SQL_CREATE_TABLE_HISTORY =
            "CREATE TABLE IF NOT EXISTS lich_su_xu_ly (" +
            "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "    client_ip VARCHAR(45) NOT NULL," +
            "    file_name VARCHAR(255) NOT NULL," +
            "    io_mode VARCHAR(10) NOT NULL," +
            "    original_content TEXT NOT NULL," +
            "    encrypted_content TEXT NOT NULL," +
            "    hello_positions VARCHAR(500) DEFAULT NULL," +
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
            "INSERT INTO lich_su_xu_ly (client_ip, file_name, io_mode, original_content, encrypted_content, hello_positions) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL_HISTORY =
            "SELECT id, client_ip, file_name, io_mode, original_content, encrypted_content, hello_positions, created_at " +
            "FROM lich_su_xu_ly ORDER BY created_at DESC, id DESC";

    private static final String SQL_SELECT_RECENT_HISTORY =
            "SELECT id, client_ip, file_name, io_mode, original_content, encrypted_content, hello_positions, created_at " +
            "FROM lich_su_xu_ly ORDER BY created_at DESC, id DESC LIMIT ?";

    private static final String SQL_CLEAR_HISTORY = "DELETE FROM lich_su_xu_ly";

    private static DatabaseHelper defaultInstance;

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public DatabaseHelper(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        ensureDriverLoaded(jdbcUrl);
        initDatabase();
    }

    /**
     * Đảm bảo JDBC Driver được nạp tường minh (tránh lỗi trùng META-INF/services/java.sql.Driver khi đóng gói Fat JAR).
     */
    private static void ensureDriverLoaded(String url) {
        if (url == null) {
            return;
        }
        String driverClass = url.startsWith("jdbc:h2:")
                ? "org.h2.Driver"
                : (url.startsWith("jdbc:mysql:") ? "com.mysql.cj.jdbc.Driver" : null);
        if (driverClass != null) {
            try {
                Class.forName(driverClass);
            } catch (ClassNotFoundException e) {
                System.err.println("[WARN] Không nạp được JDBC Driver " + driverClass + ": " + e.getMessage());
            }
        }
    }

    public static DatabaseHelper createInMemoryHelper() {
        return new DatabaseHelper(DEFAULT_H2_URL, DEFAULT_H2_USER, DEFAULT_H2_PASSWORD);
    }

    public static DatabaseHelper createMysqlHelper(String host, int port, String dbName, String user, String password) {
        String url = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                host, port, dbName);
        return new DatabaseHelper(url, user, password);
    }

    public static synchronized DatabaseHelper getInstance() {
        if (defaultInstance == null) {
            defaultInstance = createDefaultHelper();
        }
        return defaultInstance;
    }

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
                String url = String.format(
                        "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8",
                        host, port, dbName);

                try {
                    DatabaseHelper helper = new DatabaseHelper(url, user, pass);
                    if (helper.isConnectionAlive()) {
                        System.out.println("[INFO] Kết nối MySQL cục bộ thành công: " + url);
                        return helper;
                    }
                } catch (Exception e) {
                    System.err.println("[WARN] Không kết nối được MySQL local, chuyển sang H2 In-Memory DB: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[WARN] Không đọc được database.properties: " + e.getMessage());
        }

        System.out.println("[INFO] Sử dụng H2 In-Memory Database cho phiên làm việc.");
        return createInMemoryHelper();
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public boolean isConnectionAlive() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

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
     * Ghi bản ghi lịch sử theo tên hàm chuẩn hóa trong SRS FR-06 (insertLog).
     */
    public boolean insertLog(String clientIp, String fileName, String ioMode,
                             String originalContent, String encryptedContent, String helloPositions) {
        return saveHistory(new HistoryRecord(clientIp, fileName, ioMode, originalContent, encryptedContent, helloPositions));
    }

    public boolean saveHistory(HistoryRecord record) {
        if (record == null) {
            return false;
        }

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT_HISTORY, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, record.getClientIp() != null ? record.getClientIp() : "UNKNOWN");
            ps.setString(2, record.getFileName() != null ? record.getFileName() : "unnamed.dat");
            ps.setString(3, record.getIoMode() != null ? record.getIoMode() : "TEXT");
            ps.setString(4, record.getOriginalContent() != null ? record.getOriginalContent() : "");
            ps.setString(5, record.getEncryptedContent() != null ? record.getEncryptedContent() : "");
            ps.setString(6, record.getHelloPositions() != null ? record.getHelloPositions() : "Khong tim thay tu khoa HELLO");

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getLong(1));
                    }
                }
                if (record.getCreatedAt() == null) {
                    record.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi khi lưu bản ghi lịch sử: " + e.getMessage());
        }
        return false;
    }

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

    public void clearHistory() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(SQL_CLEAR_HISTORY);
        } catch (SQLException e) {
            System.err.println("[ERROR] Lỗi khi xóa bảng lịch sử: " + e.getMessage());
        }
    }

    private HistoryRecord mapResultSetToRecord(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String clientIp = rs.getString("client_ip");
        String fileName = rs.getString("file_name");
        String ioMode = rs.getString("io_mode");
        String originalContent = rs.getString("original_content");
        String encryptedContent = rs.getString("encrypted_content");
        String helloPositions = rs.getString("hello_positions");
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new HistoryRecord(id, clientIp, fileName, ioMode, originalContent, encryptedContent, helloPositions, createdAt);
    }

    @Override
    public void close() {
        // Kết nối được quản lý qua try-with-resources trên mỗi truy vấn.
    }
}
