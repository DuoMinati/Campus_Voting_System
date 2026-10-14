package model;

/**
 * Plain model class that represents one row of the candidates table.
 */
public class Candidate {

    private int candidateId;
    private String candidateName;
    private String partyName;
    private String symbol;
    private String manifesto;
    private int totalVotes;

    public Candidate() {
    }

    public Candidate(int candidateId, String candidateName, String partyName, String symbol, int totalVotes) {
        this(candidateId, candidateName, partyName, symbol, "", totalVotes);
    }

    public Candidate(int candidateId, String candidateName, String partyName, String symbol, String manifesto, int totalVotes) {
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.partyName = partyName;
        this.symbol = symbol;
        this.manifesto = manifesto;
        this.totalVotes = totalVotes;
    }

    public int getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(int candidateId) {
        this.candidateId = candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getManifesto() {
        return manifesto;
    }

    public void setManifesto(String manifesto) {
        this.manifesto = manifesto;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    @Override
    public String toString() {
        // used to display the candidate nicely inside JRadioButton / JComboBox etc
        return candidateName + "  (" + partyName + ")";
    }
}
