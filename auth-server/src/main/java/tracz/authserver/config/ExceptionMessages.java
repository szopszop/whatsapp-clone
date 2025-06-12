package tracz.authserver.config;

public class ExceptionMessages {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_EXISTS = "Email already exists";
    public static final String INVALID_EMAIL = "Invalid email address";
    public static final String BAD_CREDENTIALS = "Bad credentials";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String PASSWORD_MISMATCH = "Password does not match";
    public static final String PASSWORD_CONSTRAINT = "Password must be at least 8 characters long, "
            + "with 1 uppercase letter, with 1 lower case letter, with 1 special character";

    public static final String NOT_FOUND = "Not Found";
    public static final String CONFLICT = "Conflict";
    public static final String ACCESS_DENIED = "Access denied";

    public static final String VALIDATION_FAILED = "Validation Failed";
    public static final String UNAUTHORIZED_MESSAGE = "Authentication required to access this resource.";
    public static final String FORBIDDEN_MESSAGE = "You do not have permission to access this resource.";
    public static final String INTERNAL_ERROR = "An unexpected internal server error occurred.";
    public static final String LIMIT_EXCEEDED = "Rate limit exceeded. Please try again later.";
}
