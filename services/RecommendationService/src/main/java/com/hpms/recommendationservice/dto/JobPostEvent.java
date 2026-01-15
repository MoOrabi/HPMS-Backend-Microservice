package com.hpms.recommendationservice.dto;

import com.hpms.commonlib.dto.SelectOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostEvent {
    private UUID jobPostId;

    private String jobTitle;

    private String companyLogo;

    private String jobType;

    private String employmentType;

    private String city;
    private String country;
    private boolean remote;

    private int minExperienceYears;
    private int maxExperienceYears;

    private Set<SelectOption> skills;

    private UUID companyId;
    private String companyName;

    private boolean open;
    private boolean deleted;

    private String eventType;

    private LocalDateTime publishedOn;

}
