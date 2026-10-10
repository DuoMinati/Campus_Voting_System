// Main.java
// Module 3 - Main Application (Phase 1)

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("====== User Registration System (Phase 1) ======");

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
