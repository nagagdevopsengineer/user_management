package com.arrivnow.usermanagement.usermanagement.service.impl;

import java.io.IOException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.arrivnow.usermanagement.usermanagement.dto.UserDTO;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;


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

    public MailService(
       // JHipsterProperties jHipsterProperties,
        //JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
       // this.jHipsterProperties = jHipsterProperties;
       // this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
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
}
