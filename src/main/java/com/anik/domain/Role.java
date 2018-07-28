package com.anik.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "ROLE")
public class Role implements GrantedAuthority {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "name")
	private UserRoleName name;

	@Override
	public String getAuthority() {
		return name.name();
	}

	public Role() {
	}

	public Role(UserRoleName roleName) {
		this.name = roleName;
	}

	public void setName(UserRoleName name) {
		this.name = name;
	}

	@JsonIgnore
	public UserRoleName getName() {
		return name;
	}

	@JsonIgnore
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Authority [id=" + id + ", name=" + name + "]";
	}

}
