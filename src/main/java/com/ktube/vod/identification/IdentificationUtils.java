package com.ktube.vod.identification;

import java.util.UUID;

public class IdentificationUtils {

    public static String createIdentificationCode() {

        return UUID.randomUUID().toString().substring(0, 8);
    }
}
