package com.ktube.vod.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {

    private final EntityManager em;

    @Override
    public void create(KTubeUser user) {

        em.persist(user);
    }

    @Override
    public KTubeUser findById(Long id) {

        return em.find(KTubeUser.class, id);
    }

    @Override
    public KTubeUser findByEmail(String email) {

        String emailParam = "email";
        String query = String.format("SELECT u FROM KTubeUser u WHERE u.email = :%s", emailParam);

        List<KTubeUser> users = em.createQuery(query, KTubeUser.class)
                .setParameter(emailParam, email)
                .getResultList();

        if(users.size() >= 2) {
            throw new NonUniqueResultException();
        }
        if(users.isEmpty()) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public KTubeUser update(KTubeUser user) {
        return null;
    }

    @Override
    public KTubeUser delete(Long id) {
        return null;
    }
}
