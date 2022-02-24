package com.arrivnow.usermanagement.usermanagement.service;

import java.util.List;

import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;

public interface UserService {
	
	 UserDTO findById(Long id);

	List<UserDTO> findAll();

}
