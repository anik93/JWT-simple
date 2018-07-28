package com.anik.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anik.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByUsername(String username);
}
