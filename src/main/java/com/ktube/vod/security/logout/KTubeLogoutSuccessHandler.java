package com.ktube.vod.security.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.security.login.KTubeUserDetails;
import com.ktube.vod.security.login.ResponseLoginDto;
import com.ktube.vod.user.log.RequestUserLogCreateDto;
import com.ktube.vod.user.log.UserConnectType;
import com.ktube.vod.user.log.UserLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class KTubeLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserLogService userLogService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        if(authentication == null) {
            writeResponse(response, HttpStatus.BAD_REQUEST.value(), "세션 정보가 없습니다.");
            return;
        }
        KTubeUserDetails userDetails = (KTubeUserDetails) authentication.getPrincipal();
        RequestLogoutDto requestLogoutDto = parseRequestLogoutDto(request);
        createUserLogoutLog(userDetails.getUserId(), request.getRemoteAddr(), requestLogoutDto.getConnectDevice());

        writeResponse(response, HttpStatus.OK.value(), "로그아웃 성공");
    }

    private RequestLogoutDto parseRequestLogoutDto(HttpServletRequest request) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String str;

            while((str = br.readLine()) != null){
                stringBuilder.append(str);
            }
            return objectMapper.readValue(stringBuilder.toString(), RequestLogoutDto.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createUserLogoutLog(long userId, String connectIp, String connectDevice) {

        RequestUserLogCreateDto param = new RequestUserLogCreateDto();
        param.setUserId(userId);
        param.setConnectIp(connectIp);
        param.setConnectDevice(connectDevice);
        param.setConnectType(UserConnectType.LOGOUT);

        userLogService.create(param);
    }

    private void writeResponse(HttpServletResponse response, int code, String message) {

        ResponseLoginDto responseBody = new ResponseLoginDto();
        responseBody.setCode(code);
        responseBody.setMessage(message);

        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(objectMapper.writeValueAsString(responseBody));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
