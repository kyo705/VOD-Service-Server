package com.ktube.vod.user;

public interface UserRepository {

    User create(User user);
    User findById(Long id);
    User findByEmail(String email);
    User update(User user);
    User delete(Long id);

}
