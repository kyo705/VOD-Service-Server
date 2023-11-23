package com.ktube.vod.security.login;

import org.springframework.security.core.AuthenticationException;

public class NotAllowedDeviceException extends AuthenticationException {

    public NotAllowedDeviceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NotAllowedDeviceException(String msg) {
        super(msg);
    }
}
