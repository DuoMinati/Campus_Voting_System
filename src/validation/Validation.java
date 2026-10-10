package validation;

import exception.InvalidInputException;
import model.Voter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Helper class for input validation.
 * Uses core Java String, Character, and DateTime methods.
 */
public class Validation {

    // Validates username
    public static void validateUsername(String username) throws InvalidInputException {
        if (username == null || username.trim().isEmpty()) {
            throw new InvalidInputException("Username cannot be empty.");
        }
        if (username.trim().length() < 5) {
            throw new InvalidInputException("Username must contain at least 5 characters.");
        }
    }

    // Validates password (min 8 characters, at least one digit)
    public static void validatePassword(String password) throws InvalidInputException {
        if (password == null || password.trim().isEmpty()) {
            throw new InvalidInputException("Password cannot be empty.");
        }
        if (password.length() < 8) {
            throw new InvalidInputException("Password must contain at least 8 characters.");
        }

        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            if (Character.isDigit(password.charAt(i))) {
                hasDigit = true;
                break;
            }
        }

        if (!hasDigit) {
            throw new InvalidInputException("Password must contain at least one digit (0-9).");
        }
    }

    // Basic email check
    public static void validateEmail(String email) throws InvalidInputException {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty.");
        }

        String trimmed = email.trim();
        int atIndex = trimmed.indexOf('@');
        int dotIndex = trimmed.lastIndexOf('.');

        if (atIndex < 1 || dotIndex <= atIndex + 1 || dotIndex >= trimmed.length() - 1) {
            throw new InvalidInputException("Please enter a valid email address (e.g. name@college.edu).");
        }
    }

    /**
     * Voter ID Validation:
     * - Must be EXACTLY 6 digits.
     * - Does NOT accept letters (like 'a') or symbols.
     * - Does NOT accept more or fewer than 6 digits.
     */
    public static void validateVoterCardNo(String cardNo) throws InvalidInputException {
        if (cardNo == null || cardNo.trim().isEmpty()) {
            throw new InvalidInputException("Voter ID cannot be empty.");
        }

        String trimmed = cardNo.trim();

        if (trimmed.length() != 6) {
            throw new InvalidInputException("Voter ID must be exactly 6 digits (Entered " + trimmed.length() + " characters).");
        }

        for (int i = 0; i < trimmed.length(); i++) {
            char ch = trimmed.charAt(i);
            if (!Character.isDigit(ch)) {
                throw new InvalidInputException("Voter ID must only contain numbers (0-9). Letters like '" + ch + "' are not allowed.");
            }
        }
    }

    /**
     * Validates Date of Birth and calculates the age in years.
     * Supports formats: DD-MM-YYYY, DD/MM/YYYY, or YYYY-MM-DD.
     * Note: The "must be 18 years old" restriction has been removed as requested.
     */
    public static int validateAndCalculateAgeFromDob(String dobText) throws InvalidInputException {
        if (dobText == null || dobText.trim().isEmpty()) {
            throw new InvalidInputException("Date of Birth cannot be empty.");
        }

        String cleaned = dobText.trim().replace('/', '-');
        LocalDate birthDate = null;

        // Try DD-MM-YYYY first
        DateTimeFormatter[] formatters = new DateTimeFormatter[]{
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d-M-yyyy")
        };

        for (DateTimeFormatter fmt : formatters) {
            try {
                birthDate = LocalDate.parse(cleaned, fmt);
                break;
            } catch (DateTimeParseException ignored) {
            }
        }

        if (birthDate == null) {
            throw new InvalidInputException("Invalid Date of Birth format. Please use DD-MM-YYYY (e.g. 15-08-2004).");
        }

        LocalDate today = LocalDate.now();
        if (birthDate.isAfter(today)) {
            throw new InvalidInputException("Date of Birth cannot be in the future.");
        }

        int calculatedAge = Period.between(birthDate, today).getYears();
        if (calculatedAge > 120) {
            throw new InvalidInputException("Please enter a valid Date of Birth.");
        }

        return calculatedAge;
    }

    /**
     * Comprehensive registration validator with Date of Birth and 6-digit Voter ID.
     */
    public static Voter validateAndCreateVoter(String name, String card, String email,
                                              String dobText, String gender,
                                              String pass, String confirmPass) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Full Name cannot be empty.");
        }
        if (name.trim().length() < 3) {
            throw new InvalidInputException("Full Name must contain at least 3 characters.");
        }

        // 1. Exactly 6 digits check (no letters allowed)
        validateVoterCardNo(card);

        // 2. Email format check
        validateEmail(email);

        // 3. Date of Birth check (calculates age, no 18-year restriction)
        int age = validateAndCalculateAgeFromDob(dobText);

        // 4. Password checks
        validatePassword(pass);

        if (confirmPass == null || !pass.equals(confirmPass)) {
            throw new InvalidInputException("Passwords do not match.");
        }

        if (gender == null || gender.trim().isEmpty()) {
            gender = "Other";
        }

        return new Voter(name.trim(), card.trim(), email.trim().toLowerCase(), pass, dobText.trim(), age, gender.trim());
    }

    // Boolean helpers for console testing
    public static boolean checkUsername(String username) {
        try {
            validateUsername(username);
            return true;
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }

    public static boolean checkPassword(String password) {
        try {
            validatePassword(password);
            return true;
        } catch (InvalidInputException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    }
}
