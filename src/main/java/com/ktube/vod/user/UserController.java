package com.ktube.vod.user;

import com.ktube.vod.security.login.KTubeUserDetails;
import com.ktube.vod.user.log.ResponseUserLogDto;
import com.ktube.vod.user.log.UserLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

import static com.ktube.vod.user.UserConstants.*;

@Validated
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserLogService userLogService;

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

    @GetMapping(USER_CONNECT_LOG_URL)
    public List<ResponseUserLogDto> findUserConnectLogs(@PathVariable Long userId,
                                                        @RequestParam @PositiveOrZero int offset,
                                                        @RequestParam @PositiveOrZero int size
    ) throws IllegalAccessException {

        validate(userId);

        return userLogService.findByUserId(userId, offset, size)
                .stream()
                .map(ResponseUserLogDto::new)
                .collect(Collectors.toList());
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
