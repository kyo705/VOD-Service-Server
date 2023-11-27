package com.ktube.vod.identification;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.security.login.KTubeUserDetails;
import com.ktube.vod.user.basic.KTubeUser;
import com.ktube.vod.user.basic.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class IdentificationControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @MockBean
    private IdentificationService mockIdentificationService;
    @MockBean
    private UserService mockUserService;

    @BeforeEach
    public void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("회원가입을 위한 본인 인증 : 올바른 인증 번호로 요청할 경우 200 상태코드를 리턴한다.")
    @Test
    public void testIdentifyToJoinWithValidParam() throws Exception {

        //given
        KTubeUser user = KTubeUser.init("email@naver.com", "1234567890asg", "테스트");
        String userData = objectMapper.writeValueAsString(user);
        given(mockIdentificationService.identify(any())).willReturn(userData);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(IdentificationConstants.IDENTIFICATION_JOIN_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nickname").value(user.getNickname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.userGrade").value(user.getGrade().name()));

        verify(mockUserService, times(1)).create(any());
    }

    @DisplayName("회원가입을 위한 본인 인증 : 인증 번호 없이 요청할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyToJoinWithoutParam() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(IdentificationConstants.IDENTIFICATION_JOIN_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(mockUserService, times(0)).create(any());
    }

    @DisplayName("회원가입을 위한 본인 인증 : 잘못된 인증 번호로 요청할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyToJoinWithInvalidParam() throws Exception {

        //given
        given(mockIdentificationService.identify(any())).willThrow(IdentificationFailureException.class);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(IdentificationConstants.IDENTIFICATION_JOIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()));

        verify(mockUserService, times(0)).create(any());
    }

    @DisplayName("회원가입을 위한 본인 인증 : 이미 인증된 사용자가 해당 요청에 대해 다시 본인 인증 요청시 409 상태코드를 리턴한다.")
    @Test
    public void testIdentifyToJoinWithAlreadyIdentifiedUser() throws Exception {

        //given
        KTubeUser user = KTubeUser.init("email@naver.com", "1234567890asg", "테스트");
        String userData = objectMapper.writeValueAsString(user);
        given(mockIdentificationService.identify(any())).willReturn(userData);
        willThrow(DataIntegrityViolationException.class).given(mockUserService).create(any());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(IdentificationConstants.IDENTIFICATION_JOIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.CONFLICT.value()));

        verify(mockUserService, times(1)).create(any());
    }

    @DisplayName("로그인을 위한 본인 인증 : 올바른 인증 번호로 요청할 경우 200 상태코드를 리턴한다.")
    @Test
    public void testIdentifyToLoginWithValidParam() throws Exception {

        //given
        KTubeUser user = KTubeUser.init("email@naver.com", "1234567890asg", "테스트");
        user.setId(1L);
        KTubeUserDetails userDetails = new KTubeUserDetails(user);
        String userData = objectMapper.writeValueAsString(userDetails);
        given(mockIdentificationService.identify(any())).willReturn(userData);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(IdentificationConstants.IDENTIFICATION_LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @DisplayName("로그인을 위한 본인 인증 : 인증 번호 없이 요청할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyToLoginWithoutParam() throws Exception {

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(IdentificationConstants.IDENTIFICATION_LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    @DisplayName("로그인을 위한 본인 인증 : 잘못된 인증 번호로 요청할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testIdentifyToLoginWithInvalidParam() throws Exception {

        //given
        given(mockIdentificationService.identify(any())).willThrow(IdentificationFailureException.class);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(IdentificationConstants.IDENTIFICATION_LOGIN_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("identificationCode", "12345678")
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }
}
