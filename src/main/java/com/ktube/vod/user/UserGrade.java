package com.ktube.vod.user;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum UserGrade {

    TEMPORARY(0),
    GENERAL(1),
    PREMIUM(2);

    private static final Map<Integer, UserGrade> USER_ROLE_MAP;
    private final int code;

    static {
        USER_ROLE_MAP = new HashMap<>();

        Arrays.stream(UserGrade.values())
                .forEach(userRole -> USER_ROLE_MAP.put(userRole.getCode(), userRole));
    }

    UserGrade(int code) {
        this.code = code;
    }

    public static UserGrade valueOfCode(int code) {

        return USER_ROLE_MAP.get(code);
    }
}
