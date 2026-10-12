package dao;

import db.DBConnection;

import java.sql.*;

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

    /**
     * Deletes a single ballot cast by a voter.
     * Decrements the candidate's vote tally by 1 and resets the voter's has_voted flag to FALSE.
     */
    public boolean deleteVoteByVoterId(int voterId) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int candidateId = -1;
            String findSql = "SELECT candidate_id FROM votes WHERE voter_id = ?";
            try (PreparedStatement psFind = con.prepareStatement(findSql)) {
                psFind.setInt(1, voterId);
                ResultSet rs = psFind.executeQuery();
                if (rs.next()) {
                    candidateId = rs.getInt("candidate_id");
                }
            }

            if (candidateId == -1) {
                con.rollback();
                return false;
            }

            // 1. Delete ballot from votes table
            String delVoteSql = "DELETE FROM votes WHERE voter_id = ?";
            try (PreparedStatement psDel = con.prepareStatement(delVoteSql)) {
                psDel.setInt(1, voterId);
                psDel.executeUpdate();
            }

            // 2. Decrement candidate tally
            String decSql = "UPDATE candidates SET total_votes = GREATEST(0, total_votes - 1) WHERE candidate_id = ?";
            try (PreparedStatement psDec = con.prepareStatement(decSql)) {
                psDec.setInt(1, candidateId);
                psDec.executeUpdate();
            }

            // 3. Reset voter has_voted status
            String resetVoterSql = "UPDATE voters SET has_voted = FALSE WHERE voter_id = ?";
            try (PreparedStatement psReset = con.prepareStatement(resetVoterSql)) {
                psReset.setInt(1, voterId);
                psReset.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Deletes a ballot by its vote_id primary key.
     */
    public boolean deleteVoteByVoteId(int voteId) {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            int voterId = -1;
            int candidateId = -1;
            String findSql = "SELECT voter_id, candidate_id FROM votes WHERE vote_id = ?";
            try (PreparedStatement psFind = con.prepareStatement(findSql)) {
                psFind.setInt(1, voteId);
                ResultSet rs = psFind.executeQuery();
                if (rs.next()) {
                    voterId = rs.getInt("voter_id");
                    candidateId = rs.getInt("candidate_id");
                }
            }

            if (voterId == -1) {
                con.rollback();
                return false;
            }

            // 1. Delete ballot
            try (PreparedStatement psDel = con.prepareStatement("DELETE FROM votes WHERE vote_id = ?")) {
                psDel.setInt(1, voteId);
                psDel.executeUpdate();
            }

            // 2. Decrement candidate
            try (PreparedStatement psDec = con.prepareStatement("UPDATE candidates SET total_votes = GREATEST(0, total_votes - 1) WHERE candidate_id = ?")) {
                psDec.setInt(1, candidateId);
                psDec.executeUpdate();
            }

            // 3. Reset voter
            try (PreparedStatement psReset = con.prepareStatement("UPDATE voters SET has_voted = FALSE WHERE voter_id = ?")) {
                psReset.setInt(1, voterId);
                psReset.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Resets all election votes: clears votes table, resets candidate tallies to 0, resets all voters.
     */
    public boolean resetAllVotes() {
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            con.setAutoCommit(false);

            try (Statement st = con.createStatement()) {
                st.executeUpdate("DELETE FROM votes");
                st.executeUpdate("UPDATE candidates SET total_votes = 0");
                st.executeUpdate("UPDATE voters SET has_voted = FALSE");
            }

            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    /**
     * Gets the candidate details for a voter's cast ballot so the logged-in voter can see whom they voted for.
     */
    public model.Candidate getVotedCandidateForVoter(int voterId) {
        String sql = "SELECT c.candidate_id, c.candidate_name, c.party_name, c.symbol, c.total_votes " +
                "FROM votes v JOIN candidates c ON v.candidate_id = c.candidate_id WHERE v.voter_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, voterId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new model.Candidate(
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

    /**
     * Returns full audit log of all votes cast.
     */
    public java.util.List<java.util.Map<String, Object>> getAllVotesAudit() {
        java.util.List<java.util.Map<String, Object>> list = new java.util.ArrayList<>();
        String sql = "SELECT v.vote_id, v.voter_id, v.candidate_id, v.voted_on, " +
                "u.full_name, u.voter_card_no, c.candidate_name, c.party_name, c.symbol " +
                "FROM votes v " +
                "JOIN voters u ON v.voter_id = u.voter_id " +
                "JOIN candidates c ON v.candidate_id = c.candidate_id " +
                "ORDER BY v.vote_id DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                java.util.Map<String, Object> map = new java.util.LinkedHashMap<>();
                map.put("voteId", "VOTE-" + rs.getInt("vote_id"));
                map.put("voterId", rs.getString("voter_card_no"));
                map.put("voterDbId", rs.getInt("voter_id"));
                map.put("voterName", rs.getString("full_name"));
                map.put("candidateId", rs.getInt("candidate_id"));
                map.put("candidateName", rs.getString("candidate_name"));
                map.put("candidateParty", rs.getString("party_name"));
                map.put("candidateSymbol", rs.getString("symbol"));
                map.put("timestamp", rs.getTimestamp("voted_on") != null ? rs.getTimestamp("voted_on").toString() : "");
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
