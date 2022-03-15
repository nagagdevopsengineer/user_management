package com.arrivnow.usermanagement.usermanagement.dto;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.arrivnow.usermanagement.usermanagement.model.Authority;
import com.arrivnow.usermanagement.usermanagement.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class UserDTO {

    @JsonIgnore
    private Long id;
    
    private String userId;
    
    private String login;
    
    @JsonIgnore
    private String passwrd;
    
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
    
    private String webURL;
    
    private Set<String> authorities;
    
    private Long otp;
    
    private Long mobile;
    
    public UserDTO(User user) {
    	
    	 System.out.println(" user aut "+user.getAuthorities());
    	 Set<String> authorities =  new HashSet<String>(); 
         for (Authority auth : user.getAuthorities()) {
        	 authorities.add(auth.getName());
         	
 		}
         this.setAuthorities(authorities);
        BeanUtils.copyProperties(user, this);
        this.setUserId(user.getUserId().toString());
        
       
       
    }
    
    public UserDTO() {
        // Empty constructor needed for Jackson.
    }
	
}
