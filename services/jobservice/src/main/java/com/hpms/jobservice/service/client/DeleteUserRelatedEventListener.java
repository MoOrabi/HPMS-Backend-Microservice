package com.hpms.jobservice.service.client;

import com.hpms.commonlib.dto.DeleteUserRelatedEvent;
import com.hpms.jobservice.service.imp.DeleteUserRelatedServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserRelatedEventListener {

    private final DeleteUserRelatedServiceImpl deleteUserRelatedService;

    @KafkaListener(
            topics = "${kafka.topics.delete-user-related-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleDeleteUserRelatedEvent(DeleteUserRelatedEvent deleteEvent) {
        log.info("Received Delete Related data event: {}", deleteEvent);

        try {
            deleteUserRelatedService.deleteUserRelatedData(deleteEvent);
            log.info("Delete Related data successed for event: {}", deleteEvent.getEventId());
        } catch (Exception ex) {
            log.error("Failed to delete Related data successed for event: {}", deleteEvent.getEventId(), ex);
            // TODO: Implement retry logic or dead letter queue
        }
    }
}
