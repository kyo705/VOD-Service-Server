package com.ktube.vod.user.basic;

public interface UserRepository {

    void create(KTubeUser user);
    KTubeUser findById(Long id);
    KTubeUser findByEmail(String email);
    KTubeUser update(long userId, RequestUserUpdateDto requestParam);
    KTubeUser delete(Long id);

}
