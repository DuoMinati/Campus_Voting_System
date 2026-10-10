package model;

/**
 * Base User class for the voting system.
 * Demonstrates basic OOP concepts: private fields, constructors,
 * getters/setters (encapsulation), and display methods.
 */
public class User {

    private String username;
    private String password;
    private String role; // "VOTER" or "ADMIN"

    // Default constructor
    public User() {
        this.role = "USER";
    }

    // Parameterized constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = "USER";
    }

    // Constructor with role
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Standard getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // Print user details to console
    public void displayUser() {
        System.out.println("\n---------------- User Details ----------------");
        System.out.println("Username : " + username);
        System.out.println("Role     : " + role);
        System.out.println("----------------------------------------------");
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", role=" + role + "]";
    }
}
