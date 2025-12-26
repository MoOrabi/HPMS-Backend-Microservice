package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.ApplicationStatus;
import com.hpms.applicationservice.constants.TimelineEventType;
import com.hpms.applicationservice.model.AppEvent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimelineEventResponse {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private TimelineEventType type;

    private UUID creatorId;

    private String creatorName;

    private String creatorPhoto;

    private ApplicationStatus applicationStatus;

    private AppEvent content;

}
