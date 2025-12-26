package com.hpms.commonlib.dto;

import com.hpms.commonlib.constants.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteUserRelatedEvent implements Serializable {

    private String eventId;           // Unique event ID for tracking
    private UUID userId;
    private RoleEnum roleEnum;
    private LocalDateTime timestamp;  // When event was created

    public DeleteUserRelatedEvent(UUID userId, RoleEnum roleEnum) {
        this.eventId = java.util.UUID.randomUUID().toString();
        this.userId = userId;
        this.roleEnum = roleEnum;
        this.timestamp = LocalDateTime.now();
    }
}
