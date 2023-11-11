package com.ktube.vod.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.identification.IdentificationFailureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class UserControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @MockBean private UserService mockUserService;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

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


    @DisplayName("올바른 인증 번호로 본인 인증 요청시 200 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithValidParam() throws Exception {

        //given
        KTubeUser user = KTubeUser.init("email@naver.com", "1234567890asg", "테스트");
        given(mockUserService.identifyUser(any())).willReturn(user);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_IDENTIFICATION_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userRole").value(user.getRole().name()));
    }

    @DisplayName("인증 번호 없이 본인 인증 요청시 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithoutParam() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_IDENTIFICATION_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));
    }

    @DisplayName("잘못된 인증 번호로 본인 인증 요청시 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithInvalidParam() throws Exception {

        //given
        given(mockUserService.identifyUser(any())).willThrow(IdentificationFailureException.class);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_IDENTIFICATION_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));
    }

    @DisplayName("이미 인증된 사용자가 해당 요청에 대해 다시 본인 인증 요청시 409 상태코드를 리턴한다.")
    @Test
    public void testIdentifyWithAlreadyIdentifiedUser() throws Exception {

        //given
        given(mockUserService.identifyUser(any())).willThrow(DataIntegrityViolationException.class);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserConstants.USER_IDENTIFICATION_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.CONFLICT.value()));
    }
}
