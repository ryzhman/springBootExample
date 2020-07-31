package com.go2it.edu.service;

import com.go2it.edu.entity.User;

import java.util.List;

public interface IUserService {
    void save(User newUser);

    List<User> getAll();
}
