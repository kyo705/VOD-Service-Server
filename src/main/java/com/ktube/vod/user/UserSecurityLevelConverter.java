package com.ktube.vod.user;

import javax.persistence.AttributeConverter;

public class UserSecurityLevelConverter implements AttributeConverter<UserSecurityLevel, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserSecurityLevel attribute) {

        return attribute.getCode();
    }

    @Override
    public UserSecurityLevel convertToEntityAttribute(Integer dbData) {

        return UserSecurityLevel.valueOfCode(dbData);
    }
}
