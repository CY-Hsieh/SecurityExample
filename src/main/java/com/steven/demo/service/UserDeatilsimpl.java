package com.steven.demo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.steven.demo.model.Role;
import com.steven.demo.model.User;

public class UserDeatilsimpl implements UserDetails {

	private static final long serialVersionUID = -2784842795551452015L;

	private User user;

	public UserDeatilsimpl(User user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		System.out.println("getAuthorities() input: " + ToStringBuilder.reflectionToString(user));
		List<Role> roles = user.getRoles();
		List<GrantedAuthority> authorities = new ArrayList<>();
		
		if (roles.size() >= 1) {
			for (Role role : roles) {
				
				authorities.add(new SimpleGrantedAuthority(role.getName()));
				System.out.println("user roles: " + role.getName());
			}
			return authorities;
		}
		return AuthorityUtils.commaSeparatedStringToAuthorityList("");
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUserName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
