package dao;

import db.DBConnection;
import model.Candidate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidateDAO {

    public boolean addCandidate(String name, String party, String symbol) {
        String sql = "INSERT INTO candidates (candidate_name, party_name, symbol) VALUES (?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setString(2, party);
            ps.setString(3, symbol);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCandidate(int candidateId) {
        String sql = "DELETE FROM candidates WHERE candidate_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, candidateId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Candidate> getAllCandidates() {
        List<Candidate> list = new ArrayList<>();
        String sql = "SELECT * FROM candidates ORDER BY candidate_id";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Candidate c = new Candidate(
                        rs.getInt("candidate_id"),
                        rs.getString("candidate_name"),
                        rs.getString("party_name"),
                        rs.getString("symbol"),
                        rs.getInt("total_votes")
                );
                list.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // called right after a vote row is inserted, to bump the running total up by 1
    public boolean incrementVoteCount(int candidateId) {
        String sql = "UPDATE candidates SET total_votes = total_votes + 1 WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, candidateId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // called when a ballot is deleted by admin, to decrease the candidate's total by 1
    public boolean decrementVoteCount(int candidateId) {
        String sql = "UPDATE candidates SET total_votes = GREATEST(0, total_votes - 1) WHERE candidate_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, candidateId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // reset all candidate vote tallies back to 0
    public boolean resetAllVotes() {
        String sql = "UPDATE candidates SET total_votes = 0";
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Candidate getCandidateById(int candidateId) {
        String sql = "SELECT * FROM candidates WHERE candidate_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Candidate(
                        rs.getInt("candidate_id"),
                        rs.getString("candidate_name"),
                        rs.getString("party_name"),
                        rs.getString("symbol"),
                        rs.getInt("total_votes")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

