package com.ktube.vod.user.session;

import com.ktube.vod.security.login.KTubeUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSessionService {

    private final UserSessionRepository userSessionRepository;

    public List<ResponseUserSessionDto> getSessionsFromCurrentUser() throws IllegalAccessException {

        String principal = getPrincipalFromCurrentSession();

        return userSessionRepository.getSessionsFromUser(principal);
    }

    public void killSession(String sessionId) throws IllegalAccessException {

        String principal = getPrincipalFromCurrentSession();

        List<ResponseUserSessionDto> userSessions = userSessionRepository.getSessionsFromUser(principal);
        if(!isContainsCurrentSession(userSessions, sessionId)) {
            throw new IllegalArgumentException("현재 유저에 존재하지 않는 세션입니다.");
        }
        userSessionRepository.deleteSession(sessionId);
    }

    private String getPrincipalFromCurrentSession() throws IllegalAccessException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            KTubeUserDetails userDetails = (KTubeUserDetails) authentication.getPrincipal();
            if(userDetails == null) {
                throw new IllegalAccessException("허용되지 않은 세션 상태입니다.");
            }
            return userDetails.getUsername();
        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalAccessException("허용되지 않은 세션 상태입니다.");
        }

    }

    private boolean isContainsCurrentSession(List<ResponseUserSessionDto> userSessions, String sessionId) {

        for(ResponseUserSessionDto userSession : userSessions) {
            if(userSession.getSessionId().equals(sessionId)) {
                return true;
            }
        }
        return false;
    }
}
