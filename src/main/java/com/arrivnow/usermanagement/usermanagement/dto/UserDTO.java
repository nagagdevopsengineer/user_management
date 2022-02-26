package com.arrivnow.usermanagement.usermanagement.dto;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import com.arrivnow.usermanagement.usermanagement.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class UserDTO {

    @JsonIgnore
    private Long id;
    
    private UUID userId;
    
    private String login;
    
    @JsonIgnore
    private String password;
    
    private String firstName;
    
    private String lastName;
    
    private String email;
    
    private String imageUrl;
    
    private boolean activated;
    
    private String langKey;
    
    private String activationKey;
    
    private String resetKey;
    
    private Date resetDate;
    
    private Date createdDate;
    
    private Set<String> authorities;
    
    public UserDTO(User user) {
        BeanUtils.copyProperties(user, this);
    }
    
    public UserDTO() {
        // Empty constructor needed for Jackson.
    }
	
}
