package com.arrivnow.usermanagement.usermanagement.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.service.UserService;


@RestController
@RequestMapping("user")
public class UserResource {
	
	 private final UserService userService;

	    @Autowired
	    public UserResource(UserService userService) {
	        this.userService = userService;
	    }
	    
	    
	    @GetMapping("{id}")
	    public UserDTO findById(@PathVariable Long id) {
	        return this.userService.findById(id);
	    }
	    
	    @GetMapping
	    public List<UserDTO> findAll() {
	        return this.userService.findAll();
	    }

}
