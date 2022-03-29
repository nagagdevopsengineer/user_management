package com.arrivnow.usermanagement.usermanagement.service.impl;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arrivnow.usermanagement.usermanagement.dto.OtpDTO;
import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.exception.EmailAlreadyUsedException;
import com.arrivnow.usermanagement.usermanagement.exception.InvalidPasswordException;
import com.arrivnow.usermanagement.usermanagement.exception.UsernameAlreadyUsedException;
import com.arrivnow.usermanagement.usermanagement.model.Authority;
import com.arrivnow.usermanagement.usermanagement.model.User;
import com.arrivnow.usermanagement.usermanagement.repository.AuthorityRepository;
import com.arrivnow.usermanagement.usermanagement.repository.UserRepository;
import com.arrivnow.usermanagement.usermanagement.resources.errors.LoginAlreadyUsedException;
import com.arrivnow.usermanagement.usermanagement.resources.vm.ManagedUserVM;
import com.arrivnow.usermanagement.usermanagement.security.SecurityUtils;
import com.arrivnow.usermanagement.usermanagement.service.UserService;
import com.arrivnow.usermanagement.usermanagement.util.PasswordUtil;
import com.arrivnow.usermanagement.usermanagement.util.RandomUtil;





@Service
public class UserServiceImpl  implements UserService{

	
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthorityRepository authorityRepository;
    
    private final  ModelMapper modelMapper;
    
