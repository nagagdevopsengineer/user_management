package com.arrivnow.usermanagement.usermanagement.dto;

import lombok.Data;

@Data
public class OtpDTO {
    private Long mobile;
	private Long otp;
	private String app;
	private String messageId;
	private String userLogin;
	private boolean otpValidated;
	

}
