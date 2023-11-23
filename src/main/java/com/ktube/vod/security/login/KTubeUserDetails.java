package com.ktube.vod.security.login;

import com.ktube.vod.user.KTubeUser;
import com.ktube.vod.user.UserDevice;
import com.ktube.vod.user.UserSecurityLevel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class KTubeUserDetails implements UserDetails {

    private final String email;
    private final String password;
    @Getter
    private final String nickname;
    private final List<GrantedAuthority> authorities;
    @Getter
    private final UserSecurityLevel securityLevel;
    @Getter
    private final Set<String> devices;
    public KTubeUserDetails(KTubeUser user) {

        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.securityLevel = user.getSecurityLevel();

        authorities = new ArrayList<>();
        authorities.add(()-> user.getRole().name());

        devices = new HashSet<>();
        user.getDevices()
                .stream()
                .map(UserDevice::getDeviceInfo)
                .forEach(devices::add);
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
