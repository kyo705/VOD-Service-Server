package com.ktube.vod.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "ktube_user", indexes = @Index(unique = true, name = "ktube_user_idx_email", columnList = "email"))
public class KTubeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String nickname;

    @Convert(converter = UserGradeDatabaseConverter.class)
    @NotNull
    private UserGrade grade;

    @Convert(converter = UserSecurityLevelDatabaseConverter.class)
    @NotNull
    private UserSecurityLevel securityLevel;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserDevice> devices = new ArrayList<>();

    public static KTubeUser init(String email, String password, String nickname) {

        KTubeUser KTubeUser = new KTubeUser();
        KTubeUser.setEmail(email);
        KTubeUser.setPassword(password);
        KTubeUser.setNickname(nickname);
        KTubeUser.setGrade(UserGrade.GENERAL);
        KTubeUser.setSecurityLevel(UserSecurityLevel.GENERAL);

        return KTubeUser;
    }

    public void registerClientDeviceInfo(String clientDeviceInfo) {

        UserDevice userDevice = UserDevice.init(this, clientDeviceInfo);
        devices.add(userDevice);
    }
}
