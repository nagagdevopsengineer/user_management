package com.arrivnow.usermanagement.usermanagement.dto;

import java.util.Date;

import org.springframework.beans.BeanUtils;

import com.arrivnow.usermanagement.usermanagement.model.User;


public class UserDTO {

    private Long id;
    
    private String login;
    
    private String passwordHash;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String imageUrl;
    
    private Boolean activated;
    
    private String langKey;
    
    private String activationKey;
    
    private String resetKey;
    
    private Date resetDate;
    
    private Date createdDate;
    
    public UserDTO(User user) {
        BeanUtils.copyProperties(user, this);
    }
	
}
