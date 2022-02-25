package com.arrivnow.usermanagement.usermanagement.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.exception.EmailAlreadyUsedException;
import com.arrivnow.usermanagement.usermanagement.exception.UsernameAlreadyUsedException;
import com.arrivnow.usermanagement.usermanagement.model.Authority;
import com.arrivnow.usermanagement.usermanagement.model.User;
import com.arrivnow.usermanagement.usermanagement.repository.AuthorityRepository;
import com.arrivnow.usermanagement.usermanagement.repository.UserRepository;
import com.arrivnow.usermanagement.usermanagement.security.AuthoritiesConstants;
import com.arrivnow.usermanagement.usermanagement.service.UserService;
import com.arrivnow.usermanagement.usermanagement.util.RandomUtil;


@Service
public class UserServiceImpl  implements UserService{

	
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AuthorityRepository authorityRepository;
    @Autowired
    public UserServiceImpl(
    		UserRepository userRepository,PasswordEncoder passwordEncoder,
    		AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
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
    public void registerUser(UserDTO userDTO, String password) {
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
                    String encryptedPassword = passwordEncoder.encode(password);
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
                    newUser.setActivated(false);
                    // new user gets registration key
                    newUser.setActivationKey(RandomUtil.generateActivationKey());
                    
                    
                   
                    Set<Authority> authorities = new HashSet<>();
                    authorityRepository
                        .findById(AuthoritiesConstants.USER)
                        .map(authorities::add);
                    

                    newUser.setAuthorities(authorities );  
                    
                    userRepository.save(newUser);
                    
           
    }


}
