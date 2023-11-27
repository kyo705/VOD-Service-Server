package com.ktube.vod.identification;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.security.login.KTubeUserDetails;
import com.ktube.vod.security.login.ResponseLoginDto;
import com.ktube.vod.user.basic.KTubeUser;
import com.ktube.vod.user.basic.ResponseUserDto;
import com.ktube.vod.user.basic.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import static com.ktube.vod.identification.IdentificationConstants.IDENTIFICATION_JOIN_URL;
import static com.ktube.vod.identification.IdentificationConstants.IDENTIFICATION_LOGIN_URL;

@RequiredArgsConstructor
@RestController
public class IdentificationController {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Qualifier("jwtIdentificationService")
    private final IdentificationService identificationService;
    private final UserService userService;

    @GetMapping(path = IDENTIFICATION_LOGIN_URL)
    public ResponseLoginDto identifyUserToLogin(@RequestParam @Valid @NotEmpty String identificationCode) throws JsonProcessingException {

        String userData = (String) identificationService.identify(identificationCode);

        KTubeUserDetails userDetails = objectMapper.readValue(userData, KTubeUserDetails.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        return ResponseLoginDto.builder()
                .code(HttpStatus.OK.value())
                .message("로그인 성공")
                .build();
    }


    @GetMapping(path = IDENTIFICATION_JOIN_URL)
    public ResponseUserDto identifyUserToJoin(@RequestParam @Valid @NotEmpty String identificationCode) throws JsonProcessingException {

        String userData = (String) identificationService.identify(identificationCode);
        KTubeUser user = objectMapper.readValue(userData, KTubeUser.class);
        userService.create(user);

        return new ResponseUserDto(user);
    }
}
