package com.snd.server.constant;

public class ApiPathConstant {
    public static final String API_PREFIX = "/api/v1";

    // Auth
    public static final String AUTH_REGISTER = "/public/auth/register";
    public static final String AUTH_VERIFY = "/public/auth/verify";
    public static final String AUTH_SEND_OTP = "/public/auth/send-otp";
    public static final String AUTH_FORGOT_PASSWORD = "/public/auth/forgot-password";
    public static final String AUTH_LOGIN = "/public/auth/login";

    // Auth
    public static final String LOGOUT = "/auth/logout";

    // Admin
    public static final String REFRESH = "/admin/auth/refresh";
    public static final String ADMIN_DASHBOARD = "/admin/dashboard";
    public static final String ADMIN_USER_LIST = "/admin/users";
    public static final String ADMIN_USER_DETAIL = "/admin/users/{id}";

    public static final String ADMIN_USER_UPDATE = "/admin/users/{id}/update";
    public static final String ADMIN_USER_DELETE = "/admin/users/{id}/delete";
    public static final String ADMIN_USER_BLOCK = "/admin/users/{id}/block";
    public static final String ADMIN_USER_UNBLOCK = "/admin/users/{id}/unblock";
    public static final String ADMIN_USER_SEARCH = "/admin/users/search";

    // User
    public static final String USER_PROFILE = "/user/profile";
    public static final String USER_UPDATE_PROFILE = "/user/update-profile";
    public static final String USER_CHANGE_PASSWORD = "/user/change-password";
    public static final String USER_UPDATE_AVATAR = "/user/update-avatar";

}
