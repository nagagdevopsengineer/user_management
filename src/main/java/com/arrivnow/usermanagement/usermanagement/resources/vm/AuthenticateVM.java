package com.arrivnow.usermanagement.usermanagement.resources.vm;

import java.util.Set;

import com.arrivnow.usermanagement.usermanagement.security.jwt.JWTToken;

public class AuthenticateVM {
	
	private JWTToken token;
	
	private String userId;
	
	private Set<String> roles;
	
	private boolean otpValidated;
	
	
	public AuthenticateVM(JWTToken token,String userId,Set<String> roles,boolean otpValidated){
		
		this.token = token;
		this.userId = userId;
		this.roles = roles;
		this.otpValidated = otpValidated;
		
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

	public boolean isOtpValidated() {
		return otpValidated;
	}

	public void setOtpValidated(boolean otpValidated) {
		this.otpValidated = otpValidated;
	}
	
	

}
