package com.ktube.vod.user.session;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseUserSessionDto {

    private String sessionId;
    private String connectIp;
    private String connectDevice;
}
