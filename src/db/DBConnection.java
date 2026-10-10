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
            "jdbc:mysql://localhost:3306/voting_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root"; // <-- put your own MySQL password here

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // this normally means the mysql-connector-j jar is not on the classpath
            throw new SQLException("MySQL JDBC Driver not found. Did you add mysql-connector-j jar to the classpath?", e);
        }
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
