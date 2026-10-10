package service;

import exception.InvalidInputException;

public class UserService implements UserOperations {

    @Override
    public boolean validateUser(String username, String password) {

        try {

            if(username == null || username.trim().isEmpty()) {
                throw new InvalidInputException("Username cannot be empty.");
            }

            if(username.length() < 5) {
                throw new InvalidInputException("Username must contain at least 5 characters.");
            }

            if(password == null || password.length() < 8) {
                throw new InvalidInputException("Password must contain at least 8 characters.");
            }

            return true;

        }
        catch(InvalidInputException e) {
            System.out.println("Validation Error: " + e.getMessage());
            return false;
        }

    }

    @Override
    public boolean login(String username, String password) {

        return "admin".equals(username) &&
               "admin123".equals(password);
    }

}
