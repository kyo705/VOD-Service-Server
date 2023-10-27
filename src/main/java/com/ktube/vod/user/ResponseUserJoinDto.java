package com.ktube.vod.user;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseUserJoinDto {
    private Long id;
    private String email;
    private String nickname;
    private UserRole userRole;
}
