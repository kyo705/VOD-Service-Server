package com.ktube.vod.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderUtils {

    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    public static String encodePassword(String password) {

        return PASSWORD_ENCODER.encode(password);
    }

    public static boolean match(String rawPassword, String encodedPassword) {

        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
