package com.anik.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anik.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
