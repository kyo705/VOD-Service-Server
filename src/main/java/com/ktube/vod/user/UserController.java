package com.ktube.vod.user;

import com.ktube.vod.security.login.KTubeUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.ktube.vod.user.UserConstants.SPECIFIC_USER_URL;
import static com.ktube.vod.user.UserConstants.USER_URL;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping(USER_URL)
    public ResponseUserDto findCurrentUser() throws IllegalAccessException {

        KTubeUserDetails userDetails = (KTubeUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if(userDetails == null) {
            throw new IllegalAccessException("현재 세션 정보가 없습니다.");
        }
        KTubeUser user =  userService.find(userDetails.getUserId());

        return new ResponseUserDto(user);
    }

    @PostMapping(USER_URL)
    public ResponseUserDto join(@RequestBody @Valid RequestUserJoinDto requestBody) {

        KTubeUser joinedUser = userService.join(requestBody);

        return new ResponseUserDto(joinedUser);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(SPECIFIC_USER_URL)
    public void update(@PathVariable Long userId, @ModelAttribute @Valid RequestUserUpdateDto requestParam) throws IllegalAccessException {

        validate(userId);

        KTubeUser updatedUser = userService.update(userId, requestParam);

        KTubeUserDetails userDetails = new KTubeUserDetails(updatedUser);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(SPECIFIC_USER_URL)
    public void delete(@PathVariable Long userId) throws IllegalAccessException {

        validate(userId);

        userService.delete(userId);

        SecurityContextHolder.clearContext();
    }

    private void validate(long userId) throws IllegalAccessException {

        KTubeUserDetails userDetails = (KTubeUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if(userDetails.getUserId() != userId) {
            throw new IllegalAccessException("요청한 유저 id와 실제 유저 id가 불일치합니다.");
        }
    }
}
