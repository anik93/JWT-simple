package com.anik.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anik.domain.User;
import com.anik.service.UserService;

import java.security.Principal;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

	@Autowired
	private UserService userService;

	@RequestMapping(method = GET, value = "/user/all")
	@PreAuthorize("hasRole('ADMIN')")
	public List<User> loadAll() {
		return userService.findAll();
	}

	@RequestMapping("/user")
	@PreAuthorize("hasRole('USER')")
	public User user(Principal user) {
		return userService.findByUsername(user.getName());
	}
}
