package com.naturalprogrammer.spring.lemon.security;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * Spring Security Principal, implementing both OidcUser, UserDetails
 */
public class LemonPrincipal<ID extends Serializable> implements OidcUser, UserDetails, CredentialsContainer {

	private static final long serialVersionUID = -7849730155307434535L;
	
	private UserDto<ID> userDto;
	
	private Map<String, Object> attributes;
	private String name;
	private Map<String, Object> claims;
	private OidcUserInfo userInfo;
	private OidcIdToken idToken;
	
	public LemonPrincipal(UserDto<ID> userDto) {

		this.userDto = userDto;
	}

	public UserDto<ID> currentUser() {
		return userDto;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Set<String> roles = userDto.getRoles();
		
		Collection<LemonGrantedAuthority> authorities = roles.stream()
				.map(role -> new LemonGrantedAuthority("ROLE_" + role))
				.collect(Collectors.toCollection(() ->
					new ArrayList<LemonGrantedAuthority>(roles.size() + 2))); 
		
		if (userDto.isGoodUser()) {
			
			authorities.add(new LemonGrantedAuthority("ROLE_"
					+ LemonSecurityConfig.GOOD_USER));
			
			if (userDto.isGoodAdmin())
				authorities.add(new LemonGrantedAuthority("ROLE_"
					+ LemonSecurityConfig.GOOD_ADMIN));
		}
		
		return authorities;	
	}

	@Override
	public Map<String, Object> getAttributes() {

		return attributes;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public Map<String, Object> getClaims() {

		return claims;
	}

	@Override
	public OidcUserInfo getUserInfo() {

		return userInfo;
	}

	@Override
	public OidcIdToken getIdToken() {

		return idToken;
	}

	// UserDetails ...

	@Override
	public String getPassword() {

		return userDto.getPassword();
	}

	@Override
	public String getUsername() {

		return userDto.getUsername();
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

	@Override
	public void eraseCredentials() {
		
		userDto.setPassword(null);
		attributes = null;
		claims = null;
		userInfo = null;
		idToken = null;
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setClaims(Map<String, Object> claims) {
		this.claims = claims;
	}

	public void setUserInfo(OidcUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public void setIdToken(OidcIdToken idToken) {
		this.idToken = idToken;
	}
}
