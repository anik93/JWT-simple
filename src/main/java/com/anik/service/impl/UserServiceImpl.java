package com.anik.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.anik.domain.User;
import com.anik.repository.UserRepository;
import com.anik.service.UserService;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public User findByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username);
	}

	@Override
	public List<User> findAll() throws AccessDeniedException {
		return userRepository.findAll();
	}
}
