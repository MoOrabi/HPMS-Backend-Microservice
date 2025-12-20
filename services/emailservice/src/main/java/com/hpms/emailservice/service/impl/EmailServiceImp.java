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


//    @Override
//    public void sendActivateEmail(String sendTo, String ActivateLink) throws MessagingException {
//        ActivateEmailBuilder activateEmailBuilder = new ActivateEmailBuilder(mailSender.createMimeMessage());
//        mailSender.send(activateEmailBuilder.createEmail(sendTo, ActivateLink));
//    }
//
//    @Override
//    public void sendCompanyWelcomeEmail(String sendTo) throws MessagingException {
//        CompanyWelcomeEmailBuilder companyWelcomeEmailBuilder = new CompanyWelcomeEmailBuilder(mailSender.createMimeMessage());
//        Optional<String> companyNameOptional = companyRepository.getByEmail(sendTo);
//        if (companyNameOptional.isPresent()) {
//            String companyName = companyNameOptional.get().toString();
//            mailSender.send(companyWelcomeEmailBuilder.createEmail(sendTo, companyName));
//        }
//
//    }
//
//    @Override
//    public void sendJobSeekerWelcomeEmail(String sendTo) throws MessagingException {
//        JobSeekerWelcomeEmailBuilder jobSeekerWelcomeEmailBuilder = new JobSeekerWelcomeEmailBuilder(mailSender.createMimeMessage());
//        Optional<String> jobSeekerNameOptional = jobSeekerProfileRepository.getFirstNameByEmail(sendTo);
//        String jobSeekerName = jobSeekerNameOptional.get().toString();
//        mailSender.send(jobSeekerWelcomeEmailBuilder.createEmail(sendTo, jobSeekerName));
//    }
//
//    @Override
//    public void sendResetPasswordEmail(String sendTo, String resetLink) throws MessagingException {
//        ResetPasswordEmailBuilder resetPasswordEmailBuilder = new ResetPasswordEmailBuilder(mailSender.createMimeMessage());
//        mailSender.send(resetPasswordEmailBuilder.createEmail(sendTo, resetLink));
//    }

    public void sendEmail(EmailEvent emailEvent) throws UnsupportedEncodingException {
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

    private void sendWelcomeEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Welcome to HPMS!";
        String template = "welcome";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendVerificationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verify Your Email Address";
        String template = "verification";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendPasswordResetEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Reset Your Password";
        String template = "password-reset";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendPasswordChangedEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Your Password Has Been Changed";
        String template = "password-changed";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendInterviewInvitationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Interview Invitation";
        String template = "interview-invitation";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendRecruiterInvitationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "You're Invited to Join HPMS as a Recruiter";
        String template = "recruiter-invitation";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendApplicationStatusEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Application Status Update";
        String template = "application-status";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendJobAlertEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "New Job Opportunities";
        String template = "job-alert";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    private void sendGenericEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException {
        String subject = "Notification from HPMS";
        String template = "generic";
        sendHtmlEmail(event.getRecipientEmail(), subject, template, event.getTemplateData());
    }

    // ✅ Core method: Send HTML email with Thymeleaf template
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

    // ✅ Alternative: Send plain text email (simpler, no HTML)
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

