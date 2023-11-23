package com.ktube.vod.user;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class RequestUserJoinDto {

    @Email
    @NotEmpty
    private String email;

    @Password(message = "비밀 번호는 11자 이상 30자 이하, 특수 문자, 문자, 숫자가 적어도 하나 이상 포함 되어야 한다.")
    @NotEmpty
    private String password;

    @Nickname(message = "닉네임은 최소 1자 이상 최대 20자 이하, 특수 문자가 포함 되면 안된다.")
    @NotEmpty
    private String nickname;
}
