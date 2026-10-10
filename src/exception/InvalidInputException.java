package exception;

/**
 * Custom Exception thrown when input validation fails (Phase 2 - Reliability).
 * Using a checked exception ensures that callers must explicitly handle
 * validation failures via try-catch or declaration.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
