package com.ktube.vod.user.session;

import com.ktube.vod.security.login.KTubeUserDetails;
import com.ktube.vod.user.basic.KTubeUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class UserSessionServiceTest {

    private UserSessionService userSessionService;
    @Mock
    private UserSessionRepository mockUserSessionRepository;

    @BeforeEach
    public void setup() {
        userSessionService = new UserSessionService(mockUserSessionRepository);
    }

    @DisplayName("세션 조회 : 현재 세션에 해당하는 유저의 모든 세션 정보들을 제공한다.")
    @Test
    public void testFindUserSessionsWithSession() throws IllegalAccessException {

        //given
        String principal = "email@naver.com";
        KTubeUser user = KTubeUser.init(principal, "password", "nickname");
        user.setId(1L);
        KTubeUserDetails userDetails = new KTubeUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        List<ResponseUserSessionDto> expectedSessions = new ArrayList<>();
        BDDMockito.given(mockUserSessionRepository.getSessionsFromUser(principal)).willReturn(expectedSessions);

        //when
        List<ResponseUserSessionDto> resultSessions = userSessionService.getSessionsFromCurrentUser();

        //then
        Assertions.assertThat(expectedSessions.size()).isEqualTo(resultSessions.size());
        Mockito.verify(mockUserSessionRepository, Mockito.times(1)).getSessionsFromUser(principal);

    }

    @DisplayName("세션 조회 : 현재 세션이 없을 경우 예외가 발생한다.")
    @Test
    public void testFindUserSessionsWithNoSession() {

        //given
        String principal = "email@naver.com";
        Authentication authentication = new AnonymousAuthenticationToken(principal, principal, List.of(()-> "ROLE_ANONYMOUS"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        //when & then
        assertThrows(IllegalAccessException.class, () -> userSessionService.getSessionsFromCurrentUser());
        Mockito.verify(mockUserSessionRepository, Mockito.times(0)).getSessionsFromUser(principal);

    }

    @DisplayName("세선 삭제 : 현재 세션에 해당하는 유저의 세션들 중 하나의 세션 아이디로 삭제 요청할 경우 해당 세션이 삭제된다.")
    @Test
    public void testKillSessionWithCurrentUserSession() throws IllegalAccessException {

        //given
        String sessionId = "sessionId";
        String principal = "email@naver.com";
        KTubeUser user = KTubeUser.init(principal, "password", "nickname");
        user.setId(1L);
        KTubeUserDetails userDetails = new KTubeUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        ResponseUserSessionDto session1 = new ResponseUserSessionDto();
        session1.setSessionId(sessionId);
        List<ResponseUserSessionDto> sessions = new ArrayList<>();
        sessions.add(session1);

        BDDMockito.given(mockUserSessionRepository.getSessionsFromUser(principal)).willReturn(sessions);

        //when
        userSessionService.killSession(sessionId);

        //then
        Mockito.verify(mockUserSessionRepository, Mockito.times(1)).deleteSession(sessionId);

    }

    @DisplayName("세션 삭제 : 현재 세션이 존재하지 않을 경우 예외가 발생한다.")
    @Test
    public void testKillSessionWithNoSession() {

        //given
        String sessionId = "sessionId";
        String principal = "email@naver.com";
        Authentication authentication = new AnonymousAuthenticationToken(principal, principal, List.of(()-> "ROLE_ANONYMOUS"));
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        //when & then
        assertThrows(IllegalAccessException.class, () -> userSessionService.killSession(sessionId));
        Mockito.verify(mockUserSessionRepository, Mockito.times(0)).deleteSession(sessionId);

    }

    @DisplayName("세션 삭제 : 현재 유저에 해당하는 세션 정보가 아닌 세션 값을 삭제 시도하면 예외가 발생한다.")
    @Test
    public void testKillSessionWithNotCurrentUserSession() {

        //given
        String sessionId = "sessionId";
        String principal = "email@naver.com";
        KTubeUser user = KTubeUser.init(principal, "password", "nickname");
        user.setId(1L);
        KTubeUserDetails userDetails = new KTubeUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        ResponseUserSessionDto session1 = new ResponseUserSessionDto();
        session1.setSessionId("no" + sessionId);
        List<ResponseUserSessionDto> sessions = new ArrayList<>();
        sessions.add(session1);

        BDDMockito.given(mockUserSessionRepository.getSessionsFromUser(principal)).willReturn(sessions);

        //when & then
        assertThrows(IllegalArgumentException.class, () -> userSessionService.killSession(sessionId));
        Mockito.verify(mockUserSessionRepository, Mockito.times(0)).deleteSession(sessionId);

    }

}
