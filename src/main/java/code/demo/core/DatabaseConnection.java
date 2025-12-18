package code.demo.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    private DatabaseConnection() { }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }

    public synchronized void initialize() {
        if (dataSource != null) return;

        String url = System.getenv().getOrDefault("LMS_DB_URL", "jdbc:mysql://sql8.freesqldatabase.com:3306/sql8812338");
        String user = System.getenv().getOrDefault("LMS_DB_USER", "sql8812338");
        String pass = System.getenv().getOrDefault("LMS_DB_PASS", "unUeIUTGyQ");

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(10);
        cfg.setPoolName("LMSPool");
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(cfg);

        createSchemaAndSeed();
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) throw new SQLException("DataSource not initialized");
        return dataSource.getConnection();
    }

    public boolean testConnection() {
        try (Connection c = getConnection()) {
            return c != null && c.isValid(2);
        } catch (SQLException e) { return false; }
    }

    private void createSchemaAndSeed() {
        String usersSql = "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "username VARCHAR(255) UNIQUE NOT NULL, " +
                "password VARCHAR(255) NOT NULL, " +
                "role ENUM('ADMIN','USER') NOT NULL)";

        String booksSql = "CREATE TABLE IF NOT EXISTS books (" +
                "id INT PRIMARY KEY AUTO_INCREMENT, " +
                "title VARCHAR(255) NOT NULL, " +
                "author VARCHAR(255) NOT NULL, " +
                "category VARCHAR(100) NOT NULL, " +
                "publish_date DATE, " +
                "isbn VARCHAR(32) UNIQUE)";

        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute(usersSql);
            st.execute(booksSql);

            // Seed users if empty
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    try (PreparedStatement ps = c.prepareStatement("INSERT INTO users (username, password, role) VALUES (?,?,?)")) {
                        ps.setString(1, "admin@lib.com");
                        ps.setString(2, "admin123"); // In production, store hashed passwords
                        ps.setString(3, "ADMIN");
                        ps.addBatch();

                        ps.setString(1, "user@lib.com");
                        ps.setString(2, "user123");
                        ps.setString(3, "USER");
                        ps.addBatch();
                        ps.executeBatch();
                    }
                }
            }
        } catch (SQLException e) {
            // Basic logging to stderr for now; controllers will show friendly messages
            System.err.println("[DB INIT ERROR] " + e.getMessage());
        }
    }
}
