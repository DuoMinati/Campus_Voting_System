package exception;

/**
 * Custom Exception thrown when user or admin authentication fails (Phase 2 - Reliability).
 */
public class AuthenticationException extends Exception {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