    @Autowired
    public UserServiceImpl(
    		UserRepository userRepository,PasswordEncoder passwordEncoder,
    		AuthorityRepository authorityRepository,ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.modelMapper = modelMapper;
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
	
	
	
	@Transactional
    public UserDTO registerUser(UserDTO userDTO, String password) {
         userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .flatMap(existingUser -> {
                if (!existingUser.isActivated()) {
                	 userRepository.delete(existingUser);
                    throw new UsernameAlreadyUsedException(); 
                } else {
                	 throw new UsernameAlreadyUsedException(); 
                }
            });
           userRepository.findOneByEmailIgnoreCase(userDTO.getEmail())
            .flatMap(existingUser -> {
                if (!existingUser.isActivated()) {
                     userRepository.delete(existingUser);
                     throw new EmailAlreadyUsedException();
                } else {
                   throw new EmailAlreadyUsedException();
                }
            });
           
           
                    User newUser = new User();
                    String encryptedPassword = "";
                    
                    if(password != null && password.equalsIgnoreCase("TEMP")) {
                    	password = PasswordUtil.generatePassayPassword();
                    	encryptedPassword = passwordEncoder.encode(password);
                    	System.out.println( " Setting genersted password  "+password );
                    }else {
                    	System.out.println( " Setting request password  " +password);
                    	encryptedPassword = passwordEncoder.encode(password);
                    }
                    
                    
                    newUser.setLogin(userDTO.getLogin().toLowerCase());
                    // new user gets initially a generated password
                    newUser.setPassword(encryptedPassword);
                    newUser.setFirstName(userDTO.getFirstName());
                    newUser.setLastName(userDTO.getLastName());
                    if (userDTO.getEmail() != null) {
                        newUser.setEmail(userDTO.getEmail().toLowerCase());
                    }
                    newUser.setImageUrl(userDTO.getImageUrl());
                    newUser.setLangKey(userDTO.getLangKey());
                    // new user is not active
                    newUser.setActivated(true);
                    // new user gets registration key
                    newUser.setActivationKey(RandomUtil.generateActivationKey());
                    newUser.setMobile(userDTO.getMobile());
                    
                    newUser.setUserId(UUID.randomUUID());
                    HashSet<Authority> authorities = new HashSet<>();
                    
                    for (String authority : userDTO.getAuthorities()) {
                    	 authorityRepository
                         .findById(authority)
                         .map(authorities::add);
					}
                    
                   
                    

                    newUser.setAuthorities(authorities );  
                    
                    userRepository.save(newUser);
                    
                    UserDTO userD = new UserDTO(newUser);
                    
                    userD.setPasswrd(password);
                    
                    return  userD;
                    
           
    }
	
	
	 @Transactional
	    public UserDTO activateRegistration(String key) {
	        User user = userRepository
	            .findOneByActivationKey(key);
	            
	        
	        user.setActivated(true);
            user.setActivationKey(null);
	        
	        user =  userRepository.save(user);
	        
	        return  new UserDTO(user);
	            
	    }

	 
	 @Transactional
	    public UserDTO completePasswordReset(String newPassword, String key) {
	      User user =   userRepository
	            .findOneByResetKey(key);
	      user.setPassword(passwordEncoder.encode(newPassword));
          user.setResetKey(null);
          user.setResetDate(null); 
	          
	            
	            user = userRepository.save(user);
	            
	            return new UserDTO(user);
	    }
	 
	 
	 @Transactional
	    public UserDTO requestPasswordReset(String mail) {
	      User user =  userRepository
	            .findOneByEmailIgnoreCase(mail.trim()).get();
	            
	                user.setResetKey(RandomUtil.generateResetKey());
	                user.setResetDate(Instant.now());
	                user = userRepository.save(user);
	                return new UserDTO(user);
	    }
	 
	 
	 @Transactional
	    public UserDTO createUser(UserDTO userDTO) {
	        User user = new User();
	        user.setLogin(userDTO.getLogin().toLowerCase());
	        user.setFirstName(userDTO.getFirstName());
	        user.setLastName(userDTO.getLastName());
	        if (userDTO.getEmail() != null) {
	            user.setEmail(userDTO.getEmail().toLowerCase());
	        }
	        user.setImageUrl(userDTO.getImageUrl());
	        if (userDTO.getLangKey() == null) {
	            user.setLangKey(com.arrivnow.usermanagement.usermanagement.config.Constants.DEFAULT_LANGUAGE); // default language
	        } else {
	            user.setLangKey(userDTO.getLangKey());
	        }
	        
	        HashSet<Authority> authorities = new HashSet<>();
	        
           for (String autho :  userDTO.getAuthorities()) {
 	        	
 	        	Authority auth = authorityRepository.findById(autho).get();
 				
 	        	authorities.add(auth);
 			}
	        
	        user.setAuthorities(authorities);
	        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
	        user.setPassword(encryptedPassword);
	        user.setResetKey(RandomUtil.generateResetKey());
	        user.setResetDate(Instant.now());
	        user.setActivated(true);

	       user =  userRepository.save(user);
	        
	        
	       return new UserDTO(user);
	    }
	 
	 
	 /**
	     * Update all information for a specific user, and return the modified user.
	     *
	     * @param userDTO user to update.
	     * @return updated user.
	     */
	    @Transactional
	    public UserDTO updateUser(UserDTO userDTO) {
	       User user =   userRepository
	            .findById(userDTO.getId()).get();
	         
	         user.setLogin(userDTO.getLogin().toLowerCase());
             user.setFirstName(userDTO.getFirstName());
             user.setLastName(userDTO.getLastName());
             if (userDTO.getEmail() != null) {
                 user.setEmail(userDTO.getEmail().toLowerCase());
             }
             user.setImageUrl(userDTO.getImageUrl());
             user.setActivated(userDTO.isActivated());
             user.setLangKey(userDTO.getLangKey());
             user.setOtp(userDTO.getOtp());
             user.setMobile(userDTO.getMobile());
             Set<Authority> managedAuthorities = user.getAuthorities();
             managedAuthorities.clear();
	         
            // userRepository
             //.deleteUserAuthorities(user.getId());
             
             HashSet<Authority> authorities = new HashSet<>();
 	        
 	        for (String autho :  userDTO.getAuthorities()) {
 	        	
 	        	Authority auth = authorityRepository.findById(autho).get();
 				
 	        	authorities.add(auth);
 			}
 	        
 	        user.setAuthorities(authorities);
 	        
 	        userRepository.save(user);
             
             return new UserDTO(user);
	    }
	    
	    @Transactional
	    public void deleteUser(String login) {
	        User user =  userRepository
	            .findOneByLogin(login).get();
	           
	            userRepository.delete(user);
	    }
	    
	    /**
	     * Update basic information (first name, last name, email, language) for the current user.
	     *
	     * @param firstName first name of user.
	     * @param lastName  last name of user.
	     * @param email     email id of user.
	     * @param langKey   language key.
	     * @param imageUrl  image URL of user.
	     */
	    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
	        SecurityUtils.getCurrentUserLogin()
	            .flatMap(userRepository::findOneByLogin)
	            .ifPresent(user -> {
	                user.setFirstName(firstName);
	                user.setLastName(lastName);
	                if (email != null) {
	                    user.setEmail(email.toLowerCase());
	                }
	                user.setLangKey(langKey);
	                user.setImageUrl(imageUrl);
	            });
	    }

	    
	    @Transactional
	    public void changePassword(String currentClearTextPassword, String newPassword) {
	        SecurityUtils.getCurrentUserLogin()
	            .flatMap(userRepository::findOneByLogin)
	            .ifPresent(user -> {
	                String currentEncryptedPassword = user.getPassword();
	                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
	                    throw new InvalidPasswordException();
	                }
	                String encryptedPassword = passwordEncoder.encode(newPassword);
	                user.setPassword(encryptedPassword);
	            });
	    }
	    
	  
	  
	    /**
	     * Not activated users should be automatically deleted after 3 days.
	     * <p>
	     * This is scheduled to get fired everyday, at 01:00 (am).
	     */
	    
	    /**
	    @Scheduled(cron = "0 0 1 * * ?")
	    public void removeNotActivatedUsers() {
	        userRepository
	            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
	            .forEach(user -> {
	                log.debug("Deleting not activated user {}", user.getLogin());
	                userRepository.delete(user);
	            });
	    }

        **/
	    /**
	     * Gets a list of all the authorities.
	     * @return a list of all the authorities.
	     */
	    @Transactional(readOnly = true)
	    public List<String> getAuthorities() {
	        return authorityRepository.findAll().stream().map(Authority::getName).collect(Collectors.toList());
	    }

		@Override
		public UserDTO getUserWithAuthoritiesByLogin(String userLogin) {
			User user = userRepository.findOneByLogin(userLogin).get();
			
			
			return new UserDTO(user);
		}

		@Override
		public UserDTO findByMobile(Long mobile) {
			User user = userRepository.findOneWithAuthoritiesByMobile(mobile).get();
			return new UserDTO(user);
		}

		@Override
		public UserDTO findOneByLogin(String userLogin) {
			User user = userRepository.findOneByLogin(userLogin).get();
			return  new UserDTO(user);
		}

		@Override
		public OtpDTO validateOTP(OtpDTO otp,UserDTO userDto) throws Exception {
			if((userDto.getMobile().equals(otp.getMobile())) && (userDto.getOtp().equals(otp.getOtp()))) {
				otp.setOtpValidated(true);
				User user  = userRepository.findById(userDto.getId()).get();
				user.setOtp(null);
				userRepository.save(user);
				
			}else {
				otp.setOtpValidated(false);
				throw new Exception("Otp not matched");
				
				
			}
			return otp;
		}

		@Override
		public Boolean checkUserExist(ManagedUserVM managedUserVM) {
			List<User> currentUsers = userRepository.findByEmailOrMobile(managedUserVM.getEmail(),managedUserVM.getMobile());
			
			if(currentUsers != null && currentUsers.size() > 0 ) {
				return true;
			}
			
			return false;
		}
	    
	    
	    
	 

}
