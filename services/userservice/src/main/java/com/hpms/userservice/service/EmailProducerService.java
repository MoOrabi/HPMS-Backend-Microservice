package com.hpms.userservice.service;

import com.hpms.commonlib.dto.EmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.email-events:email-events-topic}")
    private String emailTopic;

    public void sendEmailEvent(EmailEvent emailEvent) {
        log.info("Sending email event to Kafka: {}", emailEvent);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(emailTopic, emailEvent.getEventId(), emailEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Email event sent successfully: {} with offset: {}",
                        emailEvent.getEventId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send email event: {}", emailEvent.getEventId(), ex);
            }
        });
    }
}
