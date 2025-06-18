package tracz.authserver.config;

public class ExceptionMessages {
    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_EXISTS = "User already exists";
    public static final String BAD_CREDENTIALS = "Bad credentials";
    public static final String INVALID_TOKEN = "Invalid token";

    public static final String VALIDATION_FAILED = "Validation Failed";
    public static final String UNAUTHORIZED_MESSAGE = "Authentication required to access this resource.";
    public static final String FORBIDDEN_MESSAGE = "You do not have permission to access this resource.";
    public static final String INTERNAL_ERROR = "An unexpected internal server error occurred.";
    public static final String LIMIT_EXCEEDED = "Rate limit exceeded. Please try again later.";
}
