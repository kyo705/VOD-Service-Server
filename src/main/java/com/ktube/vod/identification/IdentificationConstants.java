package com.ktube.vod.identification;

public class IdentificationConstants {

    public static final String JWT_IDENTIFICATION_SUBJECT = "identification";
    public static final String JWT_IDENTIFICATION_USER_CLAIM = "user_claim";
    public static final long JWT_IDENTIFICATION_EXPIRATION_MS = 1000*60*5;
    public static final String IDENTIFICATION_LOGIN_URL = "/api/identification/login";
    public static final String IDENTIFICATION_JOIN_URL = "/api/identification/join";
}
