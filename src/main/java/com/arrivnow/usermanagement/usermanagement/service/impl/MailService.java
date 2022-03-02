package com.arrivnow.usermanagement.usermanagement.service.impl;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.arrivnow.usermanagement.usermanagement.dto.OtpDTO;
import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.arrivnow.usermanagement.usermanagement.service.UserService;
import com.arrivnow.usermanagement.usermanagement.util.RandomUtil;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;


/**
 * Service for sending emails.
 * <p>
 * We use the {@link Async} annotation to send emails asynchronously.
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";


    //private final JavaMailSender javaMailSender;

    private final MessageSource messageSource;

    private final SpringTemplateEngine templateEngine;
    
    private final Environment env;
    
    private final UserService userService;

    public MailService(
       // JHipsterProperties jHipsterProperties,
        //JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine,
        Environment env,
        UserService userService
        
    ) {
       // this.jHipsterProperties = jHipsterProperties;
       // this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
        this.env = env;
        this.userService = userService;
    }

    @Async
    public void sendEmail(String toEmail, String subject, String contentStr, boolean isMultipart, boolean isHtml) throws IOException {
        log.debug(
            "Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart,
            isHtml,
            toEmail,
            subject,
            contentStr
        );

        // Prepare message using a Spring helper
       // MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
        	
        	Email from = new Email("techsupport@arrivnow.com");
        	Email to = new Email(toEmail);
        	Content content = new Content("text/html", contentStr);
            Mail mail = new Mail(from, subject, to, content);
            
            SendGrid sg = new SendGrid("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");
            Request request = new Request();
            try {
              request.setMethod(Method.POST);
              request.setEndpoint("mail/send");
              request.setBody(mail.build());
              Response response = sg.api(request);
              System.out.println(response.getStatusCode());
              System.out.println(response.getBody());
              System.out.println(response.getHeaders());
            } catch (IOException ex) {
              throw ex;
            }
            
            
            /**
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);**/
            log.debug("Sent email to User '{}'", to);
        } catch (MailException e) {
            log.warn("Email could not be sent to user '{}'", toEmail, e);
        }
    }

    @Async
    public void sendEmailFromTemplate(UserDTO user, String templateName, String titleKey) throws IOException {
        if (user.getEmail() == null) {
            log.debug("Email doesn't exist for user '{}'", user.getLogin());
            return;
        }
        Locale locale = Locale.forLanguageTag("en");
        Context context = new Context(locale);
        user.setWebURL("http://arrivnow.vapprtech.com");
        context.setVariable(USER, user);
        context.setVariable(BASE_URL, "http://arrivnow.vapprtech.com");
        String content = templateEngine.process(templateName, context);
        System.out.println("  Sending email  ==>> ");
       // String subject = messageSource.getMessage(titleKey, null, null);
        sendEmail(user.getEmail(), "ArriveNow Account Created !! ", content, false, true);
    }

    @Async
    public void sendActivationEmail(UserDTO user) throws IOException {
        log.debug("Sending activation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/activationEmail", "email.activation.title");
    }

    @Async
    public void sendCreationEmail(UserDTO user) throws IOException {
        log.debug("Sending creation email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/creationEmail", "email.activation.title");
    }

    @Async
    public void sendPasswordResetMail(UserDTO user) throws IOException {
        log.debug("Sending password reset email to '{}'", user.getEmail());
        sendEmailFromTemplate(user, "mail/passwordResetEmail", "email.reset.title");
    }

    @Async
	public OtpDTO generateAndSendOTP(OtpDTO otp) {
		log.debug(" Sending OTP to mobile ",otp);
		
		String OtpSMS = env.getProperty("otp.sms");
		
		UserDTO user = userService.findOneByLogin(otp.getUserLogin());
		
		if(user != null && user.getId() > 0) {
			char[] otpc = RandomUtil.generateOTP();
			String otps = new String(otpc);
			otp.setOtp(Long.parseLong(otps));
			otp.setMobile(user.getMobile());
			
			OtpSMS = OtpSMS.replace("USERNAME", user.getFirstName());
			OtpSMS = OtpSMS.replace("OTPN", otp.getOtp()+"");
			if("DRIVER".equalsIgnoreCase(otp.getApp())) {
				OtpSMS = OtpSMS.replace("AppHashKey", env.getProperty("otp.dapp.hash"));
			}else if ("EMPLOYEE".equalsIgnoreCase(otp.getApp())) {
				OtpSMS = OtpSMS.replace("AppHashKey", env.getProperty("otp.eapp.hash"));
			}
			
			
			otp = sendOTP(otp,OtpSMS);
			
			user.setOtp(otp.getOtp());
			
			userService.updateUser(user);
			
		}else {
			
			throw new UsernameNotFoundException("User not exist with given mobile.");
			
		}
		
		
		
		return otp;
	}

	private OtpDTO sendOTP(OtpDTO otp,String sms) {
		
		String mobile = otp.getMobile()+"";
		
		if(!mobile.startsWith("+91")) {
			
			mobile = "+91"+mobile;
		}
		
		
		Twilio.init(ACCOUNT_SID, AUTH_TOKEN); 
        Message message = Message.creator( 
                new com.twilio.type.PhoneNumber(mobile),  
                "MG24313a7e6ae8031b9d72a5c277bb228b", 
                sms)      
            .create(); 
        
        
        otp.setMessageId(message.getSid());
        
        return otp;
 
	}
	
	public static final String ACCOUNT_SID = "AC3f967b4e1f76a7e56162b630ee82cd37";
    public static final String AUTH_TOKEN = "eee79916f6e2a3deac2ea1a28a3dcddc";

		
		
    public static void main(String[] args) { 
    
    } 
}
