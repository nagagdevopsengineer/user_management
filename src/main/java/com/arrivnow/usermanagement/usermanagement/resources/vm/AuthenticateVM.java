package com.arrivnow.usermanagement.usermanagement.resources.vm;

import java.util.Set;

import com.arrivnow.usermanagement.usermanagement.security.jwt.JWTToken;

public class AuthenticateVM {
	
	private JWTToken token;
	
	private String userId;
	
	private Set<String> roles;
	
	
	public AuthenticateVM(JWTToken token,String userId,Set<String> roles){
		
		this.token = token;
		this.userId = userId;
		this.roles = roles;
		
	}
	
	public JWTToken getToken() {
		return token;
	}
	public void setToken(JWTToken token) {
		this.token = token;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	

}
