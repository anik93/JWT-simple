package com.anik.service.impl;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.anik.domain.User;
import com.anik.repository.RoleRepository;
import com.anik.repository.UserRepository;
import com.anik.security.auth.JwtAuthenticationRequest;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null)
			throw new UsernameNotFoundException("No user found with username " + username);
		else
			return user;

	}

	public void changePassword(String oldPassword, String newPassword) {
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		String username = currentUser.getName();
		if (authenticationManager != null)
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, oldPassword));
		else
			return;
		User user = (User) loadUserByUsername(username);
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
	}

	public void register(JwtAuthenticationRequest newUser) {
		User user = new User();
		user.setUsername(newUser.getUsername());
		user.setPassword(passwordEncoder.encode(newUser.getPassword()));
		user.setEnabled(true);
		user.setAuthorities(Arrays.asList(roleRepository.findOne(1L)));
		userRepository.save(user);
	}
}
