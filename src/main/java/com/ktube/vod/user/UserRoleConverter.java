package com.ktube.vod.user;

import javax.persistence.AttributeConverter;

public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserRole attribute) {

        return attribute.getCode();
    }

    @Override
    public UserRole convertToEntityAttribute(Integer dbData) {

        return UserRole.valueOfCode(dbData);
    }
}
