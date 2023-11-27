package com.ktube.vod.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
@ActiveProfiles("test")
@Transactional(readOnly = true)
public class JpaUserRepositoryTest {

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
        user.setPassword("123456789!d");
        user.setNickname("hi");
        user.setGrade(UserGrade.TEMPORARY);

        //when
        KTubeUser resultUser = userRepository.findById(user.getId());

        //then
        assertThat(resultUser.getId()).isEqualTo(user.getId());
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(PasswordEncoderUtils.match(user.getPassword(),resultUser.getPassword())).isTrue();
        assertThat(resultUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(resultUser.getGrade()).isEqualTo(user.getGrade());
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
        user.setPassword("123456789!d");
        user.setNickname("hi");
        user.setGrade(UserGrade.TEMPORARY);

        //when
        KTubeUser resultUser = userRepository.findByEmail(user.getEmail());

        //then
        assertThat(resultUser.getId()).isEqualTo(user.getId());
        assertThat(resultUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(PasswordEncoderUtils.match(user.getPassword(),resultUser.getPassword())).isTrue();
        assertThat(resultUser.getNickname()).isEqualTo(user.getNickname());
        assertThat(resultUser.getGrade()).isEqualTo(user.getGrade());
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

    @DisplayName("기존 이메일에 해당하는 데이터가 없을 경우 유저 데이터를 저장한다.")
    @Test
    public void testCreateUserWithNotExistingUser() {

        //given
        String email = "notExistingUser@naver.com";
        KTubeUser user = KTubeUser.init(email, "1234567!@sdg", "test");

        assertThat(userRepository.findByEmail(email)).isNull();

        //when
        userRepository.create(user);

        //then
        assertThat(userRepository.findByEmail(email)).isNotNull();

    }

    @DisplayName("이미 존재하는 이메일로 계정 생성시 예외가 발생한다.")
    @Test
    public void testCreateUserWithAlreadyExistingUser() {

        //given
        String email = "email@naver.com";
        KTubeUser user = KTubeUser.init(email, "1234567!@sdg", "test");


        //when & then
        Assertions.assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.create(user));
    }


    @DisplayName("존재하는 계정에 대해 업데이트 요청시 업데이트가 실행된다.")
    @Test
    public void testUpdateUserWithExistingUser() {

        //given
        String email = "email@naver.com";
        String updatedNickname = "updatedNickname";
        KTubeUser user = userRepository.findByEmail(email);

        RequestUserUpdateDto param = new RequestUserUpdateDto();
        param.setNickname(updatedNickname);

        //when
        KTubeUser updatedUser = userRepository.update(user.getId(), param);

        //then
        assertThat(updatedUser.getNickname()).isEqualTo(updatedNickname);

        assertThat(updatedUser.getEmail()).isEqualTo(user.getEmail());
        assertThat(updatedUser.getPassword()).isEqualTo(user.getPassword());
        assertThat(updatedUser.getGrade()).isEqualTo(user.getGrade());
        assertThat(updatedUser.getSecurityLevel()).isEqualTo(user.getSecurityLevel());
    }

    @DisplayName("존재하는 계정에 대해 업데이트 요청시 업데이트가 실행된다.")
    @Test
    public void testUpdateUserWithNotExistingUser() {

        //given
        RequestUserUpdateDto param = new RequestUserUpdateDto();
        param.setNickname("updatedNickname");

        //when & then
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class,
                ()->userRepository.update(151L, param));
    }

    @DisplayName("존재하는 유저로 계정 삭제 요청시 삭제된다.")
    @Test
    public void testDeleteUserWithExistingUser() {

        //given
        Long userId = 1L;
        KTubeUser user = em.find(KTubeUser.class, userId);
        assertThat(user).isNotNull();

        //when
        userRepository.delete(userId);

        //then
        KTubeUser deletedUser = em.find(KTubeUser.class, userId);
        assertThat(deletedUser).isNull();
    }

    @DisplayName("존재하는 유저로 계정 삭제 요청시 삭제된다.")
    @Test
    public void testDeleteUserWithNotExistingUser() {

        //given
        Long userId = 131L;
        KTubeUser user = em.find(KTubeUser.class, userId);
        assertThat(user).isNull();

        //when & then
        Assertions.assertThrows(InvalidDataAccessApiUsageException.class,
                ()->userRepository.delete(userId));
    }
}
