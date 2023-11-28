package com.ktube.vod.user.basic;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.user.UserSetup;
import com.ktube.vod.user.log.UserLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @MockBean private UserService mockUserService;
    @MockBean private UserLogService userLogService;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    // --------------------- FIND CURRENT USER --------------------------

    @WithUserDetails("email@naver.com")
    @DisplayName("올바른 파라미터로 회원가입 요청 시 200 상태코드를 리턴한다.")
    @Test
    public void testFindCurrentUserWithValidParam() throws Exception {

        //given
        KTubeUser user = new KTubeUser();
        given(mockUserService.find(anyLong())).willReturn(user);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                ;

        verify(mockUserService, times(1)).find(1);

    }


    @DisplayName("올바른 파라미터로 회원가입 요청 시 200 상태코드를 리턴한다.")
    @Test
    public void testFindCurrentUserWithNoSession() throws Exception {

        //given
        KTubeUser user = new KTubeUser();
        given(mockUserService.find(anyLong())).willReturn(user);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
        ;

        verify(mockUserService, times(0)).find(1);

    }


    // --------------------- JOIN --------------------------

    @DisplayName("올바른 파라미터로 회원가입 요청 시 200 상태코드를 리턴한다.")
    @Test
    public void testUserJoinWithValidParam() throws Exception {

        //given
        RequestUserJoinDto requestBody = new RequestUserJoinDto();
        requestBody.setEmail("test@naver.com");
        requestBody.setPassword("Aa123456789!");
        requestBody.setNickname("닉네임");

        KTubeUser responseUser = KTubeUser.init(requestBody.getEmail(),
                PasswordEncoderUtils.encodePassword(requestBody.getPassword()),
                requestBody.getNickname());

        given(mockUserService.join(any())).willReturn(responseUser);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(UserConstants.USER_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(requestBody.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value(requestBody.getNickname()));
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("기존 세션이 있을 때 회원 가입 요청시 403 상태코드를 리턴한다.")
    @Test
    public void testUserJoinWithAlreadyExistingSession() throws Exception {

        //given
        RequestUserJoinDto requestBody = new RequestUserJoinDto();
        requestBody.setEmail("emailtest@naver.com");
        requestBody.setPassword("Aa123456789!");
        requestBody.setNickname("닉네임");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(UserConstants.USER_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.FORBIDDEN.value()));
    }

    @DisplayName("request body 없이 회원가입 요청 시 400 상태코드를 리턴한다.")
    @Test
    public void testUserJoinWithoutParam() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(UserConstants.USER_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));
    }

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
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));
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
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));
    }

    @DisplayName("이미 가입되어 있는 이메일로 회원 가입 요청시 400 상태코드를 리턴한다.")
    @Test
    public void testJoinWithAlreadyJoinedEmail() throws Exception {

        //given
        RequestUserJoinDto requestBody = new RequestUserJoinDto();
        requestBody.setEmail("alreadyJoinedEmail@naver.com");
        requestBody.setPassword("1234567890a!");
        requestBody.setNickname("닉네임");

        given(mockUserService.join(any()))
                .willThrow(new IllegalArgumentException("이미 존재하는 이메일 계정입니다."));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post(UserConstants.USER_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));
    }

    // --------------------- UPDATE --------------------------

    @WithUserDetails("email@naver.com")
    @DisplayName("올바른 파라미터들로 유저 업데이트 요청시 204 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.user.UserSetup#getValidUserUpdateParam")
    @ParameterizedTest
    public void testUpdateWithValidParam(String password, String nickname,
                                            int securityLevelCode, int userGradeCode) throws Exception {

        //given
        KTubeUser user = new KTubeUser();
        user.setId(1L);
        user.setEmail("email@naver.com");
        user.setPassword(password);
        user.setNickname(nickname);
        user.setGrade(UserGrade.valueOfCode(userGradeCode));
        user.setSecurityLevel(UserSecurityLevel.valueOfCode(securityLevelCode));

        given(mockUserService.update(anyLong(), any())).willReturn(user);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch(UserConstants.SPECIFIC_USER_URL, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("password", password)
                                .param("nickname", nickname)
                                .param("securityLevel", Integer.toString(securityLevelCode))
                                .param("grade", Integer.toString(userGradeCode))
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }


    @DisplayName("세션이 존재하지 않을 때 유저 정보로 접근할 경우 403 예외가 발생한다.")
    @Test
    public void testUpdateWithNoSession() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch(UserConstants.SPECIFIC_USER_URL, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("nickname", "updatedTT")
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("현재 세션과 일치하지 않는 유저 정보로 접근할 경우 400 예외가 발생한다.")
    @Test
    public void testUpdateWithInvalidParam() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch(UserConstants.SPECIFIC_USER_URL, 2)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("nickname", "updatedTT")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @WithUserDetails("email@naver.com")
    @DisplayName("잘못된 파라미터들로 유저 업데이트 요청시 400 예외가 발생한다.")
    @MethodSource("com.ktube.vod.user.UserSetup#getInvalidUserUpdateParam")
    @ParameterizedTest
    public void testUpdateWithInvalidParam2(String password, String nickname,
                                            int securityLevelCode, int userGradeCode) throws Exception {

        //given

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .patch(UserConstants.SPECIFIC_USER_URL, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("password", password)
                                .param("nickname", nickname)
                                .param("securityLevel", Integer.toString(securityLevelCode))
                                .param("grade", Integer.toString(userGradeCode))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    // --------------------- DELETE --------------------------


    @WithUserDetails("email@naver.com")
    @DisplayName("올바른 파라미터들로 계정 탈퇴 요청시 200 상태코드를 리턴한다.")
    @Test
    public void testDeleteWithValidParam() throws Exception {

        //given

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(UserConstants.SPECIFIC_USER_URL, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
        ;
    }

    @DisplayName("세션이 없을 때 유저 정보로 접근할 경우 403 예외가 발생한다.")
    @Test
    public void testDeleteWithNoSession() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(UserConstants.SPECIFIC_USER_URL, 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
        ;
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("현재 세션과 일치하지 않는 유저 정보로 접근할 경우 400 예외가 발생한다.")
    @Test
    public void testDeleteWithInvalidParam() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(UserConstants.SPECIFIC_USER_URL, 2)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
        ;
    }


    // --------------------- FIND USER CONNECT LOGS ------------------------


    @WithUserDetails("email@naver.com")
    @DisplayName("유저 로그 조회 : 올바른 파라미터로 요청시 200 상태코드를 리턴한다.")
    @Test
    public void testFindUserConnectLogsWithValidParam() throws Exception {

        //given
        long userId = 1L;
        int offset = 0;
        int size = 20;
        given(userLogService.findByUserId(userId, offset, size)).willReturn(UserSetup.getUserLogs(userId));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_CONNECT_LOG_URL, userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
        ;
    }


    @DisplayName("유저 로그 조회 : 세션이 없을 경우 예외가 발생한다.")
    @Test
    public void testFindUserConnectLogsWithNoSession() throws Exception {

        //given
        long userId = 1L;
        int offset = 0;
        int size = 20;

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_CONNECT_LOG_URL, userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated())
        ;
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("유저 로그 조회 : 잘못된 파라미터로 요청시 400 상태코드를 리턴한다.")
    @MethodSource("com.ktube.vod.user.UserSetup#getInvalidParamWithFindUserConnectLogs")
    @ParameterizedTest
    public void testFindUserConnectLogsWithInvalidParam(long userId, int offset, int size) throws Exception {

        //given
        given(userLogService.findByUserId(userId, offset, size)).willReturn(UserSetup.getUserLogs(userId));

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_CONNECT_LOG_URL, userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("offset", Integer.toString(offset))
                                .param("size", Integer.toString(size))
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(SecurityMockMvcResultMatchers.authenticated())
        ;
    }
}
