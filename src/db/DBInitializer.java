package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Automatically initializes and verifies MySQL schema and initial seed data.
 * Ensures all 10 candidates with 0 votes, sample voters, and tables are present
 * whenever the system starts.
 */
public class DBInitializer {

    public static void main(String[] args) {
        boolean ok = initializeDatabase();
        System.out.println("Database initialization result: " + (ok ? "SUCCESS" : "FAILED"));
    }

    public static final Object[][] INITIAL_CANDIDATES = {
            {1, "Rahul Sharma", "Student Democratic Front", "🦅", "Advocating for 24/7 digital lab access, upgraded library facilities, and transparent semester project evaluations.", 0},
            {2, "Anita Verma", "Youth Innovation Alliance", "💡", "Focused on national hackathon sponsorships, industry internships, and tech club infrastructure funding.", 0},
            {3, "Suresh Iyer", "Campus United Voice", "🌟", "Committed to hostel amenities modernization, student welfare grievance committee, and sports festival expansion.", 0},
            {4, "Priya Nair", "Creative Arts & Media Forum", "🎨", "Dedicated to annual cultural fest revitalization, campus digital media studio, and creative arts clubs funding.", 0},
            {5, "Rohan Deshmukh", "Engineering & Tech Syndicate", "⚙️", "Pushing for advanced robotics labs, cloud computing credits for students, and open-source project grants.", 0},
            {6, "Sneha Patel", "Eco-Green Campus Movement", "🌱", "Advancing campus sustainability, solar energy on hostel roofs, waste recycling, and paperless administrative workflows.", 0},
            {7, "Mohammed Zeeshan", "Sports & Health League", "⚽", "Upgrading sports complex amenities, inter-college athletic tournaments, and medical insurance coverage for student athletes.", 0},
            {8, "Ananya Roy", "Women in STEM & Equality Council", "🚀", "Promoting mentorship programs, diversity scholarships, female leadership seminars, and safe campus transport.", 0},
            {9, "Tanmay Bhatia", "Entrepreneurship & Incubation Guild", "📈", "Connecting students with venture capital seed funding, startup incubator facilities, and business pitch competitions.", 0},
            {10, "Divya Menon", "Academic Excellence & Research Collective", "📚", "Expanding peer tutoring networks, research publication fee waivers, and international conference travel stipends.", 0}
    };

    public static final Object[][] SAMPLE_VOTERS = {
            {"Vikram Malhotra", "101001", "vikram@campus.edu", "voter123", "14-08-2001", 24, "Male"},
            {"Pooja Deshmukh", "101002", "pooja@campus.edu", "voter123", "22-03-2002", 24, "Female"},
            {"Aakash Banerjee", "101003", "aakash@campus.edu", "voter123", "05-11-2001", 24, "Male"},
            {"Neha Sharma", "101004", "neha@campus.edu", "voter123", "19-01-2002", 24, "Female"},
            {"Kavita Rao", "101005", "kavita@campus.edu", "voter123", "10-07-2001", 24, "Female"}
    };

    public static synchronized boolean initializeDatabase() {
        try (Connection con = DBConnection.getConnection()) {
            if (con == null) return false;

            try (Statement st = con.createStatement()) {
                // 1. Ensure admin table exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS admin (" +
                        "admin_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(50) NOT NULL UNIQUE," +
                        "password VARCHAR(50) NOT NULL" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 2. Ensure voters table exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS voters (" +
                        "voter_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "full_name VARCHAR(100) NOT NULL," +
                        "voter_card_no VARCHAR(20) NOT NULL UNIQUE," +
                        "email VARCHAR(100) NOT NULL UNIQUE," +
                        "password VARCHAR(50) NOT NULL," +
                        "dob VARCHAR(20)," +
                        "age INT NOT NULL," +
                        "gender VARCHAR(10)," +
                        "has_voted BOOLEAN NOT NULL DEFAULT FALSE," +
                        "registered_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 3. Ensure candidates table exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS candidates (" +
                        "candidate_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "candidate_name VARCHAR(100) NOT NULL," +
                        "party_name VARCHAR(100) NOT NULL," +
                        "symbol VARCHAR(50)," +
                        "manifesto TEXT," +
                        "total_votes INT NOT NULL DEFAULT 0" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 4. Ensure votes table exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS votes (" +
                        "vote_id INT AUTO_INCREMENT PRIMARY KEY," +
                        "voter_id INT NOT NULL UNIQUE," +
                        "candidate_id INT NOT NULL," +
                        "voted_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "FOREIGN KEY (voter_id) REFERENCES voters(voter_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (candidate_id) REFERENCES candidates(candidate_id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

                // 5. Ensure election_status exists
                st.executeUpdate("CREATE TABLE IF NOT EXISTS election_status (" +
                        "id INT PRIMARY KEY DEFAULT 1," +
                        "election_title VARCHAR(150) DEFAULT 'MCA College Election 2026'," +
                        "is_active BOOLEAN NOT NULL DEFAULT TRUE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");
            }

            // Ensure manifesto column exists in candidates
            try (Statement st = con.createStatement()) {
                st.executeUpdate("ALTER TABLE candidates ADD COLUMN manifesto TEXT");
            } catch (Exception ignored) {
                // column already exists
            }

            // Seed admin if empty
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM admin")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.executeUpdate("INSERT INTO admin (username, password) VALUES ('admin', 'admin123')");
                }
            }

            // Seed or update 10 candidates
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM candidates")) {
                int count = 0;
                if (rs.next()) count = rs.getInt(1);

                if (count < 10) {
                    // Replace/insert standard 10 candidates
                    for (Object[] c : INITIAL_CANDIDATES) {
                        String upsert = "INSERT INTO candidates (candidate_id, candidate_name, party_name, symbol, manifesto, total_votes) " +
                                "VALUES (?, ?, ?, ?, ?, 0) " +
                                "ON DUPLICATE KEY UPDATE " +
                                "candidate_name = VALUES(candidate_name), " +
                                "party_name = VALUES(party_name), " +
                                "symbol = VALUES(symbol), " +
                                "manifesto = VALUES(manifesto);";
                        try (PreparedStatement ps = con.prepareStatement(upsert)) {
                            ps.setInt(1, (Integer) c[0]);
                            ps.setString(2, (String) c[1]);
                            ps.setString(3, (String) c[2]);
                            ps.setString(4, (String) c[3]);
                            ps.setString(5, (String) c[4]);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Seed sample voters if empty
            try (Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM voters")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String insertVoter = "INSERT INTO voters (full_name, voter_card_no, email, password, dob, age, gender, has_voted) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, FALSE)";
                    for (Object[] v : SAMPLE_VOTERS) {
                        try (PreparedStatement ps = con.prepareStatement(insertVoter)) {
                            ps.setString(1, (String) v[0]);
                            ps.setString(2, (String) v[1]);
                            ps.setString(3, (String) v[2]);
                            ps.setString(4, (String) v[3]);
                            ps.setString(5, (String) v[4]);
                            ps.setInt(6, (Integer) v[5]);
                            ps.setString(7, (String) v[6]);
                            ps.executeUpdate();
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("[Database Initializer Notice] " + e.getMessage());
            return false;
        }
    }
}
