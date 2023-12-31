package com.ktube.vod.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.user.log.UserLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static com.ktube.vod.security.SecurityConstants.LOGIN_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class LoginIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;

    @MockBean
    private UserLogService userLogService;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("인증이 완료된 유저로 로그인 시도할 경우 200 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.security.login.LoginSetup#getLoginDtoWithAuthorizedUser")
    @ParameterizedTest
    public void testLoginWithAuthorizedUserInfo(RequestLoginDto requestBody) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.OK.value());
                });

        Mockito.verify(userLogService, times(1)).create(any());
    }

    @DisplayName("허용되지 않은 디바이스에서 로그인 시도할 경우 401 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.security.login.LoginSetup#getLoginDtoWithUnauthorizedUser")
    @ParameterizedTest
    public void testLoginWithUnauthorizedUserInfo(RequestLoginDto requestBody) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.UNAUTHORIZED.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.value());
                });

        Mockito.verify(userLogService, times(0)).create(any());
    }

    @DisplayName("인증이 완료되지 않은 유저로 로그인 시도할 경우 403 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.security.login.LoginSetup#getLoginDtoWithUserRequiredToIdentity")
    @ParameterizedTest
    public void testLoginWithUserInfoRequiredToIdentity(RequestLoginDto requestBody) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
                });

        Mockito.verify(userLogService, times(0)).create(any());
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("기존 세션이 있으면서 로그인 시도할 경우 409 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.security.login.LoginSetup#getLoginDtoWithAuthorizedUser")
    @ParameterizedTest
    public void testLoginWithAlreadyExistingSession(RequestLoginDto requestBody) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.CONFLICT.value()))
                .andExpect(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.CONFLICT.value());
                });

        Mockito.verify(userLogService, times(0)).create(any());
    }

    @DisplayName("존재하지 않는 이메일로 로그인 시도할 경우 400 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.security.login.LoginSetup#getLoginDtoWithNotExistingEmail")
    @ParameterizedTest
    public void testLoginWithNotExistingEmail(RequestLoginDto requestBody) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

        Mockito.verify(userLogService, times(0)).create(any());
    }

    @DisplayName("비밀번호가 부정확한 요청으로 로그인 시도할 경우 400 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.security.login.LoginSetup#getLoginDtoWithInvalidPassword")
    @ParameterizedTest
    public void testLoginWithInvalidPassword(RequestLoginDto requestBody) throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

        Mockito.verify(userLogService, times(0)).create(any());
    }
}
