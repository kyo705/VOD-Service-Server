package com.ktube.vod.identification;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.notification.NotificationFailureException;
import com.ktube.vod.notification.NotificationService;
import com.ktube.vod.user.KTubeUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.ktube.vod.identification.IdentificationConstants.JWT_IDENTIFICATION_SUBJECT;
import static com.ktube.vod.identification.IdentificationConstants.JWT_IDENTIFICATION_USER_CLAIM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.willThrow;

@ExtendWith(MockitoExtension.class)
public class JwtIdentificationServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private JwtIdentificationService jwtIdentificationService;
    @Mock
    private NotificationService mockNotificationService;

    @BeforeEach
    public void setup() {
        jwtIdentificationService = new JwtIdentificationService(mockNotificationService);
    }

    @DisplayName("본인 인증 사전 작업시 인증 번호 알림이 전송되고 jwt가 생성된 후 header에 저장된다.")
    @Test
    public void testCreateIdentificationWithSuccess() {

        //given
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        KTubeUser user = KTubeUser.init("email@naver.com", "password1#", "닉네임");

        //when
        String resultJwt = jwtIdentificationService.createIdentification(user.getEmail(), user);

        //then
        String expectedJwt = response.getHeader(HttpHeaders.AUTHORIZATION);

        assertThat(resultJwt).isEqualTo(expectedJwt);
    }

    @DisplayName("본인 인증 사전 작업시 인증 번호 알림 전송이 실패할 경우 예외가 발생한다.")
    @Test
    public void testCreateIdentificationWithNotificationFailureException() {

        //given
        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        KTubeUser user = KTubeUser.init("email@naver.com", "password1#", "닉네임");

        willThrow(NotificationFailureException.class)
                .given(mockNotificationService).send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        //when & then
        assertThrows(NotificationFailureException.class, () -> jwtIdentificationService.createIdentification(user.getEmail(), user));
    }

    @DisplayName("올바른 인증 번호로 인증을 시도할 경우 해당 유저 데이터가 리턴된다.")
    @Test
    public void testIdentifyWithSuccess() throws JsonProcessingException {

        //given
        String identificationCode = "SECRET";
        KTubeUser user = KTubeUser.init("email@naver.com", "password1#", "닉네임");

        Map<String, String> claims = new HashMap<>();
        claims.put(JWT_IDENTIFICATION_USER_CLAIM, objectMapper.writeValueAsString(user));
        String jwt = JwtUtils.create(identificationCode, JWT_IDENTIFICATION_SUBJECT, 10000L, claims);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, jwt);

        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        //when
        String userData = (String) jwtIdentificationService.identify(identificationCode);
        KTubeUser resultUser = objectMapper.readValue(userData, KTubeUser.class);

        //then
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(resultUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(resultUser.getGrade()).isEqualTo(user.getGrade());
        assertThat(resultUser.getPassword()).isEqualTo(user.getPassword());
    }

    @DisplayName("잘못된 인증번호로 인증을 시도할 경우 예외가 발생한다.")
    @Test
    public void testIdentifyWithInvalidIdentificationCode() throws JsonProcessingException {

        //given
        String identificationCode = "SECRET";
        KTubeUser user = KTubeUser.init("email@naver.com", "password1#", "닉네임");

        Map<String, String> claims = new HashMap<>();
        claims.put(JWT_IDENTIFICATION_USER_CLAIM, objectMapper.writeValueAsString(user));
        String jwt = JwtUtils.create(identificationCode, JWT_IDENTIFICATION_SUBJECT, 10000L, claims);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, jwt);

        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        //when & then
        assertThrows(IdentificationFailureException.class, ()-> jwtIdentificationService.identify("invalidIdentificationCode"));
    }

    @DisplayName("기간 만료된 jwt로 인증을 시도할 경우 예외가 발생한다.")
    @Test
    public void testIdentifyWithExpiredJWT() throws JsonProcessingException, InterruptedException {

        //given
        String identificationCode = "SECRET";
        KTubeUser user = KTubeUser.init("email@naver.com", "password1#", "닉네임");

        Map<String, String> claims = new HashMap<>();
        claims.put(JWT_IDENTIFICATION_USER_CLAIM, objectMapper.writeValueAsString(user));
        String jwt = JwtUtils.create(identificationCode, JWT_IDENTIFICATION_SUBJECT, 100L, claims);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, jwt);

        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        //when & then
        Thread.sleep(200L);
        assertThrows(IdentificationFailureException.class, ()-> jwtIdentificationService.identify(identificationCode));
    }

    @DisplayName("본인 인증 jwt가 아닌 토큰으로 인증을 시도할 경우 예외가 발생한다.")
    @Test
    public void testIdentifyWithNotIdentificationJWT() throws JsonProcessingException {

        //given
        String identificationCode = "SECRET";
        KTubeUser user = KTubeUser.init("email@naver.com", "password1#", "닉네임");

        Map<String, String> claims = new HashMap<>();
        claims.put(JWT_IDENTIFICATION_USER_CLAIM, objectMapper.writeValueAsString(user));
        String jwt = JwtUtils.create(identificationCode, "NOT_IDENTIFICATION_SUBJECT", 10000L, claims);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, jwt);

        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        //when & then
        assertThrows(IdentificationFailureException.class, ()-> jwtIdentificationService.identify(identificationCode));
    }

    @DisplayName("요청 헤더에 본인 인증 jwt가 없을 경우 예외가 발생한다.")
    @Test
    public void testIdentifyWithNotExistingJWT() {

        //given
        String identificationCode = "SECRET";
        MockHttpServletRequest request = new MockHttpServletRequest();

        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        //when & then
        assertThrows(IdentificationFailureException.class, ()-> jwtIdentificationService.identify(identificationCode));
    }

    @DisplayName("유효하지 않은 jwt로 인증을 시도할 경우 예외가 발생한다.")
    @Test
    public void testIdentifyWithNotExistingUserData() {

        //given
        String identificationCode = "SECRET";

        Map<String, String> claims = new HashMap<>();
        String jwt = JwtUtils.create(identificationCode, JWT_IDENTIFICATION_SUBJECT, 100L, claims);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, jwt);

        HttpServletResponse response = new MockHttpServletResponse();
        RequestAttributes requestAttributes = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        //when & then
        assertThrows(IdentificationFailureException.class, ()-> jwtIdentificationService.identify(identificationCode));
    }
}
