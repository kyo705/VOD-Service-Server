package com.ktube.vod.user.basic;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserGradeControllerConverter implements Converter<String, UserGrade> {

    @Override
    public UserGrade convert(String source) {

        int code = Integer.parseInt(source);

        return UserGrade.valueOfCode(code);
    }
}
