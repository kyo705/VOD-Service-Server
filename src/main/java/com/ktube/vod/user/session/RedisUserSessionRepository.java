package com.ktube.vod.user.session;

import com.ktube.vod.security.login.KTubeUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class RedisUserSessionRepository implements UserSessionRepository {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Override
    public List<ResponseUserSessionDto> getSessionsFromUser(String principal) {

        return sessionRepository.findByPrincipalName(principal)
                .values()
                .stream()
                .filter(session -> !session.isExpired())
                .map(session -> {
                    String sessionId = session.getId();
                    KTubeUserDetails details = (KTubeUserDetails)((SecurityContext) session
                            .getAttribute("SPRING_SECURITY_CONTEXT"))
                            .getAuthentication()
                            .getPrincipal();

                    ResponseUserSessionDto result = new ResponseUserSessionDto();
                    result.setSessionId(sessionId);
                    result.setConnectIp(details.getConnectIp());
                    result.setConnectDevice(details.getConnectDevice());

                    return result;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSession(String sessionId) {

        sessionRepository.deleteById(sessionId);
    }
}
