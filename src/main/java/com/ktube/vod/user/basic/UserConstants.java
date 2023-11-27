package com.ktube.vod.user.basic;

public class UserConstants {

    // 영어 대소문자, 숫자, 특수 문자가 반드시 하나 이상 포함 되어야 함.
    public static final String PASSWORD_REGEX = "^(?=.*[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z])(?=.*\\d)(?=.*[!~<>,;:_=?*+#.\"'&§%°()\\|\\[\\]\\-\\$\\^\\@\\/])[ㄱ-ㅎㅏ-ㅣ가-힣A-Za-z\\d!~<>,;:_=?*+#.\"'&§%°()\\|\\[\\]\\-\\$\\^\\@\\/]{11,30}$";
    public static final String NICKNAME_REGEX = "[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z\\d\\s]{1,20}$";
    public static final String USER_URL = "/api/user";
    public static final String SPECIFIC_USER_URL = "/api/user/{userId}";
    public static final String USER_CONNECT_LOG_URL = "/api/user/{userId}/connect-log";

    // 메세지
    public static final String ALREADY_EXISTING_EMAIL_MESSAGE = "이미 존재하는 이메일 계정입니다.";
    public static final String NOT_EXISTING_EMAIL_MESSAGE = "해당 이메일에 대한 계정이 존재하지 않습니다.";
}
