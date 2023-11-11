package com.ktube.vod.user;

import com.ktube.vod.identification.IdentificationFailureException;
import com.ktube.vod.identification.IdentificationService;
import com.ktube.vod.notification.NotificationFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import static com.ktube.vod.user.UserConstants.ALREADY_EXISTING_EMAIL_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private UserService userService;
    @Mock
    private IdentificationService identificationService;
    @Mock
    private UserRepository userRepository;


    @BeforeEach
    public void setup() {
        userService = new UserService(identificationService, userRepository);
    }

    @DisplayName("유효한 파라미터로 회원가입 요청시 회원가입이 성공한다.")
    @Test
    public void testJoinWithValidRequestParam() {

        //given
        RequestUserJoinDto request = new RequestUserJoinDto();
        request.setEmail("email@naver.com");
        request.setPassword("1234567890!a");
        request.setNickname("닉네임");

        given(userRepository.findByEmail(any())).willReturn(null);

        //when
        KTubeUser resultKTubeUser = userService.join(request);

        //then
        assertThat(resultKTubeUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(PasswordEncoderUtils.match(request.getPassword(), resultKTubeUser.getPassword())).isTrue();
        assertThat(resultKTubeUser.getNickname()).isEqualTo(request.getNickname());
        assertThat(resultKTubeUser.getRole()).isEqualTo(UserRole.TEMPORARY);
    }

    @DisplayName("이미 존재하는 이메일로 회원가입 요청시 예외가 발생한다.")
    @Test
    public void testJoinWithAlreadyExistingEmail() {

        //given
        RequestUserJoinDto request = new RequestUserJoinDto();
        request.setEmail("alreadyExistingEmail@naver.com");
        request.setPassword("1234567890!a");
        request.setNickname("닉네임");

        given(userRepository.findByEmail(any()))
                .willReturn(KTubeUser.init(request.getEmail(), request.getPassword(), request.getNickname()));

        // when & then
        Exception result = assertThrows(Exception.class, () -> userService.join(request));

        assertThat(result.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(result.getMessage()).isEqualTo(ALREADY_EXISTING_EMAIL_MESSAGE);
    }

    @DisplayName("알림 서버의 오류가 발생한 경우 회원가입 요청시 예외가 발생한다.")
    @Test
    public void testJoinWithNotificationFailureException() {

        //given
        RequestUserJoinDto request = new RequestUserJoinDto();
        request.setEmail("alreadyExistingEmail@naver.com");
        request.setPassword("1234567890!a");
        request.setNickname("닉네임");

        given(userRepository.findByEmail(any())).willReturn(null);
        given(identificationService.createIdentification(any())).willThrow(NotificationFailureException.class);

        // when & then
        assertThrows(NotificationFailureException.class, () -> userService.join(request));
    }

    @DisplayName("올바른 인증 번호로 인증 요청 시 해당 유저 정보가 저장된다.")
    @Test
    public void testIdentifyWithValidParam() {

        //given
        String identificationCode = "IDCODE";

        //when
        userService.identifyUser(identificationCode);

        //then
        verify(userRepository, times(1)).create(any());
    }

    @DisplayName("잘못된 인증 번호로 인증 요청 시 해당 예외가 발생한다.")
    @Test
    public void testIdentifyWithInvalidParam() {

        //given
        String identificationCode = "IDCODE";
        given(identificationService.identify(any())).willThrow(IdentificationFailureException.class);

        //when & then
        assertThrows(IdentificationFailureException.class, () -> userService.identifyUser(identificationCode));

        verify(userRepository, times(0)).create(any());
    }

    @DisplayName("이미 인증된 유저에 대해 다시 인증 요청 시 예외가 발생한다.")
    @Test
    public void testIdentifyWithAlreadyIdentifiedUser() {

        //given
        String identificationCode = "IDCODE";
        willThrow(DataIntegrityViolationException.class).given(userRepository).create(any());

        //when & then
        assertThrows(DataIntegrityViolationException.class, () -> userService.identifyUser(identificationCode));

        verify(userRepository, times(1)).create(any());
    }
}
