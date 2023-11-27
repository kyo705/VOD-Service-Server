package com.ktube.vod.user;

import org.junit.jupiter.params.provider.Arguments;

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
}
