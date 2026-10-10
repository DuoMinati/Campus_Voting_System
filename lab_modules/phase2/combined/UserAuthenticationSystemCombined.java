// UserAuthenticationSystemCombined.java
// Combined Single File Version for Lab Exams & Practical Tests (Phase 2)

import java.util.Scanner;

class InvalidInputException extends Exception {
    public InvalidInputException(String msg) {
        super(msg);
    }
}

interface UserOperations {

    boolean validateUser(String username, String password);

    boolean login(String username, String password);

}

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

    public void displayUser() {
        System.out.println("\nUser Details");
        System.out.println("Username : " + username);
    }
}

class UserService implements UserOperations {

    @Override
    public boolean validateUser(String username, String password) {

        try {

            if(username == null || username.trim().isEmpty())
                throw new InvalidInputException("Username cannot be empty.");

            if(username.length() < 5)
                throw new InvalidInputException("Username should have at least 5 characters.");

            if(password == null || password.length() < 8)
                throw new InvalidInputException("Password should have at least 8 characters.");

            return true;

        }
        catch(InvalidInputException e) {

            System.out.println("Error: " + e.getMessage());

            return false;

        }

    }

    @Override
    public boolean login(String username, String password) {

        return "admin".equals(username) &&
               "admin123".equals(password);

    }

}

public class UserAuthenticationSystemCombined {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== User Authentication System (Phase 2 Combined) =====");

        System.out.print("Username : ");
        String username = sc.nextLine();

        System.out.print("Password : ");
        String password = sc.nextLine();

        UserService service = new UserService();

        if(service.validateUser(username, password)) {

            User user = new User(username, password);

            if(service.login(username, password)) {

                System.out.println("\nLogin Successful");

                user.displayUser();

            }
            else {

                System.out.println("\nInvalid Login Credentials");

            }

        }

        sc.close();

    }

}
