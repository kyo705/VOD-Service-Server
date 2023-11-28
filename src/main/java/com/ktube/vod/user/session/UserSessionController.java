package com.ktube.vod.user.session;


import com.ktube.vod.security.login.KTubeUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserSessionController {

    private final UserSessionService userSessionService;

    @GetMapping("/api/user/{userId}/session")
    public List<ResponseUserSessionDto> getSessionsFromCurrentUser(@PathVariable long userId) throws IllegalAccessException {

        validateCurrentUser(userId);
        return userSessionService.getSessionsFromCurrentUser();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/api/user/{userId}/session/{sessionId}")
    public void killSession(@PathVariable long userId, @PathVariable String sessionId) throws IllegalAccessException {

        validateCurrentUser(userId);
        userSessionService.killSession(sessionId);
    }

    private void validateCurrentUser(long userId) throws IllegalAccessException {

        KTubeUserDetails userDetails = (KTubeUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if(userDetails.getUserId() != userId) {
            throw new IllegalAccessException("요청한 유저 id와 실제 유저 id가 불일치합니다.");
        }
    }
}
