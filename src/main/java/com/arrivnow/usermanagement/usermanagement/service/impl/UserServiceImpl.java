package com.arrivnow.usermanagement.usermanagement.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.model.User;
import com.arrivnow.usermanagement.usermanagement.repository.UserRepository;
import com.arrivnow.usermanagement.usermanagement.service.UserService;

@Service
public class UserServiceImpl  implements UserService{

	
    private final UserRepository userRepository;
    
    
    @Autowired
    public UserServiceImpl(
    		UserRepository userRepository) {
        this.userRepository = userRepository;
    }    

	@Override
	public UserDTO findById(Long id) {
		User user = this.userRepository
                .findById(id).get();

        return new UserDTO(user);
	}

	@Override
	public List<UserDTO> findAll() {
		return this.userRepository
                .findAll()
                .stream()
                .map(user -> new UserDTO(user))
                .collect(Collectors.toList());
	}

}
