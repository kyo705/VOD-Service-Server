package com.ktube.vod.security.login;

import com.ktube.vod.identification.IdentificationService;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class KTubeLoginConfigurer<H extends HttpSecurityBuilder<H>> extends
        AbstractAuthenticationFilterConfigurer<H, KTubeLoginConfigurer<H>, KTubeLoginAuthenticationFilter> {

    public KTubeLoginConfigurer(IdentificationService identificationService) {
        super(new KTubeLoginAuthenticationFilter(identificationService), null);
    }

    public KTubeLoginConfigurer(IdentificationService identificationService, String url) {
        super(new KTubeLoginAuthenticationFilter(identificationService), url);
    }

    @Override
    protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}