package com.ktube.vod.user.basic;

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

        UserSecurityLevel securityLevel = USER_SECURITY_LEVEL_MAP.get(code);
        if(securityLevel == null) {
            throw new IllegalArgumentException("존재하지 않는 유저 보안 레벨입니다.");
        }
        return securityLevel;
    }
}
