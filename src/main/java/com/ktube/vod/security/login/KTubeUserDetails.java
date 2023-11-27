package com.ktube.vod.security.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ktube.vod.user.basic.KTubeUser;
import com.ktube.vod.user.basic.UserDevice;
import com.ktube.vod.user.basic.UserSecurityLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class KTubeUserDetails implements UserDetails {

    private long userId;
    private String email;
    private String password;
    private String nickname;
    private List<KTubeGrantedAuthority> authorities;
    private UserSecurityLevel securityLevel;
    private Set<String> devices;

    public KTubeUserDetails(KTubeUser user) {

        this.userId = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.securityLevel = user.getSecurityLevel();

        authorities = new ArrayList<>();
        authorities.add(new KTubeGrantedAuthority(user.getGrade().name()));

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

    @JsonIgnore
    @Override
    public String getUsername() {
        return this.email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Getter
    @NoArgsConstructor
    static class KTubeGrantedAuthority implements GrantedAuthority {

        private String authority;

        public KTubeGrantedAuthority(String authority) {

            this.authority = authority;
        }

        @Override
        public String getAuthority() {
            return authority;
        }
    }
}
