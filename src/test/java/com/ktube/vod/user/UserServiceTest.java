package com.ktube.vod.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ktube.vod.user.UserConstants.ALREADY_EXISTING_EMAIL_MESSAGE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userService = new UserService(userRepository);
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
        User resultUser = userService.join(request);

        //then
        verify(userRepository, times(1)).create(any());
        assertThat(resultUser.getEmail()).isEqualTo(request.getEmail());
        assertThat(PasswordEncoderUtils.match(request.getPassword(), resultUser.getPassword())).isTrue();
        assertThat(resultUser.getNickname()).isEqualTo(request.getNickname());
        assertThat(resultUser.getRole()).isEqualTo(UserRole.TEMPORARY);
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
                .willThrow(new IllegalArgumentException(ALREADY_EXISTING_EMAIL_MESSAGE));

        // when & then
        Exception result = Assertions.assertThrows(Exception.class, () -> userService.join(request));

        assertThat(result.getClass()).isEqualTo(IllegalArgumentException.class);
        assertThat(result.getMessage()).isEqualTo(ALREADY_EXISTING_EMAIL_MESSAGE);
    }
}
