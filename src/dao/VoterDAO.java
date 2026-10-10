package dao;

import db.DBConnection;
import model.Voter;

import java.sql.*;

/**
 * All the database operations related to a voter are kept here
 * (register, login, mark as voted, etc.) so the GUI classes stay clean.
 */
public class VoterDAO {

    // returns true if registration went through fine
    public boolean registerVoter(Voter voter) {
        String sql = "INSERT INTO voters (full_name, voter_card_no, email, password, dob, age, gender) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, voter.getFullName());
            ps.setString(2, voter.getVoterCardNo());
            ps.setString(3, voter.getEmail());
            ps.setString(4, voter.getPassword());
            ps.setString(5, voter.getDob() != null ? voter.getDob() : "");
            ps.setInt(6, voter.getAge());
            ps.setString(7, voter.getGender());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
            return false;
        }
    }

    // checks email + password and returns the full Voter object if matched, else null
    public Voter login(String email, String password) {
        String sql = "SELECT * FROM voters WHERE email = ? AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voter v = new Voter();
                v.setVoterId(rs.getInt("voter_id"));
                v.setFullName(rs.getString("full_name"));
                v.setVoterCardNo(rs.getString("voter_card_no"));
                v.setEmail(rs.getString("email"));
                v.setPassword(rs.getString("password"));
                try {
                    v.setDob(rs.getString("dob"));
                } catch (SQLException ignored) {
                }
                v.setAge(rs.getInt("age"));
                v.setGender(rs.getString("gender"));
                v.setHasVoted(rs.getBoolean("has_voted"));
                return v;
            }

        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
        }
        return null;
    }

    // once a voter casts their vote we flip this flag so they can never vote again
    public boolean markAsVoted(int voterId) {
        String sql = "UPDATE voters SET has_voted = TRUE WHERE voter_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voterId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
            return false;
        }
    }

    public int getTotalVoterCount() {
        String sql = "SELECT COUNT(*) FROM voters";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
        }
        return 0;
    }

    public int getTotalVotesCastCount() {
        String sql = "SELECT COUNT(*) FROM voters WHERE has_voted = TRUE";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
        }
        return 0;
    }
}
