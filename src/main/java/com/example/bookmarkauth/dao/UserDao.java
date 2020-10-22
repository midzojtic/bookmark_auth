package com.example.bookmarkauth.dao;

import com.example.bookmarkauth.model.User;

public interface UserDao {

    long insertUser(User user);

    long insertUserUserType(long user_id, String userType);

    User getUserByUsername(String username);

}
