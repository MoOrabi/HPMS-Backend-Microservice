package com.hpms.commonlib.dto;

import com.hpms.commonlib.constants.EmailType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailEvent implements Serializable {

    private String eventId;           // Unique event ID for tracking
    private String recipientEmail;    // To email address
    private EmailType emailType;      // Type of email to send
    private Map<String, Object> templateData;  // Dynamic data for email template
    private LocalDateTime timestamp;  // When event was created

    public EmailEvent(String recipientEmail, EmailType emailType, Map<String, Object> templateData) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.recipientEmail = recipientEmail;
        this.emailType = emailType;
        this.templateData = templateData;
        this.timestamp = LocalDateTime.now();
    }
}
