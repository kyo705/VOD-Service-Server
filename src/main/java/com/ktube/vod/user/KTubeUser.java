package com.ktube.vod.user;

import com.ktube.vod.identification.AlreadyIdentifiedUserException;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@Entity
@Table(name = "ktube_user", indexes = @Index(unique = true, name = "ktube_user_idx_email", columnList = "email"))
public class KTubeUser {

    @Id @GeneratedValue
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String nickname;
    @Convert(converter = UserRoleConverter.class)
    @NotNull
    private UserRole role;

    public static KTubeUser init(String email, String password, String nickname) {

        KTubeUser KTubeUser = new KTubeUser();
        KTubeUser.setEmail(email);
        KTubeUser.setPassword(password);
        KTubeUser.setNickname(nickname);
        KTubeUser.setRole(UserRole.TEMPORARY);

        return KTubeUser;
    }

    public void identified() {

        if(this.role != UserRole.TEMPORARY) {
            throw new AlreadyIdentifiedUserException(this.email);
        }
        this.role = UserRole.GENERAL;
    }
}
