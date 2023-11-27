package com.ktube.vod.user;


import com.ktube.vod.user.log.JpaUserLogRepository;
import com.ktube.vod.user.log.UserConnectType;
import com.ktube.vod.user.log.UserLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional(readOnly = true)
public class JpaUserLogRepositoryTest {

    @Autowired
    private JpaUserLogRepository userLogRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("유저 로그 조회 : 올바른 파라미터로 요청시 로그 데이터들이 반환된다.")
    @Test
    public void testFindByUserIdWithValidParam() {

        //given
        long userId = 1L;
        int offset = 0;
        int size = 20;

        //when
        List<UserLog> userLogs = userLogRepository.findByUserId(userId, offset, size);

        //then
        assertThat(userLogs.size()).isLessThanOrEqualTo(size);
        userLogs.forEach(userLog -> assertThat(userLog.getUserId()).isEqualTo(userId));
    }

    @DisplayName("유저 로그 조회 : 잘못된 파라미터로 요청시 예외가 발생한다.")
    @MethodSource("com.ktube.vod.user.UserSetup#getInvalidParamWithFindUserLogs")
    @ParameterizedTest
    public void testFindByUserIdWithInvalidParam(int offset, int size) {

        //given
        long userId = 1L;

        //when
        assertThrows(InvalidDataAccessApiUsageException.class,
                () -> userLogRepository.findByUserId(userId, offset, size));

    }

    @DisplayName("유저 로그 생성 : 올바른 파라미터로 요청시 로그 데이터가 저장된다.")
    @Test
    public void testCreateWithValidParam() {

        //given
        UserLog connectLog = UserLog.init(1, "ip", "device5", UserConnectType.LOGIN);

        assertThat(connectLog.getId()).isNull();

        //when
        userLogRepository.create(connectLog);

        //then
        assertThat(connectLog.getId()).isNotNull();

        UserLog savedUserLog = em.find(UserLog.class, connectLog.getId());
        assertThat(savedUserLog).isNotNull();
    }
}
