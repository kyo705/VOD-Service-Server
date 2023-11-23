package com.ktube.vod.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JsonLoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {

        String failureMessage = exception.getMessage();
        log.error(failureMessage);

        if(exception instanceof NotYetAuthorizedException) {
            writeResponse(response, HttpStatus.FORBIDDEN.value(), failureMessage);
            return;
        }
        if(exception instanceof NotAllowedDeviceException) {
            writeResponse(response, HttpStatus.UNAUTHORIZED.value(), failureMessage);
            return;
        }
        writeResponse(response, HttpStatus.BAD_REQUEST.value(), failureMessage);
    }

    private void writeResponse(HttpServletResponse response, int statusCode, String message) throws IOException {

        ResponseLoginDto responseBody = new ResponseLoginDto();
        responseBody.setCode(statusCode);
        responseBody.setMessage(message);

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }
}
