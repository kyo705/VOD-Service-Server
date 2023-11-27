package com.ktube.vod.user.log;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaUserLogRepository implements UserLogRepository {

    private final EntityManager em;
    @Override
    public List<UserLog> findByUserId(Long userId, int offset, int size) {

        String userIdParameter = "userId";
        String jpql = String.format("SELECT l FROM UserLog l WHERE l.%s = :%s",
                userIdParameter, userIdParameter);

        return em.createQuery(jpql, UserLog.class)
                .setParameter(userIdParameter, userId)
                .setFirstResult(offset)
                .setMaxResults(size)
                .getResultList();
    }

    @Override
    public void create(UserLog userLog) {

        em.persist(userLog);
    }
}
