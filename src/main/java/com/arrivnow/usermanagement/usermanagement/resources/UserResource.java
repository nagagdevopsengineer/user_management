package com.arrivnow.usermanagement.usermanagement.resources;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arrivnow.usermanagement.usermanagement.dto.OtpDTO;
import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.resources.vm.LoginVM;
import com.arrivnow.usermanagement.usermanagement.resources.vm.ManagedUserVM;
import com.arrivnow.usermanagement.usermanagement.security.SecurityUtils;
import com.arrivnow.usermanagement.usermanagement.security.jwt.JWTFilter;
import com.arrivnow.usermanagement.usermanagement.security.jwt.TokenProvider;
import com.arrivnow.usermanagement.usermanagement.service.UserService;
import com.arrivnow.usermanagement.usermanagement.service.impl.MailService;
import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("users")
public class UserResource {
	
	 private final Logger log = LoggerFactory.getLogger(UserResource.class);
	
	 private final UserService userService;

	 private final MailService mailService;
	 
	 private final TokenProvider tokenProvider;

	 private final AuthenticationManagerBuilder authenticationManagerBuilder;

	 
	 public UserResource(UserService userService,MailService mailService
			 ,TokenProvider tokenProvider
			 ,AuthenticationManagerBuilder authenticationManagerBuilder) {
	        this.userService = userService;
	        this.mailService = mailService;
	        this.tokenProvider = tokenProvider;
	        this.authenticationManagerBuilder = authenticationManagerBuilder;
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
	    public ResponseEntity<UserDTO>  registerUser(@RequestBody ManagedUserVM managedUserVM) {
	    	
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
	        
	        return new ResponseEntity<>(user, HttpStatus.OK);
	    }
	    
	    
        public UserDTO createUser(UserDTO user) {
	    	
	    	
	    	return this.userService.createUser(user);
	    }
        
        
        private static boolean checkPasswordLength(String password) {
            return !StringUtils.isEmpty(password) &&
                password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
                password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
        }
        
        
        @PostMapping("/authenticate")
        public ResponseEntity<JWTToken> authorize(@Valid @RequestBody LoginVM loginVM) {

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
        }
        
        @PostMapping("/sendOTP")
        public ResponseEntity<OtpDTO>  generateOTP(@RequestBody OtpDTO otp) {
        	
         otp =	mailService.generateAndSendOTP(otp);
        	
        	return new ResponseEntity<>(otp, HttpStatus.OK);
        }
        
        
        @GetMapping("/currentuser")
        public ResponseEntity<UserDTO> getUser() {
        	
        	String userLogin = SecurityUtils.getCurrentUserLogin().get();
            System.out.println("REST request to get User : {}"+ userLogin);
            UserDTO userDTO = userService.getUserWithAuthoritiesByLogin(userLogin);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        
        
        /**
         * Object to return as body in JWT Authentication.
         */
        static class JWTToken {

            private String idToken;

            JWTToken(String idToken) {
                this.idToken = idToken;
            }

            @JsonProperty("id_token")
            String getIdToken() {
                return idToken;
            }

            void setIdToken(String idToken) {
                this.idToken = idToken;
            }
        }
        
        
        

}
