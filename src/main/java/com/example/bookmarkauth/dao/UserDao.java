package com.example.bookmarkauth.dao;

import com.example.bookmarkauth.model.User;

public interface UserDao {

    long insertUser(User user);

    User getUserByUsername(String username);

}
