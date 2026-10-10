package service;

import dao.AdminDAO;
import dao.VoterDAO;
import exception.AuthenticationException;
import exception.InvalidInputException;
import model.Voter;
import validation.Validation;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class that handles business logic for users and voters.
 * Implements the UserOperations interface.
 */
public class UserService implements UserOperations {

    private final VoterDAO voterDAO;
    private final AdminDAO adminDAO;

    // In-memory list to allow registration/login demo even when MySQL is not running
    private static final List<Voter> localVoters = new ArrayList<>();

    public UserService() {
        this.voterDAO = new VoterDAO();
        this.adminDAO = new AdminDAO();
    }

    @Override
    public boolean validateUser(String username, String password) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        if (username.trim().length() < 5) {
            throw new InvalidInputException("Username must contain at least 5 characters.");
        }
        if (password == null || password.length() < 8) {
            throw new InvalidInputException("Password must contain at least 8 characters.");
        }
        return true;
    }

    @Override
    public boolean login(String username, String password) {
        return "admin".equals(username) && "admin123".equals(password);
    }

    @Override
    public boolean registerVoter(Voter voter, String confirmPassword) throws InvalidInputException {
        if (voter == null) {
            throw new InvalidInputException("Voter details cannot be empty.");
        }

        // Validate basic fields
        if (voter.getFullName() == null || voter.getFullName().trim().isEmpty()) {
            throw new InvalidInputException("Full name cannot be empty.");
        }
        Validation.validateVoterCardNo(voter.getVoterCardNo());
        Validation.validateEmail(voter.getEmail());

        Validation.validatePassword(voter.getPassword());

        if (confirmPassword == null || !confirmPassword.equals(voter.getPassword())) {
            throw new InvalidInputException("Passwords do not match.");
        }

        // Try saving to MySQL database
        boolean dbSaved = false;
        try {
            dbSaved = voterDAO.registerVoter(voter);
        } catch (Exception e) {
            System.out.println("Note: Database not available, storing in local session.");
        }

        if (dbSaved) {
            return true;
        }

        // Fallback: Store in local list so testing always works
        for (Voter v : localVoters) {
            if (v.getEmail().equalsIgnoreCase(voter.getEmail()) ||
                v.getVoterCardNo().equalsIgnoreCase(voter.getVoterCardNo())) {
                throw new InvalidInputException("Email or Voter Card number is already in use.");
            }
        }
        voter.setVoterId(localVoters.size() + 101);
        localVoters.add(voter);
        return true;
    }

    @Override
    public Voter loginVoter(String email, String password) throws InvalidInputException, AuthenticationException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Password cannot be empty.");
        }

        // 1. Try database login
        try {
            Voter voter = voterDAO.login(email.trim(), password);
            if (voter != null) {
                return voter;
            }
        } catch (Exception ignored) {
        }

        // 2. Try local list
        for (Voter v : localVoters) {
            if (v.getEmail().equalsIgnoreCase(email.trim()) && v.getPassword().equals(password)) {
                return v;
            }
        }

        throw new AuthenticationException("Invalid email or password. Please try again.");
    }

    @Override
    public boolean loginAdmin(String username, String password) throws InvalidInputException, AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Admin username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidInputException("Admin password cannot be empty.");
        }

        // Check database
        try {
            if (adminDAO.login(username.trim(), password)) {
                return true;
            }
        } catch (Exception ignored) {
        }

        // Default admin credentials
        if ("admin".equals(username.trim()) && "admin123".equals(password)) {
            return true;
        }

        throw new AuthenticationException("Invalid admin credentials.");
    }
}
