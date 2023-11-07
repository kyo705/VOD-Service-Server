package com.ktube.vod.error;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseErrorDto {

    private int errorCode;
    private String errorMessage;
}
