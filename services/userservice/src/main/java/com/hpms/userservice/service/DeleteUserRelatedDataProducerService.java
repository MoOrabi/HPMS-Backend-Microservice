package com.hpms.userservice.service;

import com.hpms.commonlib.dto.DeleteUserRelatedEvent;
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
public class DeleteUserRelatedDataProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.delete-user-related:delete-user-related-topic}")
    private String deleteTopic;

    public void sendDeleteEvent(DeleteUserRelatedEvent emailEvent) {
        log.info("Sending Delete event to Kafka: {}", emailEvent);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(deleteTopic, emailEvent.getEventId(), emailEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Delete event sent successfully: {} with offset: {}",
                        emailEvent.getEventId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send delete event: {}", emailEvent.getEventId(), ex);
            }
        });
    }
}
