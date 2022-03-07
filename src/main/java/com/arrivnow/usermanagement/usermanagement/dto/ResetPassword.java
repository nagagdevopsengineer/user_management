package com.arrivnow.usermanagement.usermanagement.dto;
import javax.validation.constraints.NotNull;

public class ResetPassword {

	@NotNull
	private String mail;

	public ResetPassword() {

	}

	public ResetPassword(String mail) {
		this.mail = mail;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

}
