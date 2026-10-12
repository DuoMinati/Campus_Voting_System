package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Small helper class that opens a connection to the voting_system
 * MySQL database.
 *
 * NOTE: change DB_USER / DB_PASSWORD below to match your own
 * MySQL Workbench / XAMPP login before running the project.
 */
public class DBConnection {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/voting_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true";
    private static final String DB_USER = "root";
    // Configured password for this machine, with fallback to root/blank for other environments
    private static final String PRIMARY_PASSWORD = "Jyotirmoy@123";
    private static volatile String cachedWorkingPassword = null;

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found. Did you add mysql-connector-j jar to the classpath?", e);
        }

        if (cachedWorkingPassword != null) {
            try {
                return DriverManager.getConnection(DB_URL, DB_USER, cachedWorkingPassword);
            } catch (SQLException ignored) {
                cachedWorkingPassword = null; // Invalidate cache and retry candidate list
            }
        }

        String envPass = System.getenv("DB_PASSWORD");
        if (envPass == null) {
            envPass = System.getProperty("DB_PASSWORD");
        }

        String[] passwords = envPass != null 
                ? new String[]{envPass, PRIMARY_PASSWORD, "root", ""} 
                : new String[]{PRIMARY_PASSWORD, "root", ""};

        SQLException lastEx = null;
        for (String pass : passwords) {
            try {
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, pass);
                cachedWorkingPassword = pass;
                return conn;
            } catch (SQLException ex) {
                lastEx = ex;
            }
        }

        throw lastEx != null ? lastEx : new SQLException("Unable to connect to MySQL database.");
    }

    /**
     * Checks if MySQL database is reachable.
     */
    public static boolean testConnection() {
        try (Connection con = getConnection()) {
            return con != null && !con.isClosed();
        } catch (Exception e) {
            return false;
        }
    }
}
