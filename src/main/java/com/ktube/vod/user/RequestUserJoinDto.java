package com.ktube.vod.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class RequestUserJoinDto {

    @Email @NotEmpty
    private String email;
    @Password @NotEmpty
    private String password;
    @Nickname @NotEmpty
    private String nickname;
}
