package com.ktube.vod.user.log;

import java.util.List;

public interface UserLogRepository {

    List<UserLog> findByUserId(Long userId, int offset, int size);

    void create(UserLog userLog);
}
