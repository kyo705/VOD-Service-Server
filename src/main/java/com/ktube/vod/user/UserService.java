package com.ktube.vod.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.ktube.vod.user.UserConstants.ALREADY_EXISTING_EMAIL_MESSAGE;
import static com.ktube.vod.user.UserConstants.NOT_EXISTING_EMAIL_MESSAGE;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User join(RequestUserJoinDto requestBody) {

        String email = requestBody.getEmail();
        String password = PasswordEncoderUtils.encodePassword(requestBody.getPassword());
        String nickname = requestBody.getNickname();

        if(userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException(ALREADY_EXISTING_EMAIL_MESSAGE);
        }
        User user = User.init(email, password, nickname);
        userRepository.create(user);

        return user;
    }

    @Transactional
    public void registerIdentifiedUser(String email) {

        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException(NOT_EXISTING_EMAIL_MESSAGE);
        }
        user.identified();
        userRepository.update(user);
    }
}
