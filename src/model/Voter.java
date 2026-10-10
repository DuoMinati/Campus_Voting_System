package model;

/**
 * Voter class extending base User.
 * Demonstrates inheritance (Voter IS-A User) with voter-specific attributes.
 */
public class Voter extends User {

    private int voterId;
    private String fullName;
    private String voterCardNo; // Exactly 6 digits
    private String email;
    private String dob;         // Date of Birth (e.g. DD-MM-YYYY)
    private int age;
    private String gender;
    private boolean hasVoted;

    // Default constructor
    public Voter() {
        super();
        setRole("VOTER");
    }

    // Constructor with Date of Birth
    public Voter(String fullName, String voterCardNo, String email, String password, String dob, int age, String gender) {
        super(email, password, "VOTER");
        this.fullName = fullName;
        this.voterCardNo = voterCardNo;
        this.email = email;
        this.dob = dob;
        this.age = age;
        this.gender = gender;
        this.hasVoted = false;
    }

    // Overloaded constructor without explicit dob (for backward compatibility)
    public Voter(String fullName, String voterCardNo, String email, String password, int age, String gender) {
        this(fullName, voterCardNo, email, password, "", age, gender);
    }

    // Full constructor (for database retrieval)
    public Voter(int voterId, String fullName, String voterCardNo, String email, String password, int age, String gender, boolean hasVoted) {
        super(email, password, "VOTER");
        this.voterId = voterId;
        this.fullName = fullName;
        this.voterCardNo = voterCardNo;
        this.email = email;
        this.dob = "";
        this.age = age;
        this.gender = gender;
        this.hasVoted = hasVoted;
    }

    // Getters and setters
    public int getVoterId() {
        return voterId;
    }

    public void setVoterId(int voterId) {
        this.voterId = voterId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVoterCardNo() {
        return voterCardNo;
    }

    public void setVoterCardNo(String voterCardNo) {
        this.voterCardNo = voterCardNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        setUsername(email); // Keep username in sync with email
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public void setHasVoted(boolean hasVoted) {
        this.hasVoted = hasVoted;
    }

    // Override displayUser to show full voter profile
    @Override
    public void displayUser() {
        System.out.println("\n---------------- Voter Details ----------------");
        System.out.println("Voter ID      : " + (voterId > 0 ? voterId : "Pending Registration"));
        System.out.println("Full Name     : " + fullName);
        System.out.println("Voter Card No : " + voterCardNo);
        System.out.println("Email         : " + email);
        if (dob != null && !dob.isEmpty()) {
            System.out.println("Date of Birth : " + dob);
        }
        System.out.println("Age           : " + age);
        System.out.println("Gender        : " + gender);
        System.out.println("Has Voted?    : " + (hasVoted ? "Yes" : "No"));
        System.out.println("Role          : " + getRole());
        System.out.println("-----------------------------------------------");
    }

    @Override
    public String toString() {
        return "Voter [fullName=" + fullName + ", voterCardNo=" + voterCardNo + ", email=" + email + "]";
    }
}
