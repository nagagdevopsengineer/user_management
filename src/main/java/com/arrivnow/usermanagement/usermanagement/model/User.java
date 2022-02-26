package com.arrivnow.usermanagement.usermanagement.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {
	
	
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    @JsonIgnore
	    private Long id;
	    
	    @NotNull
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "user_id")
	    private UUID userId;
	    
	    @Column(name = "login")
	    private String login;
	    
	    @JsonIgnore
	    @NotNull
	    @Size(min = 60, max = 60)
	    @Column(name="password_hash")
	    private String password;
	    
	    @Column(name = "first_name")
	    private String firstName;
	    
	    @Column(name = "last_name")
	    private String lastName;
	    
	    @Column(name = "email")
	    private String email;
	    
	    @Column(name = "image_url")
	    private String imageUrl;
	    
	    @Column(name = "activated")
	    private boolean activated;
	    
	    @Column(name = "lang_key")
	    private String langKey;
	    
	    @Column(name = "activation_key")
	    private String activationKey;
	    
	    @Column(name = "reset_key")
	    private String resetKey;
	    
	    @Column(name = "reset_date")
	    private Instant resetDate;
	    
	    @Column(name = "created_date")
	    private Instant createdDate;
	    
	    @JsonIgnore
	    @Transient
	    private Set<Authority> authorities = new HashSet<>();

}
