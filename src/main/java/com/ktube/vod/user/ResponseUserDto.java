package com.ktube.vod.user;

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
    private UserRole userRole;

    public ResponseUserDto(KTubeUser user) {

        id = user.getId();
        email = user.getEmail();
        nickname = user.getNickname();
        userRole = user.getRole();
    }
}
