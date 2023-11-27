package com.ktube.vod.user;

import javax.persistence.AttributeConverter;

public class UserGradeDatabaseConverter implements AttributeConverter<UserGrade, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserGrade attribute) {

        return attribute.getCode();
    }

    @Override
    public UserGrade convertToEntityAttribute(Integer dbData) {

        return UserGrade.valueOfCode(dbData);
    }
}
