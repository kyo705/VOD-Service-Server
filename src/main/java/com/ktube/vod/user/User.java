package com.ktube.vod.user;

import com.ktube.vod.identification.AlreadyIdentifiedUserException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Setter
@Getter
@Entity
public class User {

    @Id @GeneratedValue
    private Long id;
    private String email;
    private String password;
    private String nickname;
    private UserRole role;

    public static User init(String email, String password, String nickname) {

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setNickname(nickname);
        user.setRole(UserRole.TEMPORARY);

        return user;
    }

    public void identified() {

        if(this.role != UserRole.TEMPORARY) {
            throw new AlreadyIdentifiedUserException(this.email);
        }
        this.role = UserRole.GENERAL;
    }
}
