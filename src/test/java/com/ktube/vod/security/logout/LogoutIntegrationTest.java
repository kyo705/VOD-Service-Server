package com.ktube.vod.security.logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.security.login.ResponseLoginDto;
import com.ktube.vod.user.log.UserLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static com.ktube.vod.security.SecurityConstants.LOGOUT_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@ActiveProfiles("test")
@SpringBootTest
public class LogoutIntegrationTest {

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

    @WithUserDetails("email@naver.com")
    @DisplayName("기존 세션이 있을 때 로그아웃 시도할 경우 200 상태코드를 리턴한다.")
    @Test
    public void testLogoutWithExistingSession() throws Exception {

        //given
        RequestLogoutDto requestBody = new RequestLogoutDto();
        requestBody.setConnectDevice("device2");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGOUT_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.OK.value());
                });

        Mockito.verify(userLogService, times(1)).create(any());
    }

    @DisplayName("기존 세션이 없을 때 로그아웃 시도할 경우 400 상태코드를 리턴한다.")
    @Test
    public void testLogoutWithNotExistingSession() throws Exception {

        //given
        RequestLogoutDto requestBody = new RequestLogoutDto();
        requestBody.setConnectDevice("device2");

        //when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.post(LOGOUT_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody))
                )
                .andExpect(MockMvcResultMatchers.status().is(HttpStatus.BAD_REQUEST.value()))
                .andExpect(result -> {
                    ResponseLoginDto responseBody = objectMapper.readValue(result.getResponse().getContentAsString(StandardCharsets.UTF_8), ResponseLoginDto.class);
                    assertThat(responseBody.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                });

        Mockito.verify(userLogService, times(0)).create(any());
    }
}
