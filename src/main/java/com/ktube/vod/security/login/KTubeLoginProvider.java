package com.ktube.vod.security.login;

import com.ktube.vod.user.basic.PasswordEncoderUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KTubeLoginProvider implements AuthenticationProvider {

    private final KTubeLoginUserDetailService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String email = authentication.getName();
        String rawPassword = (String) authentication.getCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if(userDetails == null || !PasswordEncoderUtils.match(rawPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        if(!userDetails.isAccountNonExpired() || !userDetails.isCredentialsNonExpired()) {
            throw new AccountExpiredException("해당 계정은 만료되었습니다.");
        }
        if(!userDetails.isAccountNonLocked()) {
            throw new LockedException("해당 계정은 정지되었습니다.");
        }
        if(!userDetails.isEnabled()) {
            throw new DisabledException("해당 계정은 사용 불가입니다.");
        }
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
