package com.ktube.vod.user.log;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUserLogCreateDto {

    private long userId;
    private String connectIp;
    private String connectDevice;
    private UserConnectType connectType;
}
