package com.ktube.vod.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.ktube.vod.user.UserConstants.USER_URL;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(USER_URL)
    public ResponseUserJoinDto join(@RequestBody @Valid RequestUserJoinDto requestBody) {

        User joinedUser = userService.join(requestBody);

        return ResponseUserJoinDto
                .builder()
                .id(joinedUser.getId())
                .email(joinedUser.getEmail())
                .nickname(joinedUser.getNickname())
                .userRole(joinedUser.getRole())
                .build();
    }
}
