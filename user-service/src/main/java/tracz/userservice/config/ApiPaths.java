package tracz.userservice.config;

public final class ApiPaths {
    public static final String USER_API = "/api/v1/user";
    public static final String USER_BY_ID = USER_API + "/{id}";
    public static final String USER_BY_EMAIL = USER_API + "/by-email";
    public static final String USER_EXISTS_BY_EMAIL = USER_API + "/exists-by-email";
}
