package service;

import exception.AuthenticationException;
import exception.InvalidInputException;
import model.Voter;

/**
 * Interface for user and voter operations (Phase 2).
 * Defines what operations can be performed on users.
 */
public interface UserOperations {

    // Validates username and password format (Phase 2 lab requirement)
    boolean validateUser(String username, String password) throws InvalidInputException;

    // Checks default login credentials
    boolean login(String username, String password);

    // Voter registration with validation
    boolean registerVoter(Voter voter, String confirmPassword) throws InvalidInputException;

    // Voter authentication
    Voter loginVoter(String email, String password) throws InvalidInputException, AuthenticationException;

    // Administrator authentication
    boolean loginAdmin(String username, String password) throws InvalidInputException, AuthenticationException;
}
