package com.ktube.vod.user.log;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseUserLogDto {

    private long userId;
    private String connectIp;
    private String connectDevice;
    private UserConnectType connectType;
    private long connectTimestamp;

    public ResponseUserLogDto(UserLog userLog) {

        this.userId = userLog.getUserId();
        this.connectIp = userLog.getConnectIp();
        this.connectDevice = userLog.getConnectDevice();
        this.connectType = userLog.getConnectType();
        this.connectTimestamp = userLog.getConnectTimestamp();
    }
}
