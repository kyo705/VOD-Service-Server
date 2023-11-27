package com.ktube.vod.config;

import com.ktube.vod.identification.IdentificationService;
import com.ktube.vod.security.authority.JsonAccessDeniedHandler;
import com.ktube.vod.security.authority.JsonHttp403ForbiddenEntryPoint;
import com.ktube.vod.security.login.KTubeLoginConfigurer;
import com.ktube.vod.user.basic.UserGrade;
import com.ktube.vod.user.log.UserLogService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static com.ktube.vod.identification.IdentificationConstants.IDENTIFICATION_JOIN_URL;
import static com.ktube.vod.identification.IdentificationConstants.IDENTIFICATION_LOGIN_URL;
import static com.ktube.vod.security.SecurityConstants.LOGIN_URL;
import static com.ktube.vod.security.SecurityConstants.LOGOUT_URL;
import static com.ktube.vod.user.basic.UserConstants.*;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
public class SecurityConfig {


    @Value("${ktube.url.front}")
    private String frontEndServerUrl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationSuccessHandler loginSuccessHandler,
                                           AuthenticationFailureHandler loginFailureHandler,
                                           LogoutSuccessHandler logoutSuccessHandler,
                                           @Qualifier("jwtIdentificationService")
                                           IdentificationService identificationService,
                                           UserLogService userLogService
    ) throws Exception {


        //엔드 포인트 권한 설정
        http.authorizeHttpRequests()
                .antMatchers(POST, USER_URL).hasAnyAuthority("ROLE_ANONYMOUS")
                .antMatchers(GET, IDENTIFICATION_JOIN_URL, IDENTIFICATION_LOGIN_URL).hasAnyAuthority("ROLE_ANONYMOUS")
                .antMatchers(GET, USER_URL, USER_CONNECT_LOG_URL).hasAnyAuthority(UserGrade.TEMPORARY.name(), UserGrade.GENERAL.name(), UserGrade.PREMIUM.name())
                .antMatchers(SPECIFIC_USER_URL).hasAnyAuthority(UserGrade.TEMPORARY.name(), UserGrade.GENERAL.name(), UserGrade.PREMIUM.name())

        ;

        // 인가 예외 처리 설정
        http.exceptionHandling()
                .accessDeniedHandler(new JsonAccessDeniedHandler())
                .authenticationEntryPoint(new JsonHttp403ForbiddenEntryPoint());

        //로그인 설정
        http.apply(new KTubeLoginConfigurer<>(identificationService, userLogService))
                .loginProcessingUrl(LOGIN_URL)
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler);

        //로그아웃 설정
        http.logout()
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessHandler(logoutSuccessHandler);

        // cors 정책
        http.cors();

        http.csrf().disable();

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(frontEndServerUrl));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
