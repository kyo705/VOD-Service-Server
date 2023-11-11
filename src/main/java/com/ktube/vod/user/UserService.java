package com.ktube.vod.user;

import com.ktube.vod.identification.IdentificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ktube.vod.user.UserConstants.ALREADY_EXISTING_EMAIL_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("jwtIdentificationService")
    private final IdentificationService identificationService;

    private final UserRepository userRepository;


    @Transactional
    public boolean isExistingUser(String email) {

        return userRepository.findByEmail(email) != null;
    }

    /**
     *
     * @param requestBody 회원 가입에 필요한 유저 데이터
     * @return 임시 가입된 유저 데이터
     */
    @Transactional
    public KTubeUser join(RequestUserJoinDto requestBody) {

        String email = requestBody.getEmail();
        String password = PasswordEncoderUtils.encodePassword(requestBody.getPassword());
        String nickname = requestBody.getNickname();

        if(isExistingUser(email)) {
            throw new IllegalArgumentException(ALREADY_EXISTING_EMAIL_MESSAGE);
        }
        KTubeUser user = KTubeUser.init(email, password, nickname);

        // 인증 생성
        identificationService.createIdentification(user);

        return user;
    }

    /**
     * 인증 번호로 인증 처리가 성공하면 해당 계정을 생성한다.
     * @param identificationCode 인증 시도를 위한 코드
     */
    @Transactional
    public KTubeUser identifyUser(String identificationCode) {

        KTubeUser user = identificationService.identify(identificationCode);

        userRepository.create(user);

        return user;
    }
}
