// User.java
// Module 1 - User Entity (Phase 1)

public class User {

    private String username;
    private String password;

    // Default Constructor
    public User() {
    }

    // Parameterized Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter Methods
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setter Methods
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Display User Details
    public void displayUser() {
        System.out.println("\nUser Details");
        System.out.println("Username : " + username);
        System.out.println("Password : " + password);
    }
}
