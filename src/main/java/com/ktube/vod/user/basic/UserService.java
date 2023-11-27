package com.ktube.vod.user.basic;

import com.ktube.vod.identification.IdentificationService;
import com.ktube.vod.notification.NotificationFailureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ktube.vod.user.basic.UserConstants.ALREADY_EXISTING_EMAIL_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("jwtIdentificationService")
    private final IdentificationService identificationService;

    private final UserRepository userRepository;

    @Transactional
    public void create(KTubeUser user) {

        userRepository.create(user);
    }

    @Transactional(readOnly = true)
    public KTubeUser find(long userId) {

        return userRepository.findById(userId);
    }

    @Transactional
    public KTubeUser update(long userId, RequestUserUpdateDto requestParam) {

        return userRepository.update(userId, requestParam);
    }

    @Transactional
    public void delete(long userId) {

        userRepository.delete(userId);
    }

    @Transactional(readOnly = true)
    public boolean isExistingUser(String email) {

        return userRepository.findByEmail(email) != null;
    }

    /**
     *
     * @param requestBody 회원 가입에 필요한 유저 데이터
     * @return 임시 가입된 유저 데이터
     */
    @Transactional
    public KTubeUser join(RequestUserJoinDto requestBody) throws NotificationFailureException {

        String email = requestBody.getEmail();
        String password = PasswordEncoderUtils.encodePassword(requestBody.getPassword());
        String nickname = requestBody.getNickname();

        if(isExistingUser(email)) {
            throw new IllegalArgumentException(ALREADY_EXISTING_EMAIL_MESSAGE);
        }
        KTubeUser user = KTubeUser.init(email, password, nickname);

        // 인증 생성
        identificationService.createIdentification(email, user);

        return user;
    }

}
