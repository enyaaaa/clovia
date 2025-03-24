// DBConnection.java
// Utility class for database connections using Config values
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String dbUrl = Config.DB_URL_BASE + Config.DB_NAME + Config.DB_PARAMS;
            return DriverManager.getConnection(dbUrl, Config.DB_USER, Config.DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Database driver not found: " + e.getMessage());
        }
    }
}