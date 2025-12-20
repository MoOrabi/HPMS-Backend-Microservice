package com.hpms.emailservice.config;

import com.hpms.commonlib.dto.EmailEvent;
import com.hpms.emailservice.service.EmailService;
import com.hpms.emailservice.service.impl.EmailServiceImp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailEventListener {

    private final EmailServiceImp emailService;

    @KafkaListener(
            topics = "${kafka.topics.email-events:email-events-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleEmailEvent(EmailEvent emailEvent) {
        log.info("Received email event: {}", emailEvent);

        try {
            emailService.sendEmail(emailEvent);
            log.info("Email sent successfully for event: {}", emailEvent.getEventId());
        } catch (Exception ex) {
            log.error("Failed to send email for event: {}", emailEvent.getEventId(), ex);
            // TODO: Implement retry logic or dead letter queue
        }
    }
}
