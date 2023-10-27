package com.ktube.vod.user;

import java.net.URI;

public class UserConstants {

    // 영어 대소문자, 숫자, 특수 문자가 반드시 하나 이상 포함 되어야 함.
    public static final String PASSWORD_REGEX = "^(?=.*[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z])(?=.*\\d)(?=.*[!~<>,;:_=?*+#.\"'&§%°()\\|\\[\\]\\-\\$\\^\\@\\/])[ㄱ-ㅎㅏ-ㅣ가-힣A-Za-z\\d!~<>,;:_=?*+#.\"'&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{11,30}$";
    public static final String NICKNAME_REGEX = "[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z\\d\\s]{1,20}$";
    public static final String USER_URL = "/api/user";
}
