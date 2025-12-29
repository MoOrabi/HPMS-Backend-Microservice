package com.hpms.applicationservice.service.client;

import com.hpms.applicationservice.service.impl.DeleteUserRelatedServiceImpl;
import com.hpms.commonlib.dto.DeleteUserRelatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteUserRelatedEventListener {
    private final DeleteUserRelatedServiceImpl deleteJobRelatedService;

    @KafkaListener(
            topics = "${kafka.topics.delete-user-related-events}",
            groupId = "delete-user-related-at-apps-group"
    )
    public void handleDeleteUserRelatedEvent(DeleteUserRelatedEvent deleteEvent) {
        log.info("Received Delete User Related data event: {}", deleteEvent);

        try {
            deleteJobRelatedService.deleteUserRelatedData(deleteEvent);
            log.info("Delete Related data successed for event: {}", deleteEvent.getEventId());
        } catch (Exception ex) {
            log.error("Failed to delete Related data successed for event: {}", deleteEvent.getEventId(), ex);
        }
    }


}
