package com.ktube.vod.user.basic;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserSecurityLevelControllerConverter implements Converter<String, UserSecurityLevel> {

    @Override
    public UserSecurityLevel convert(String source) {

        int code = Integer.parseInt(source);

        return UserSecurityLevel.valueOfCode(code);
    }


}
