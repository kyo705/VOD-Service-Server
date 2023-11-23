package com.ktube.vod.security.login;

import java.util.stream.Stream;

public class LoginSetup {

    static Stream<RequestLoginDto> getLoginDtoWithUnauthorizedUser() {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setEmail("email@naver.com");
        requestBody.setPassword("123456789!d");
        requestBody.setTemporary(false);
        requestBody.setClientDeviceInfo("Mozilla/1 AppleWebKit/2 Chrome/3 Safari/4");

        return Stream.of(requestBody);
    }

    static Stream<RequestLoginDto> getLoginDtoWithAuthorizedUser() {

        RequestLoginDto requestBody1 = new RequestLoginDto();
        requestBody1.setEmail("email1@naver.com");
        requestBody1.setPassword("123456789!d");
        requestBody1.setTemporary(false);
        requestBody1.setClientDeviceInfo("Mozilla/1 AppleWebKit/2 Chrome/3 Safari/4");

        RequestLoginDto requestBody2 = new RequestLoginDto();
        requestBody2.setEmail("email2@naver.com");
        requestBody2.setPassword("123456789!d");
        requestBody2.setTemporary(false);
        requestBody2.setClientDeviceInfo("Mozilla/1 AppleWebKit/2 Chrome/3 Safari/4");

        return Stream.of(requestBody1, requestBody2);
    }

    static Stream<RequestLoginDto> getLoginDtoWithUserRequiredToIdentity() {

        RequestLoginDto requestBody1 = new RequestLoginDto();
        requestBody1.setEmail("email1@naver.com");
        requestBody1.setPassword("123456789!d");
        requestBody1.setTemporary(true);
        requestBody1.setClientDeviceInfo("Mozilla/1 AppleWebKit/2 Chrome/3 Safari/4");

        RequestLoginDto requestBody2 = new RequestLoginDto();
        requestBody2.setEmail("email3@naver.com");
        requestBody2.setPassword("123456789!d");
        requestBody2.setTemporary(false);
        requestBody2.setClientDeviceInfo("Mozilla/1 AppleWebKit/2 Chrome/3 Safari/4");

        return Stream.of(requestBody1, requestBody2);
    }

    static Stream<RequestLoginDto> getLoginDtoWithNotExistingEmail() {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setEmail("notExistingEmail@naver.com");
        requestBody.setPassword("123456789!d");
        requestBody.setTemporary(false);
        requestBody.setClientDeviceInfo("Mozilla/1 AppleWebKit/2 Chrome/3 Safari/4");

        return Stream.of(requestBody);
    }

    static Stream<RequestLoginDto> getLoginDtoWithInvalidPassword() {

        RequestLoginDto requestBody = new RequestLoginDto();
        requestBody.setEmail("email@naver.com");
        requestBody.setPassword("invalidPassword");
        requestBody.setTemporary(false);
        requestBody.setClientDeviceInfo("Mozilla/1 AppleWebKit/2 Chrome/3 Safari/4");

        return Stream.of(requestBody);
    }
}
