package com.anik.service;

import java.util.List;

import com.anik.domain.User;

public interface UserService {

	User findByUsername(String username);

	List<User> findAll();
}
