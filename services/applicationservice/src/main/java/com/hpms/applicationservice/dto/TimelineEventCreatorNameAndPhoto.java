package com.hpms.applicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TimelineEventCreatorNameAndPhoto {
    private String name;
    private String photoUrl;
}
