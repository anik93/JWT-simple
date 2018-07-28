package com.anik.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anik.common.DeviceProvider;
import com.anik.domain.User;
import com.anik.domain.UserTokenState;
import com.anik.security.auth.JwtAuthenticationRequest;
import com.anik.security.auth.TokenHelper;
import com.anik.service.impl.CustomUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	@Autowired
	private TokenHelper tokenHelper;

	@Lazy
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private DeviceProvider deviceProvider;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			HttpServletResponse response, Device device) throws AuthenticationException, IOException {
		final Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
						authenticationRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User user = (User) authentication.getPrincipal();
		String jws = tokenHelper.generateToken(user.getUsername(), device);
		int expiresIn = tokenHelper.getExpiredIn(device);
		return ResponseEntity.ok(new UserTokenState(jws, expiresIn));
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.POST)
	public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request, HttpServletResponse response,
			Principal principal) {
		String authToken = tokenHelper.getToken(request);
		Device device = deviceProvider.getCurrentDevice(request);
		if (authToken != null && principal != null) {
			String refreshedToken = tokenHelper.refreshToken(authToken, device);
			int expiresIn = tokenHelper.getExpiredIn(device);
			return ResponseEntity.ok(new UserTokenState(refreshedToken, expiresIn));
		} else {
			UserTokenState userTokenState = new UserTokenState();
			return ResponseEntity.accepted().body(userTokenState);
		}
	}

	@RequestMapping(value = "/change-password", method = RequestMethod.POST)
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwordChanger) {
		userDetailsService.changePassword(passwordChanger.get("oldPassword"), passwordChanger.get("newPassword"));
		Map<String, String> result = new HashMap<>();
		result.put("result", "success");
		return ResponseEntity.accepted().body(result);
	}

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<?> registerUser(@RequestBody JwtAuthenticationRequest newUser) {
		userDetailsService.register(newUser);
		return ResponseEntity.accepted().body("");
	}
}