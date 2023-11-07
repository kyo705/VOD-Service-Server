package com.ktube.vod.security.login;

import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class JsonLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, JsonLoginConfigurer<H>, JsonLoginAuthenticationFilter> {

    public JsonLoginConfigurer() {
        super(new JsonLoginAuthenticationFilter(), null);
    }

    public JsonLoginConfigurer(String url) {
        super(new JsonLoginAuthenticationFilter(), url);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}