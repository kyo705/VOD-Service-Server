package com.ktube.vod.security.login;

import org.springframework.security.core.AuthenticationException;

public class NotYetAuthorizedException extends AuthenticationException {

    public NotYetAuthorizedException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NotYetAuthorizedException(String msg) {
        super(msg);
    }
}
