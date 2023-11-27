package com.ktube.vod.user.log;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@Table(name = "user_log", indexes = @Index(name = "user_log_idx_user_id", columnList = "userId"))
public class UserLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private long userId;

    @NotNull
    private String connectIp;

    @NotNull
    private String connectDevice;

    @Convert(converter = UserConnectTypeDatabaseConverter.class)
    @NotNull
    private UserConnectType connectType;

    @NotNull
    private long connectTimestamp;

    public static UserLog init(long userId, String connectIp,
                               String connectDevice, UserConnectType connectType) {

        UserLog userLog = new UserLog();
        userLog.setUserId(userId);
        userLog.setConnectIp(connectIp);
        userLog.setConnectDevice(connectDevice);
        userLog.setConnectType(connectType);
        userLog.setConnectTimestamp(System.currentTimeMillis());

        return userLog;
    }
}
