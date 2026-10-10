package dao;

import db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Handles the actual "casting" of a vote. This touches two tables
 * (votes + candidates) so it is wrapped in a transaction - either both
 * updates succeed together or neither of them does. This avoids a
 * situation where a vote gets recorded but the candidate's total_votes
 * count does not go up (or the other way round).
 */
public class VoteDAO {

    public boolean castVote(int voterId, int candidateId) {
        String insertVoteSql = "INSERT INTO votes (voter_id, candidate_id) VALUES (?, ?)";
        String updateCountSql = "UPDATE candidates SET total_votes = total_votes + 1 WHERE candidate_id = ?";
        String updateVoterSql = "UPDATE voters SET has_voted = TRUE WHERE voter_id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false); // start transaction

            try (PreparedStatement ps1 = con.prepareStatement(insertVoteSql)) {
                ps1.setInt(1, voterId);
                ps1.setInt(2, candidateId);
                ps1.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement(updateCountSql)) {
                ps2.setInt(1, candidateId);
                ps2.executeUpdate();
            }

            try (PreparedStatement ps3 = con.prepareStatement(updateVoterSql)) {
                ps3.setInt(1, voterId);
                ps3.executeUpdate();
            }

            con.commit(); // all 3 statements worked, save everything
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // something failed, undo everything
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
