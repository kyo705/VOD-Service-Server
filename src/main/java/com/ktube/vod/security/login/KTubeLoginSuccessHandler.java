package com.ktube.vod.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.notification.NotificationService;
import com.ktube.vod.user.basic.UserSecurityLevel;
import com.ktube.vod.user.log.RequestUserLogCreateDto;
import com.ktube.vod.user.log.UserConnectType;
import com.ktube.vod.user.log.UserLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ktube.vod.notification.NotificationConstants.LOGIN_ALARM_MESSAGE;
import static com.ktube.vod.notification.NotificationConstants.LOGIN_ALARM_SUBJECT;


@Slf4j
@Component
@RequiredArgsConstructor
public class KTubeLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Qualifier("emailNotificationService")
    private final NotificationService notificationService;
    private final UserLogService userLogService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        KTubeUserDetails userDetails = (KTubeUserDetails) authentication.getPrincipal();
        if(userDetails.getSecurityLevel().getCode() >= UserSecurityLevel.ALARM.getCode()) {
            String loginMessage = String.format(LOGIN_ALARM_MESSAGE, request.getRemoteAddr());

            notificationService.send(userDetails.getEmail(), LOGIN_ALARM_SUBJECT, loginMessage);
        }
        createUserLoginLog(userDetails.getUserId(), userDetails.getConnectIp(), userDetails.getConnectDevice());

        writeResponse(response, HttpStatus.OK.value(), "로그인 성공");
    }

    private void createUserLoginLog(long userId, String connectIp, String connectDevice) {

        RequestUserLogCreateDto param = new RequestUserLogCreateDto();
        param.setUserId(userId);
        param.setConnectIp(connectIp);
        param.setConnectDevice(connectDevice);
        param.setConnectType(UserConnectType.LOGIN);

        userLogService.create(param);
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
