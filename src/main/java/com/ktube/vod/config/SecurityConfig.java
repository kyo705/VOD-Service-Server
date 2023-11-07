package com.ktube.vod.config;

import com.ktube.vod.security.authority.JsonAccessDeniedHandler;
import com.ktube.vod.security.authority.JsonHttp403ForbiddenEntryPoint;
import com.ktube.vod.security.login.JsonLoginConfigurer;
import com.ktube.vod.security.login.JsonLoginFailureHandler;
import com.ktube.vod.security.login.JsonLoginSuccessHandler;
import com.ktube.vod.security.logout.JsonLogoutSuccessHandler;
import com.ktube.vod.user.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static com.ktube.vod.security.SecurityConstants.LOGIN_URL;
import static com.ktube.vod.security.SecurityConstants.LOGOUT_URL;
import static com.ktube.vod.user.UserConstants.USER_URL;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //엔드 포인트 권한 설정
        http.authorizeHttpRequests()
                .antMatchers(POST, USER_URL).hasAnyAuthority("ROLE_ANONYMOUS")
                .antMatchers(GET, USER_URL).hasAnyAuthority(UserRole.GENERAL.name(), UserRole.PREMIUM.name());

        // 인가 예외 처리 설정
        http.exceptionHandling()
                .accessDeniedHandler(new JsonAccessDeniedHandler())
                .authenticationEntryPoint(new JsonHttp403ForbiddenEntryPoint());

        //로그인 설정
        http.apply(new JsonLoginConfigurer<>())
                .loginProcessingUrl(LOGIN_URL)
                .successHandler(new JsonLoginSuccessHandler())
                .failureHandler(new JsonLoginFailureHandler());

        //로그아웃 설정
        http.logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessHandler(new JsonLogoutSuccessHandler());

        http.csrf().disable();

        return http.build();
    }
}
