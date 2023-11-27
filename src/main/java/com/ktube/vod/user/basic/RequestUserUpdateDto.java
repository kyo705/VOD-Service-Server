package com.ktube.vod.user.basic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserUpdateDto {

    @Password(message = "비밀 번호는 11자 이상 30자 이하, 특수 문자, 문자, 숫자가 적어도 하나 이상 포함 되어야 한다.")
    private String password;

    @Nickname(message = "닉네임은 최소 1자 이상 최대 20자 이하, 특수 문자가 포함 되면 안된다.")
    private String nickname;

    private UserSecurityLevel securityLevel;

    private UserGrade grade;
}
