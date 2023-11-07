package com.ktube.vod.security.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestLoginDto {

    private String email;
    private String password;
}
