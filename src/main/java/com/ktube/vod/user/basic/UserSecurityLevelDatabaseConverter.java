package com.ktube.vod.user.basic;

import javax.persistence.AttributeConverter;

public class UserSecurityLevelDatabaseConverter implements AttributeConverter<UserSecurityLevel, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserSecurityLevel attribute) {

        return attribute.getCode();
    }

    @Override
    public UserSecurityLevel convertToEntityAttribute(Integer dbData) {

        return UserSecurityLevel.valueOfCode(dbData);
    }
}
