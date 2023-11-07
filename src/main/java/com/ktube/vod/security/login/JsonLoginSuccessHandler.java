package com.ktube.vod.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.user.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JsonLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        KTubeUserDetails userDetails = (KTubeUserDetails) authentication.getPrincipal();
        for(GrantedAuthority authority : userDetails.getAuthorities()) {
            if(!authority.getAuthority().equals(UserRole.TEMPORARY.name())) {
                continue;
            }
            writeResponse(response, HttpStatus.UNAUTHORIZED.value(), "본인 인증되지 않은 유저입니다.");
            return;
        }
        writeResponse(response, HttpStatus.OK.value(), "로그인 성공");
    }

    private void writeResponse(HttpServletResponse response, int code, String message) throws IOException {

        ResponseLoginDto responseBody = new ResponseLoginDto();
        responseBody.setCode(code);
        responseBody.setMessage(message);

        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
