package com.ktube.vod.user;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum UserSecurityLevel {

    GENERAL(0),
    ALARM(1),
    IDENTITY(2);

    private static final Map<Integer, UserSecurityLevel> USER_SECURITY_LEVEL_MAP;
    private final int code;

    static {
        USER_SECURITY_LEVEL_MAP = new HashMap<>();

        Arrays.stream(UserSecurityLevel.values())
                .forEach(userRole -> USER_SECURITY_LEVEL_MAP.put(userRole.getCode(), userRole));
    }

    UserSecurityLevel(int code) {
        this.code = code;
    }

    public static UserSecurityLevel valueOfCode(int code) {

        return USER_SECURITY_LEVEL_MAP.get(code);
    }
}
