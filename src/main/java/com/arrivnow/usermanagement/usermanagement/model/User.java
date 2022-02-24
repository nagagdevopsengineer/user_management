package com.arrivnow.usermanagement.usermanagement.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
	
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    
	    @Column(name = "login")
	    private String login;
	    
	    @Column(name = "password_hash")
	    private String passwordHash;
	    
	    @Column(name = "first_name")
	    private String firstName;
	    
	    @Column(name = "last_name")
	    private String lastName;
	    
	    @Column(name = "email")
	    private String email;
	    
	    @Column(name = "image_url")
	    private String imageUrl;
	    
	    @Column(name = "activated")
	    private Boolean activated;
	    
	    @Column(name = "lang_key")
	    private String langKey;
	    
	    @Column(name = "activation_key")
	    private String activationKey;
	    
	    @Column(name = "reset_key")
	    private String resetKey;
	    
	    @Column(name = "reset_date")
	    private Date resetDate;
	    
	    @Column(name = "created_date")
	    private Date createdDate;
	    
	    

}
