package com.arrivnow.usermanagement.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class OtpDTO {
	@JsonIgnore
    private Long mobile;
	private Long otp;
	private String app;
	private String messageId;
	private String userLogin;
	
	

}
