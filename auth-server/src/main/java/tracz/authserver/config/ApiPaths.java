package tracz.authserver.config;

public final class ApiPaths {
    public static final String USER_API = "/api/v1/user";
    public static final String USER_BY_ID = "/{id}";
    public static final String USER_BY_EMAIL = "/by-email";
    public static final String USER_EXISTS_BY_EMAIL = "/exists-by-email";

    public static final String USER_API_BY_ID = USER_API + USER_BY_ID;
    public static final String USER_API_BY_EMAIL = USER_API + USER_BY_EMAIL;
    public static final String USER_API_EXISTS_BY_EMAIL = USER_API + USER_EXISTS_BY_EMAIL;
}