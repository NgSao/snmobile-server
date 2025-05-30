package com.snd.server.constant;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

public class SecurityConstant {
        // 1 day (seconds = 24 * 60 * 60)
        public static final long EXPIRATION_TIME = (24 * 60 * 60) * 2;
        public static final long REFRESH_TOKEN_EXP = (24 * 60 * 60) * 12;

        public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS512;

        public static long EXPIRATION_OTP = 3 * 60; // 3 phút (3 phút * 60 giây)

        public static final String INVALID_TOKEN = "Invalid token (expired, malformed, or JWT not provided in header).";
        public static final String ACCESS_DENIED = "You do not have permission to access this resource.";
        public static final String TOKEN_REVOKED = "The token has been revoked.";

        public static final String LOGIN = "/api/v1/public/auth/login";

        public static final String[] PUBLIC_URLS = {
                        "/api/v1/public/**",
                        "/v3/api-docs/**", "/swagger-ui/**",
                        "/swagger-ui.html" };

        public static final String[] ADMIN_URLS = { "/api/v1/**" };
        public static final String[] QUAN_LY_CUA_HANG_URLS = {
                        "/api/v1/store-management/**"
        };

        public static final String[] NHAN_VIEN_BAN_HANG_URLS = {
                        "/api/v1/sales/**"
        };

        public static final String[] CHAM_SOC_KHACH_HANG_URLS = {
                        "/api/v1/customer-care/**"
        };

        public static final String[] KHO_URLS = {
                        "/api/v1/warehouse/**"
        };

        public static final String[] KHACH_HANG_URLS = {
                        "/api/v1/customer/**",
                        "/api/v1/orders/**",
                        "/api/v1/address/**"
        };

}
