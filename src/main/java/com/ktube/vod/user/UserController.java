package com.ktube.vod.user;

import com.ktube.vod.identification.RequestIdentificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ktube.vod.user.UserConstants.USER_IDENTIFICATION_URL;
import static com.ktube.vod.user.UserConstants.USER_URL;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(USER_URL)
    public ResponseUserDto join(@RequestBody @Valid RequestUserJoinDto requestBody) {

        KTubeUser joinedUser = userService.join(requestBody);

        return new ResponseUserDto(joinedUser);
    }

    @GetMapping(USER_IDENTIFICATION_URL)
    public ResponseUserDto identify(@ModelAttribute @Valid RequestIdentificationDto requestBody) {

        KTubeUser joinedUser =  userService.identifyUser(requestBody.getIdentificationCode());

        return new ResponseUserDto(joinedUser);
    }

}
