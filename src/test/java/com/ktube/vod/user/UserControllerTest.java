package com.ktube.vod.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.config.TestSecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

@Import(TestSecurityConfig.class)
@WebMvcTest({UserController.class, UserExceptionHandler.class})
public class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired private MockMvc mockMvc;
    @MockBean private UserService mockUserService;

    @DisplayName("잘못된 이메일로 회원가입 요청 시 400 상태코드를 리턴한다.")
    @EmptySource
    @ValueSource(strings = {"email", "email@", "email@naver,com", "email@naver.com.", "email[@naver.com"})
    @ParameterizedTest
    public void testUserJoinWithInvalidEmail(String invalidEmail) throws Exception {

        //given
        RequestUserJoinDto requestBody = new RequestUserJoinDto();
        requestBody.setEmail(invalidEmail);
        requestBody.setPassword("Aa123456789!");
        requestBody.setNickname("닉네임");

        //when & then
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post(UserConstants.USER_URL)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("잘못된 비밀번호로 회원가입 요청 시 400 상태코드를 리턴한다.")
    @EmptySource
    @ValueSource(
            strings = {"1234567890!" /* 문자 x */
                    , "aaaasdgsdgsgw!!" /* 숫자 x */
                    , "a1234567890a" /* 특수문자 x */
                    , "aaaasdgsdgsgwaaaa" /* 숫자 x, 특수 문자 x */
                    , "12345678901" /* 문자 x, 특수문자 x */
                    , "##!#$#%%!_#%##^#!" /* 문자 x, 숫자 x */
                    , "12345678a!" /* 글자수 11 보다 작음 */
                    , "!a12345678912345678901234567890" /* 글자수 30 보다 큼 */})
    @ParameterizedTest
    public void testUserJoinWithInvalidPassword(String invalidPassword) throws Exception {

        //given
        RequestUserJoinDto requestBody = new RequestUserJoinDto();
        requestBody.setEmail("email@naver.com");
        requestBody.setPassword(invalidPassword);
        requestBody.setNickname("닉네임");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(UserConstants.USER_URL)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("잘못된 닉네임으로 회원가입 요청 시 400 상태코드를 리턴한다.")
    @EmptySource
    @ValueSource(strings = {"~닉네임", "닉~네임", "닉네~임", "닉네임~"})
    @ParameterizedTest
    public void testUserJoinWithInvalidNickname(String invalidNickname) throws Exception {

        //given
        RequestUserJoinDto requestBody = new RequestUserJoinDto();
        requestBody.setEmail("email@naver.com");
        requestBody.setPassword("1234567890a!");
        requestBody.setNickname(invalidNickname);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(UserConstants.USER_URL)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @DisplayName("이미 가입되어 있는 이메일로 회원 가입 요청시 400 상태코드를 리턴한다.")
    @Test
    public void testJoinWithAlreadyJoinedEmail() throws Exception {

        //given
        RequestUserJoinDto requestBody = new RequestUserJoinDto();
        requestBody.setEmail("alreadyJoinedEmail@naver.com");
        requestBody.setPassword("1234567890a!");
        requestBody.setNickname("닉네임");

        BDDMockito.given(mockUserService.join(ArgumentMatchers.any()))
                .willThrow(new IllegalArgumentException("이미 존재하는 이메일 계정입니다."));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(UserConstants.USER_URL)
                                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .characterEncoding(StandardCharsets.UTF_8)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
