package com.hpms.userservice.service;

import com.hpms.commonlib.constants.EmailType;
import com.hpms.commonlib.dto.EmailEvent;
import com.hpms.userservice.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailService {
    private final EmailProducerService emailProducerService;


    @Value("${app.client-url}")
    private String clientUrl;

    public EmailService(EmailProducerService emailProducerService) {
        this.emailProducerService = emailProducerService;
    }

    public void sendWelcomeEmail(User user) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", user.getName());
        templateData.put("email", user.getUsername());

        EmailEvent emailEvent = new EmailEvent(
                user.getUsername(),
                EmailType.WELCOME,
                templateData
        );

        emailProducerService.sendEmailEvent(emailEvent);
    }

    public void sendVerificationEmail(User user, String verificationLink) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("name", user.getUsername());
        templateData.put("verificationLink", verificationLink);

        EmailEvent emailEvent = new EmailEvent(
                user.getUsername(),
                EmailType.EMAIL_VERIFICATION,
                templateData
        );

        emailProducerService.sendEmailEvent(emailEvent);
    }

    public void sendRecruiterInvitation(String recruiterEmail, String companyName, String invitationLink) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("recruiterName", recruiterEmail);
        templateData.put("companyName", companyName);
        templateData.put("organizationName", "HireXHire");
        templateData.put("invitationLink", invitationLink);
        templateData.put("expiryTime", "7 days");

        EmailEvent emailEvent = new EmailEvent(
                recruiterEmail,
                EmailType.RECRUITER_INVITATION,
                templateData
        );

        emailProducerService.sendEmailEvent(emailEvent);
    }

    public void sendPasswordResetEmail(String email, String resetTokenLink) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("resetLink",  resetTokenLink);
        templateData.put("expiryTime", "1 hour");

        EmailEvent emailEvent = new EmailEvent(
                email,
                EmailType.PASSWORD_RESET,
                templateData
        );

        emailProducerService.sendEmailEvent(emailEvent);
    }
}

