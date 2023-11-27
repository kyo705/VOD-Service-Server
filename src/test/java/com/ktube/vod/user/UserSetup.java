package com.ktube.vod.user;

import com.ktube.vod.user.log.UserConnectType;
import com.ktube.vod.user.log.UserLog;
import org.junit.jupiter.params.provider.Arguments;

import java.util.List;
import java.util.stream.Stream;

public class UserSetup {

    public static Stream<Arguments> getInvalidUserUpdateParam() {

        return Stream.of(
          Arguments.of("1", "validNickname", 0, 0),
                Arguments.of("1234567890a!", "!invalidNickname", 0, 0),
                Arguments.of("1234567890a!", "validNickname", 5, 0),
                Arguments.of("1234567890a!", "validNickname", 0, 5)
        );
    }

    public static Stream<Arguments> getValidUserUpdateParam() {

        return Stream.of(
                Arguments.of("1234567890a!", "validNickname", 0, 0)
        );
    }

    public static Stream<Arguments> getInvalidParamWithFindUserLogs() {
        // offset, size
        return Stream.of(
                Arguments.of(-1, 5),
                Arguments.of(0, -5),
                Arguments.of(-1, -5)
        );
    }

    public static Stream<Arguments> getInvalidParamWithFindUserConnectLogs() {

        // userId, offset, size
        return Stream.of(
                Arguments.of(-3, 0, 5),
                Arguments.of(1, -1, 5),
                Arguments.of(1, 0, -5),
                Arguments.of(1, -1, -5)
        );
    }

    public static List<UserLog> getUserLogs(long userId) {

        return List.of(
          UserLog.init(userId, "127.0.0.1", "device1", UserConnectType.LOGIN)
                ,UserLog.init(userId, "59.2.1.5", "device2", UserConnectType.LOGIN)
                ,UserLog.init(userId, "131.52.1.33", "device3", UserConnectType.LOGIN)
                ,UserLog.init(userId, "59.2.1.5", "device2", UserConnectType.LOGOUT)
                ,UserLog.init(userId, "127.0.0.1", "device1", UserConnectType.LOGOUT)

        );
    }
}
