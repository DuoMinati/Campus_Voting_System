package db;

import java.sql.*;

/**
 * Terminal utility to view all tables and records in the voting_system database.
 * Run from VS Code terminal with:
 *   java -cp "bin;lib/*" db.ViewDatabase
 */
public class ViewDatabase {

    public static void main(String[] args) {
        System.out.println("==================================================================================");
        System.out.println("                 ONLINE VOTING SYSTEM - DATABASE VIEWER");
        System.out.println("==================================================================================");

        try (Connection con = DBConnection.getConnection()) {
            System.out.println("Status: Connected to MySQL database [voting_system]\n");

            printTable(con, "voters", "REGISTERED VOTERS TABLE");
            printTable(con, "candidates", "CANDIDATES TABLE");
            printTable(con, "votes", "BALLOTS / VOTES CAST TABLE");
            printTable(con, "admin", "ADMINISTRATOR TABLE");
            printTable(con, "election_status", "ELECTION STATUS TABLE");

        } catch (SQLException e) {
            System.err.println("\n[Database Connection Note]");
            System.err.println("Could not connect to MySQL: " + e.getMessage());
            System.err.println("\nTip: To configure your MySQL password, open 'src/db/DBConnection.java'");
            System.err.println("and change DB_PASSWORD on line 19 to match your MySQL root password.");
        }
    }

    public static void printTable(Connection con, String tableName, String title) {
        String query = "SELECT * FROM " + tableName;
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            System.out.println("----------------------------------------------------------------------------------");
            System.out.println("  " + title);
            System.out.println("----------------------------------------------------------------------------------");

            // Print Header
            StringBuilder header = new StringBuilder();
            for (int i = 1; i <= colCount; i++) {
                header.append(String.format("%-18s", meta.getColumnName(i)));
            }
            System.out.println(header);
            System.out.println("-".repeat(Math.max(20, colCount * 18)));

            // Print Rows
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                StringBuilder row = new StringBuilder();
                for (int i = 1; i <= colCount; i++) {
                    String val = rs.getString(i);
                    if (val == null) val = "NULL";
                    if (val.length() > 16) val = val.substring(0, 13) + "...";
                    row.append(String.format("%-18s", val));
                }
                System.out.println(row);
            }

            if (rowCount == 0) {
                System.out.println("  (Table is currently empty)");
            }
            System.out.println();

        } catch (SQLException e) {
            System.out.println("Could not read table '" + tableName + "': " + e.getMessage());
        }
    }
}
