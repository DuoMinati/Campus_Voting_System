// UserRegistrationSystemCombined.java
// Combined Single File Version for Lab Exams & Practical Tests (Phase 1)

import java.util.Scanner;

class User {

    private String username;
    private String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void displayUser() {
        System.out.println("\nUser Details");
        System.out.println("Username : " + username);
        System.out.println("Password : " + password);
    }
}

class Validation {

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

public class UserRegistrationSystemCombined {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("====== User Registration System (Phase 1 Combined) ======");

        System.out.print("Enter Username : ");
        String username = sc.nextLine();

        System.out.print("Enter Password : ");
        String password = sc.nextLine();

        if(Validation.validateUsername(username) &&
           Validation.validatePassword(password))
        {
            User user = new User(username, password);

            System.out.println("\nRegistration Successful!");

            user.displayUser();
        }
        else
        {
            System.out.println("\nRegistration Failed.");
        }

        sc.close();
    }
}
