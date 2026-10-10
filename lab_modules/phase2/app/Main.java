package app;

import java.util.Scanner;

import model.User;
import service.UserService;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("===== User Authentication System (Phase 2) =====");

        System.out.print("Enter Username : ");
        String username = sc.nextLine();

        System.out.print("Enter Password : ");
        String password = sc.nextLine();

        UserService service = new UserService();

        if(service.validateUser(username, password)) {

            User user = new User(username, password);

            if(service.login(username, password)) {

                System.out.println("\nLogin Successful");

                user.displayUser();

            }
            else {

                System.out.println("\nInvalid Username or Password");

            }

        }

        sc.close();

    }

}
