package com.ktube.vod.security.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ktube.vod.user.UserSecurityLevel;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Set;

public class JsonLoginAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // 로그인 요청이 아닐 경우 다음 필터로 넘어감
        if (!requiresAuthentication((HttpServletRequest) request, (HttpServletResponse) response)) {
            chain.doFilter(request, response);
            return;
        }
        //세션 정보가 이미 존재하는 경우
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            writeResponse((HttpServletResponse) response, HttpStatus.CONFLICT.value(), "이미 세션을 갖고 있는 클라이언트 입니다.");
            return;
        }
        super.doFilter(request, response, chain);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals(HttpMethod.POST.name())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        RequestLoginDto requestBody = extractRequestLoginDto(request); /* requestBody로 부터 데이터 파싱 */

        boolean temporary = requestBody.isTemporary();
        String clientDeviceInfo = requestBody.getClientDeviceInfo();
        String username = requestBody.getEmail();
        String password = requestBody.getPassword();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);
        setDetails(request, authRequest);

        Authentication authentication =  getAuthenticationManager().authenticate(authRequest);
        KTubeUserDetails userDetails = (KTubeUserDetails) authentication.getPrincipal();

        if(temporary || userDetails.getSecurityLevel().getCode() >= UserSecurityLevel.IDENTITY.getCode()) {
            //본인 인증하도록 시도

            //로직 넣어야함

            throw new NotYetAuthorizedException("본인 인증을 해야합니다.");
        }
        // 허용된 디바이스인지 확인
        Set<String> devices = userDetails.getDevices();
        for(String deviceInfo : clientDeviceInfo.split(" ")) {
            if(!devices.contains(deviceInfo)) {
                continue;
            }
            return authentication;
        }
        throw new NotAllowedDeviceException("등록되지 않은 디바이스에서 접속했습니다.");
    }

    private void writeResponse(HttpServletResponse response, int code, String message) throws IOException {

        ResponseLoginDto responseBody = new ResponseLoginDto();
        responseBody.setCode(code);
        responseBody.setMessage(message);

        response.setStatus(code);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(responseBody));
    }

    private RequestLoginDto extractRequestLoginDto(HttpServletRequest request){

        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String str;

            while((str = br.readLine()) != null){
                stringBuilder.append(str);
            }
            return objectMapper.readValue(stringBuilder.toString(), RequestLoginDto.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
