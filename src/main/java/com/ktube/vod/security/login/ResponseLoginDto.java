package com.ktube.vod.security.login;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseLoginDto {

    private int code;
    private String message;
}
