package com.ktube.vod.security.login;

import com.ktube.vod.user.KTubeUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KTubeUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private final String nickname;
    private final List<GrantedAuthority> authorities;
    public KTubeUserDetails(KTubeUser user) {

        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();

        authorities = new ArrayList<>();
        authorities.add(()-> user.getRole().name());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
