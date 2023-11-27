package com.ktube.vod.user.basic;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserDto {
    private Long id;
    private String email;
    private String nickname;
    private UserGrade userGrade;

    public ResponseUserDto(KTubeUser user) {

        id = user.getId();
        email = user.getEmail();
        nickname = user.getNickname();
        userGrade = user.getGrade();
    }
}
