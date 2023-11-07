package com.ktube.vod.security.authority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.error.ResponseErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JsonHttp403ForbiddenEntryPoint extends Http403ForbiddenEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {

        log.debug("해당 클라이언트 : {} 는 접근 권한이 없음", request.getRemoteAddr());

        ResponseErrorDto responseErrorDto = ResponseErrorDto.builder()
                .errorCode(HttpStatus.FORBIDDEN.value())
                .errorMessage(ex.getMessage())
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseErrorDto));
    }
}
