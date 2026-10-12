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

    // checks (email OR voter_card_no) + password and returns Voter object if matched, else null
    public Voter login(String identifier, String password) {
        String sql = "SELECT * FROM voters WHERE (email = ? OR voter_card_no = ?) AND password = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, identifier);
            ps.setString(2, identifier);
            ps.setString(3, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToVoter(rs);
            }

        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
        }
        return null;
    }

    public Voter getVoterByCardNo(String voterCardNo) {
        String sql = "SELECT * FROM voters WHERE voter_card_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, voterCardNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToVoter(rs);
            }
        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
        }
        return null;
    }

    public Voter getVoterById(int voterId) {
        String sql = "SELECT * FROM voters WHERE voter_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToVoter(rs);
            }
        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
        }
        return null;
    }

    public java.util.List<Voter> getAllVoters() {
        java.util.List<Voter> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM voters ORDER BY voter_id";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToVoter(rs));
            }
        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
        }
        return list;
    }

    private Voter mapResultSetToVoter(ResultSet rs) throws SQLException {
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

    // resets voter's voting status back to false (e.g. when ballot is deleted)
    public boolean resetVotedStatus(int voterId) {
        String sql = "UPDATE voters SET has_voted = FALSE WHERE voter_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voterId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVoterByCardNo(String cardNo) {
        String sql = "DELETE FROM voters WHERE voter_card_no = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cardNo);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVoterById(int voterId) {
        String sql = "DELETE FROM voters WHERE voter_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voterId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[Database Info] " + e.getMessage());
            return false;
        }
    }

    public boolean resetAllVoters() {
        String sql = "UPDATE voters SET has_voted = FALSE";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
            return true;
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
