package com.ktube.vod.user.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class UserSessionControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private WebApplicationContext context;
    private MockMvc mockMvc;
    @MockBean
    private UserSessionService mockUserSessionService;

    @BeforeEach
    public void setup(){

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("세션 조회 : 현재 세션과 일치하는 유저 id로 요청시 200 상태 코드를 리턴한다.")
    @Test
    public void testFindUserSessionsFromCurrentUserWithValidParam() throws Exception {

        //given
        given(mockUserSessionService.getSessionsFromCurrentUser())
                .willReturn(new ArrayList<>());

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserSessionConstants.USER_SESSION_URL, 1)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
        ;

        verify(mockUserSessionService, times(1)).getSessionsFromCurrentUser();
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("세션 조회 : 현재 세션과 일치하지 않는 유저 id로 요청시 400 상태 코드를 리턴한다.")
    @Test
    public void testFindUserSessionsFromCurrentUserWithInvalidParam() throws Exception {

        //given

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserSessionConstants.USER_SESSION_URL, 2)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        ;

        verify(mockUserSessionService, times(0)).getSessionsFromCurrentUser();
    }


    @DisplayName("세션 조회 : 현재 세션이 존재하지 않은 채 요청시 403 상태 코드를 리턴한다.")
    @Test
    public void testFindUserSessionsFromCurrentUserWithNoSession() throws Exception {

        //given

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserSessionConstants.USER_SESSION_URL, 2)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.FORBIDDEN.value()))
        ;

        verify(mockUserSessionService, times(0)).getSessionsFromCurrentUser();
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("세션 삭제 : 현재 세션과 일치하는 유저의 SessionId를 삭제 요청시 204 상태 코드를 리턴한다.")
    @Test
    public void testKillSessionWithValidParam() throws Exception {

        //given
        String sessionId = "sessionId";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(UserSessionConstants.SPECIFIC_USER_SESSION_URL, 1, sessionId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isNoContent())
        ;

        verify(mockUserSessionService, times(1)).killSession(sessionId);
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("세션 삭제 : 현재 세션과 일치하지 않는 유저 id로 요청시 400 상태 코드를 리턴한다.")
    @Test
    public void testKillSessionWithInvalidParam1() throws Exception {

        //given
        String sessionId = "sessionId";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get(UserSessionConstants.USER_SESSION_URL, 2, sessionId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        ;

        verify(mockUserSessionService, times(0)).killSession(sessionId);
    }

    @WithUserDetails("email@naver.com")
    @DisplayName("세션 삭제 : 현재 세션 유저에 존재하지 않는 SessionId로 요청시 400 상태 코드를 리턴한다.")
    @Test
    public void testKillSessionWithInvalidParam2() throws Exception {

        //given
        String sessionId = "sessionId";
        willThrow(IllegalArgumentException.class).given(mockUserSessionService).killSession(sessionId);

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(UserSessionConstants.SPECIFIC_USER_SESSION_URL, 1, sessionId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.BAD_REQUEST.value()))
        ;

        verify(mockUserSessionService, times(1)).killSession(sessionId);
    }


    @DisplayName("세션 삭제 : 현재 세션이 존재하지 않은 채 요청시 403 상태 코드를 리턴한다.")
    @Test
    public void testKillSessionWithNoSession() throws Exception {

        //given
        String sessionId = "sessionId";

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .delete(UserSessionConstants.SPECIFIC_USER_SESSION_URL, 1, sessionId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").value(HttpStatus.FORBIDDEN.value()))
        ;

        verify(mockUserSessionService, times(0)).getSessionsFromCurrentUser();
    }
}
