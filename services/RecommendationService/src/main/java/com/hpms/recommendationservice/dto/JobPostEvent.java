package com.hpms.recommendationservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class JobPostEvent {
    private UUID jobPostId;

    private String jobTitle;

    private String jobType;

    private String employmentType;

    private int minExperienceYears;
    private int maxExperienceYears;

    private String educationLevel;
    private String gender;
    private Set<String> skills;

    private Long industryId;
    private Long jobNameId;

    private UUID companyId;
    private String companyName;

    private String eventType;

    private LocalDateTime publishedOn;
    private LocalDateTime lastJobUpdate;
    private boolean matchingPropertyChanged;

}
