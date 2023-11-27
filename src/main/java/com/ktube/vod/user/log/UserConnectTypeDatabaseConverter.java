package com.ktube.vod.user.log;

import javax.persistence.AttributeConverter;

public class UserConnectTypeDatabaseConverter implements AttributeConverter<UserConnectType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserConnectType attribute) {

        return attribute.getCode();
    }

    @Override
    public UserConnectType convertToEntityAttribute(Integer code) {

        return UserConnectType.valueOfCode(code);
    }
}
