package com.ktube.vod.user.log;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserLogService {

    private final UserLogRepository userLogRepository;

    @Transactional
    public List<UserLog> findByUserId(long userId, int offset, int size) {

        return userLogRepository.findByUserId(userId, offset, size);
    }

    @Transactional
    public void create(RequestUserLogCreateDto requestParam) {

        UserLog userLog = UserLog.init(requestParam.getUserId(),
                requestParam.getConnectIp(),
                requestParam.getConnectDevice(),
                requestParam.getConnectType());

        userLogRepository.create(userLog);
    }
}
