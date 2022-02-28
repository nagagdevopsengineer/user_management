package com.arrivnow.usermanagement.usermanagement.resources;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.resources.vm.ManagedUserVM;
import com.arrivnow.usermanagement.usermanagement.service.UserService;
import com.arrivnow.usermanagement.usermanagement.service.impl.MailService;

@RestController
@RequestMapping("users")
public class UserResource {
	
	private final Logger log = LoggerFactory.getLogger(UserResource.class);
	
	 private final UserService userService;

	 private final MailService mailService;
	 
	 public UserResource(UserService userService,MailService mailService) {
	        this.userService = userService;
	        this.mailService = mailService;
	    }
	    
	    
	    @GetMapping("{id}")
	    public UserDTO findById(@PathVariable Long id) {
	        return this.userService.findById(id);
	    }
	    
	    @GetMapping
	    public List<UserDTO> findAll() {
	        return this.userService.findAll();
	    }
	    
	    @PostMapping("/register")
	    @ResponseStatus(HttpStatus.CREATED)
	    public void registerUser(@RequestBody ManagedUserVM managedUserVM) {
	    	
	    	System.out.println(managedUserVM.getFirstName()+" Password  "+managedUserVM.getPassword());
	    	//if (!checkPasswordLength(managedUserVM.getPassword())) {
	         //   throw new InvalidPasswordException();
	       // }
	        UserDTO user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
	        try {
				mailService.sendActivationEmail(user);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    
	    
        public UserDTO createUser(UserDTO user) {
	    	
	    	
	    	return this.userService.createUser(user);
	    }
        
        
        private static boolean checkPasswordLength(String password) {
            return !StringUtils.isEmpty(password) &&
                password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
                password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
        }
        
        
        

}
