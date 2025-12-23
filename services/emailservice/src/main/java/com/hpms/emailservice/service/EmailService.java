package com.hpms.emailservice.service;

import com.hpms.commonlib.dto.EmailEvent;
import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {

    void sendWelcomeEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendVerificationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendPasswordResetEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendPasswordChangedEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendInterviewInvitationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendRecruiterInvitationEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendApplicationStatusEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendJobAlertEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;

    void sendGenericEmail(EmailEvent event) throws MessagingException, UnsupportedEncodingException;
}
