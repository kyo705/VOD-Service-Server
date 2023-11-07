package com.ktube.vod.security.login;

import com.ktube.vod.user.KTubeUser;
import com.ktube.vod.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class KTubeLoginUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        KTubeUser user = userRepository.findByEmail(email);

        if(user == null) return null;

        return new KTubeUserDetails(user);
    }
}
