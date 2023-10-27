package com.ktube.vod.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new IllegalArgumentException("이미 존재하는 이메일 계정입니다.");
        }
        User user = User.init(email, password, nickname);

        return userRepository.create(user);
    }

    @Transactional
    public void registerIdentifiedUser(String email) {

        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new IllegalArgumentException("해당 email의 계정이 존재하지 않습니다.");
        }
        user.identified();
        userRepository.update(user);
    }
}
