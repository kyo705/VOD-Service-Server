package com.ktube.vod.user.log;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum UserConnectType {

    LOGIN(0),
    LOGOUT(1);

    private static final Map<Integer, UserConnectType> CONNECT_TYPE_MAP;
    private final int code;

    static {
        CONNECT_TYPE_MAP = new HashMap<>();

        Arrays.stream(UserConnectType.values())
                .forEach(connectType -> CONNECT_TYPE_MAP.put(connectType.getCode(), connectType));
    }

    UserConnectType(int code) {
        this.code = code;
    }

    public static UserConnectType valueOfCode(int code) {

        return CONNECT_TYPE_MAP.get(code);
    }
}
