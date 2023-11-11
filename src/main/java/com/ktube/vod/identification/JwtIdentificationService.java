package com.ktube.vod.identification;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.notification.NotificationFailureException;
import com.ktube.vod.notification.NotificationService;
import com.ktube.vod.user.KTubeUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.ktube.vod.identification.IdentificationConstants.*;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("jwtIdentificationService")
public class JwtIdentificationService implements IdentificationService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Qualifier("emailNotificationService")
    private final NotificationService notificationService;

    @Override
    public String createIdentification(KTubeUser user) throws NotificationFailureException {

        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requireNonNull(attributes).getResponse();

        // 인증 번호 생성
        String identificationCode = IdentificationUtils.createIdentificationCode();

        // 인증 번호를 secret key로 jwt 생성
        try {
            Map<String, String> claims = new HashMap<>();
            claims.put(JWT_IDENTIFICATION_USER_CLAIM, objectMapper.writeValueAsString(user));

            String jwt = JwtUtils.create(identificationCode, JWT_IDENTIFICATION_SUBJECT,
                            JWT_IDENTIFICATION_EXPIRATION_MS, claims);

            // jwt를 response 해더에 추가
            requireNonNull(response).setHeader(HttpHeaders.AUTHORIZATION, jwt);

            // 인증번호로 이메일 메세지 생성 후 이메일 전송
            notificationService.send(user.getEmail(), identificationCode);

            return jwt;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public KTubeUser identify(String identificationCode) throws IdentificationFailureException {

        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requireNonNull(attributes).getRequest();

        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(jwt == null) {
            throw new IdentificationFailureException("인증 시도를 위한 권한이 없습니다.");
        }
        try {
            DecodedJWT decodedJWT = JwtUtils.verify(jwt, identificationCode);

            String subject = JwtUtils.getSubject(decodedJWT);
            if(!subject.equals(JWT_IDENTIFICATION_SUBJECT)) {
                throw new IdentificationFailureException("유효하지 않은 인증 토큰입니다.");
            }
            String user = JwtUtils.getClaim(decodedJWT, JWT_IDENTIFICATION_USER_CLAIM);

            return objectMapper.readValue(user, KTubeUser.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (JWTVerificationException e) {
            throw new IdentificationFailureException(e.getMessage());
        }
    }
}