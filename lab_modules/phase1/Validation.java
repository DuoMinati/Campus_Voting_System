// Validation.java
// Module 2 - Validation Logic (Phase 1)

public class Validation {

    // Username Validation
    public static boolean validateUsername(String username) {

        if(username == null || username.trim().isEmpty()) {
            System.out.println("Username cannot be empty.");
            return false;
        }

        if(username.length() < 5) {
            System.out.println("Username should contain at least 5 characters.");
            return false;
        }

        return true;
    }

    // Password Validation
    public static boolean validatePassword(String password) {

        if(password == null || password.length() < 8) {
            System.out.println("Password should contain at least 8 characters.");
            return false;
        }

        boolean hasDigit = false;

        for(int i = 0; i < password.length(); i++) {

            if(Character.isDigit(password.charAt(i))) {
                hasDigit = true;
                break;
            }
        }

        if(!hasDigit) {
            System.out.println("Password must contain at least one digit.");
            return false;
        }

        return true;
    }
}
