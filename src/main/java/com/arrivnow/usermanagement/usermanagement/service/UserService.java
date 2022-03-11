package com.arrivnow.usermanagement.usermanagement.service;

import java.util.List;

import com.arrivnow.usermanagement.usermanagement.dto.OtpDTO;
import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;

public interface UserService {
	
	  UserDTO findById(Long id);

	  List<UserDTO> findAll();
	  
	  public UserDTO registerUser(UserDTO userDTO, String password) ;
	  
	  public UserDTO activateRegistration(String key);
	  
	  public UserDTO completePasswordReset(String newPassword, String key);
	  
	  public UserDTO requestPasswordReset(String mail);
	  
	  public UserDTO createUser(UserDTO userDTO) ;
	  
	  public UserDTO updateUser(UserDTO userDTO);
	  
	  public void deleteUser(String login);
	  
	  public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl);
	  
	  public void changePassword(String currentClearTextPassword, String newPassword) ;

	  public UserDTO getUserWithAuthoritiesByLogin(String userLogin);

	  UserDTO findByMobile(Long mobile);

	  UserDTO findOneByLogin(String userLogin);

	  OtpDTO validateOTP(OtpDTO otp, UserDTO useDTO) throws Exception ;
	  
	  

}
