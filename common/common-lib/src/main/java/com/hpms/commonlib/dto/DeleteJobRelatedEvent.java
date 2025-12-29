package com.hpms.commonlib.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteJobRelatedEvent implements Serializable {

    private String eventId;           // Unique event ID for tracking
    private UUID jobId;
    private LocalDateTime timestamp;  // When event was created

    public DeleteJobRelatedEvent(UUID jobId) {
        this.eventId = UUID.randomUUID().toString();
        this.jobId = jobId;
        this.timestamp = LocalDateTime.now();
    }
}
