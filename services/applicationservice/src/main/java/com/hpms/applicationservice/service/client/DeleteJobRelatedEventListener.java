package com.hpms.applicationservice.service.client;

import com.hpms.applicationservice.service.impl.DeleteJobRelatedServiceImpl;
import com.hpms.commonlib.dto.DeleteJobRelatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteJobRelatedEventListener {
    private final DeleteJobRelatedServiceImpl deleteJobRelatedService;

    @KafkaListener(
            topics = "${kafka.topics.delete-job-related-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleDeleteJobRelatedEvent(DeleteJobRelatedEvent deleteEvent) {
        log.info("Received Delete Job Related data event: {}", deleteEvent);

        try {
            deleteJobRelatedService.deleteJobRelatedData(deleteEvent);
            log.info("Delete Related data successed for event: {}", deleteEvent.getEventId());
        } catch (Exception ex) {
            log.error("Failed to delete Related data successed for event: {}", deleteEvent.getEventId(), ex);
        }
    }


}
