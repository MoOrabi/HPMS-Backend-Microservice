package com.hpms.jobservice.service;

import com.hpms.commonlib.dto.DeleteJobRelatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteJobRelatedProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.delete-job-related:delete-job-related-topic}")
    private String deleteTopic;

    public void sendDeleteEvent(DeleteJobRelatedEvent deleteEvent) {
        log.info("Sending Delete event to Kafka: {}", deleteEvent);

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(deleteTopic, deleteEvent.getEventId(), deleteEvent);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Delete event sent successfully: {} with offset: {}",
                        deleteEvent.getEventId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to send delete event: {}", deleteEvent.getEventId(), ex);
            }
        });
    }
}
