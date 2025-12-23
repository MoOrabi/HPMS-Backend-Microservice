package com.hpms.emailservice.service.impl;


import com.hpms.commonlib.dto.EmailEvent;
import com.hpms.emailservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.from-name}")
    private String fromName;

    public EmailServiceImp(JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendEmail(EmailEvent emailEvent) {
        try {
            switch (emailEvent.getEmailType()) {
                case WELCOME -> sendWelcomeEmail(emailEvent);
                case EMAIL_VERIFICATION -> sendVerificationEmail(emailEvent);
                case PASSWORD_RESET -> sendPasswordResetEmail(emailEvent);
                case PASSWORD_CHANGED -> sendPasswordChangedEmail(emailEvent);
                case INTERVIEW_INVITATION -> sendInterviewInvitationEmail(emailEvent);
                case RECRUITER_INVITATION -> sendRecruiterInvitationEmail(emailEvent);
                case APPLICATION_STATUS_UPDATE -> sendApplicationStatusEmail(emailEvent);
                case JOB_ALERT -> sendJobAlertEmail(emailEvent);
                default -> {
                    log.warn("Unknown email type: {}", emailEvent.getEmailType());
                    sendGenericEmail(emailEvent);
                }
            }
            log.info("Email sent successfully to: {}", emailEvent.getRecipientEmail());
        } catch (Exception ex) {
            log.error("Failed to send email to: {}", emailEvent.getRecipientEmail(), ex);
            throw new RuntimeException("Email sending failed", ex);
        }
    }

    @Override
    public void sendWelcomeEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Welcome to HirexHire!";
        String template = "welcome";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendVerificationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verify Your Email Address";
        String template = "verification";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendPasswordResetEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Reset Your Password";
        String template = "password-reset";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendPasswordChangedEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Your Password Has Been Changed";
        String template = "password-changed";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendInterviewInvitationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Interview Invitation";
        String template = "interview-invitation";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendRecruiterInvitationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "You're Invited to Join HPMS as a Recruiter";
        String template = "recruiter-invitation";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendApplicationStatusEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Application Status Update";
        String template = "application-status";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendJobAlertEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "New Job Opportunities";
        String template = "job-alert";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    @Override
    public void sendGenericEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Notification from HPMS";
        String template = "generic";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> templateData)
            throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        // Set email properties
        helper.setFrom(fromEmail, fromName);
        helper.setTo(to);
        helper.setSubject(subject);

        // Process Thymeleaf template
        Context context = new Context();
        context.setVariables(templateData);
        String html = templateEngine.process(templateName, context);
        helper.setText(html, true); // true = HTML content

        // Send email
        mailSender.send(message);
        log.info("HTML email sent to: {} with template: {}", to, templateName);
    }

    private void sendPlainTextEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        log.info("Plain text email sent to: {}", to);
    }
}

