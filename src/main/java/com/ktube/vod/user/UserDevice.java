package com.ktube.vod.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String deviceInfo;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private KTubeUser user;

    static UserDevice init(KTubeUser user, String deviceInfo) {

        UserDevice userDevice = new UserDevice();
        userDevice.setDeviceInfo(deviceInfo);
        userDevice.setUser(user);

        return userDevice;
    }
}
