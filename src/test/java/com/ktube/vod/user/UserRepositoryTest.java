package com.ktube.vod.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@ActiveProfiles("test")
@Transactional(readOnly = true)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @DisplayName("유저 데이터가 저장될 때 id 값을 자동으로 생성한다.")
    @Test
    public void testCreatingUser() {

        //given
        KTubeUser user = KTubeUser.init("notExistingEmail@naver.com", "password1234!", "닉네임");

        assertThat(user.getId()).isNull();

        //when
        userRepository.create(user);

        //then
        assertThat(user.getId()).isNotNull();
    }

    @DisplayName("이미 존재하는 이메일로 저장 요청시 예외가 발생한다.")
    @Test
    public void testFindingUserByEmailWithDuplicatedEmail() {

        //given
        KTubeUser user = KTubeUser.init("email@naver.com", "password1234!", "닉네임1");

        //when & then
        try {
            userRepository.create(user);

            em.flush();
            em.clear();
        } catch (DataIntegrityViolationException e) {
            return;
        }
        throw new IllegalStateException("이메일이 중복되어 저장됨");
    }

    @DisplayName("존재하는 유저 id로 조회할 경우 유저 데이터를 가져온다.")
    @Test
    public void testFindingUserByIdWithExistingUserId() {

        //given
        KTubeUser user = new KTubeUser();
        user.setId(1L);
        user.setEmail("email@naver.com");
        user.setPassword("$2a$10$T.aFIqEh8NA3QahFugycAu/IZWUXoAihFYYsWwulBZRRkxAg6zUy6");
        user.setNickname("hi");
        user.setRole(UserRole.TEMPORARY);

        //when
        KTubeUser resultUser = userRepository.findById(user.getId());

        //then
        assertThat(resultUser.getId()).isEqualTo(user.getId());
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(resultUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(resultUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(resultUser.getRole()).isEqualTo(user.getRole());
    }

    @DisplayName("존재하지 않는 유저 id로 조회할 경우 null을 리턴한다.")
    @Test
    public void testFindingUserByIdWithNotExistingUserId() {

        //given
        Long userId = 5L;

        //when
        KTubeUser resultKTubeUser = userRepository.findById(userId);

        //then
        assertThat(resultKTubeUser).isNull();
    }

    @DisplayName("존재하는 유저 이메일로 조회할 경우 유저 데이터를 가져온다.")
    @Test
    public void testFindingUserByEmailWithExistingEmail() {

        //given
        KTubeUser user = new KTubeUser();
        user.setId(1L);
        user.setEmail("email@naver.com");
        user.setPassword("$2a$10$T.aFIqEh8NA3QahFugycAu/IZWUXoAihFYYsWwulBZRRkxAg6zUy6");
        user.setNickname("hi");
        user.setRole(UserRole.TEMPORARY);

        //when
        KTubeUser resultUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(resultUser.getId()).isEqualTo(user.getId());
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(resultUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(resultUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(resultUser.getRole()).isEqualTo(user.getRole());
    }

    @DisplayName("존재하지 않는 유저 이메일로 조회할 경우 null을 리턴한다.")
    @Test
    public void testFindingUserByEmailWithNotExistingEmail() {

        //given
        String email = "notExistingEmail@naver.com";

        //when
        KTubeUser resultUser = userRepository.findByEmail(email);

        //then
        assertThat(resultUser).isNull();
    }
}
