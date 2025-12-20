package com.hpms.gateway.utils;

import org.springframework.beans.factory.annotation.Value;

public class AppConstants {
    public static final String AUTH_HEADER = "Authentication";
    public static final int ACCESS_EXPATRIATION_TIME = 24 * 60 * 60 * 1000;
    public static final int REFRESH_EXPATRIATION_TIME = 7 * ACCESS_EXPATRIATION_TIME;

    @Value("${jwt.secret}")
    public static final String JWT_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    public static final String CLIENT_URL = "http://localhost:4200";

    public static final String EMAIL_CONFORMATION_PREFIX = CLIENT_URL + "/auth/registration/after-verification/";

    public static final String RECRUITER_FORM_PREFIX = CLIENT_URL + "/auth/registration/recruiter/";

    public static final String RESET_PASSWORD_CLIENT_LINK = CLIENT_URL + "/auth/change-password/reset/";

    public static final String LOCAL_PROVIDER_ID = "412429";

    public static final String HIRE_X_HIRE_EMAIL = "hirexhire@gmail.com";

}
