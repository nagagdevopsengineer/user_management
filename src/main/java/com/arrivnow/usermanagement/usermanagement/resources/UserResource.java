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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.arrivnow.usermanagement.usermanagement.dto.OtpDTO;
import com.arrivnow.usermanagement.usermanagement.dto.PasswordChangeDTO;
import com.arrivnow.usermanagement.usermanagement.dto.ResetPassword;
import com.arrivnow.usermanagement.usermanagement.dto.ResponseMessage;
import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.exception.InvalidPasswordException;
import com.arrivnow.usermanagement.usermanagement.resources.vm.AuthenticateVM;
import com.arrivnow.usermanagement.usermanagement.resources.vm.KeyAndPasswordVM;
import com.arrivnow.usermanagement.usermanagement.resources.vm.LoginVM;
import com.arrivnow.usermanagement.usermanagement.resources.vm.ManagedUserVM;
import com.arrivnow.usermanagement.usermanagement.security.SecurityUtils;
import com.arrivnow.usermanagement.usermanagement.security.jwt.JWTFilter;
import com.arrivnow.usermanagement.usermanagement.security.jwt.JWTToken;
import com.arrivnow.usermanagement.usermanagement.security.jwt.TokenProvider;
import com.arrivnow.usermanagement.usermanagement.service.UserService;
import com.arrivnow.usermanagement.usermanagement.service.impl.MailService;

@RestController
@RequestMapping("users")
public class UserResource {
	
	 private final Logger log = LoggerFactory.getLogger(UserResource.class);
	
	 private final UserService userService;

	 private final MailService mailService;
	 
	 private final TokenProvider tokenProvider;

	 private final AuthenticationManagerBuilder authenticationManagerBuilder;

	 private static class AccountResourceException extends RuntimeException {
	        private AccountResourceException(String message) {
	            super(message);
	        }
	    }
	 
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
        public ResponseEntity<AuthenticateVM> authorize(@Valid @RequestBody LoginVM loginVM) {

            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginVM.getUsername(), loginVM.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            boolean rememberMe = (loginVM.isRememberMe() == null) ? false : loginVM.isRememberMe();
            String jwt = tokenProvider.createToken(authentication, rememberMe);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
            UserDTO user = userService.findOneByLogin(loginVM.getUsername());
            return new ResponseEntity<>(new AuthenticateVM(new JWTToken(jwt),
            		user.getUserId(),user.getAuthorities(),true) , httpHeaders, HttpStatus.OK);
        }
        
        @PostMapping("/sendOTP")
        public ResponseEntity<OtpDTO>  generateOTP(@RequestBody OtpDTO otp) throws Exception {
        	
            otp =	mailService.generateAndSendOTP(otp);
        	
        		
        	
        	return new ResponseEntity<>(otp, HttpStatus.OK);
        }
        
        @PostMapping("/validateOTP")
        public ResponseEntity<AuthenticateVM>  validateOTP(@RequestBody OtpDTO otp) {
        	//String userLogin = SecurityUtils.getCurrentUserLogin().get();
            try {
            	
            	UserDTO useDTO = userService.findByMobile(otp.getMobile());
            	boolean isRememberMe = false;
				otp =	userService.validateOTP(otp,useDTO);
				
				if(otp.isOtpValidated()) {
					
			            return new ResponseEntity<>(new AuthenticateVM(null,
			            		useDTO.getUserId(),useDTO.getAuthorities(),true) , null, HttpStatus.OK);
					
				}else {
					otp.setOtpValidated(false);
					return new ResponseEntity<>(new AuthenticateVM(null,
		            		null,null,false) , null, HttpStatus.NOT_ACCEPTABLE);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				otp = new OtpDTO();
				otp.setOtpValidated(false);
				return new ResponseEntity<>(new AuthenticateVM(null,
	            		null,null,false) , null, HttpStatus.NOT_ACCEPTABLE);
			}
        	
        	//return null;
        }
        
        /**
         * {@code GET  /currentuser} : current user .
         *
         
         * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be found.
         */
        @GetMapping("/currentuser")
        public ResponseEntity<UserDTO> getUser() {
        	
        	String userLogin = SecurityUtils.getCurrentUserLogin().get();
            System.out.println("REST request to get User : {}"+ userLogin);
            UserDTO userDTO = userService.getUserWithAuthoritiesByLogin(userLogin);
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        
        
        /**
         * {@code GET  /activate} : activate the registered user.
         *
         * @param key the activation key.
         * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
         */
        @GetMapping("/activate")
        public void activateAccount(@RequestParam(value = "key") String key) {
        	
        	log.debug(" Activating user with key  ");
        	
            UserDTO user = userService.activateRegistration(key);
            if (user != null && user.getId() > 0) {
                throw new AccountResourceException("No user was found for this activation key");
            }
        }
        
        
        /**
         * {@code POST  /change-password} : changes the current user's password.
         *
         * @param passwordChangeDto current and new password.
         * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
         */
        @PostMapping(path = "change-password")
        public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
            if (!checkPasswordLength(passwordChangeDto.getNewPassword())) {
                throw new InvalidPasswordException();
            }
            userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
        }
        
        
        /**
         * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
         *
         * @param mail the mail of the user.
         */
        @PostMapping(path = "/account/reset-password/init")
        public ResponseEntity<ResponseMessage> requestPasswordReset(@RequestBody ResetPassword resetPassword) {
           UserDTO user = userService.requestPasswordReset(resetPassword.getMail());
            ResponseMessage rm = new ResponseMessage();
            if (user != null && user.getId() > 0) {
            	//UserDTO existingUser = userService.findOneByEmailIgnoreCase(resetPassword.getMail());
                try {
					mailService.sendPasswordResetMail(user);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                rm.setResult(true);
                rm.setMessage(" Password reset email sent to "+resetPassword.getMail());
            } else {
                // Pretend the request has been successful to prevent checking which emails really exist
                // but log that an invalid attempt has been made
                log.warn("Password reset requested for non existing mail");
                rm.setResult(false);
                rm.setMessage(" Password reset requested for non existing mail "+resetPassword.getMail());
            }
            
            return ResponseEntity.ok(rm);
                    
        }

        /**
         * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
         *
         * @param keyAndPassword the generated key and the new password.
         * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
         * @throws RuntimeException {@code 500 (Internal Server Error)} if the password could not be reset.
         */
        @PostMapping(path = "/account/reset-password/finish")
        public ResponseEntity<ResponseMessage>  finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        	 ResponseMessage rm = new ResponseMessage();
            if (!checkPasswordLength(keyAndPassword.getNewPassword())) {
            	rm.setMessage("Invalid  Password  ");
                rm.setResult(false);
               // throw new InvalidPasswordException();
            }
            UserDTO user =
                userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());
            rm.setMessage("Password reset done, please login ");
            rm.setResult(true);
            if (user != null && user.getId() > 0) {
            	 rm.setMessage("No user was found for this reset key");
                 rm.setResult(false);
               // throw new AccountResourceException("No user was found for this reset key");
            }
            
            return ResponseEntity.ok(rm);
        }

        
        
        
        

}
