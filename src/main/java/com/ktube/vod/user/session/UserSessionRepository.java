package com.ktube.vod.user.session;

import java.util.List;

public interface UserSessionRepository {

    List<ResponseUserSessionDto> getSessionsFromUser(String principal);

    void deleteSession(String sessionId);
}
