package com.hpms.jobservice.event;

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
public class JobPostEventDTO {
    private UUID jobPostId;
    private String jobTitle;
    private String jobType;
    private String employmentType;
    private int minExperienceYears;
    private int maxExperienceYears;
    private double minSalary;
    private double maxSalary;
    private String currency;
    private String educationLevel;
    private String gender;
    private Set<Long> skillIds;
    private Long industryId;
    private Long jobNameId;
    private UUID companyId;
    private boolean open;
    private boolean deleted;
    private String eventType;
    private LocalDateTime publishedOn;
    private LocalDateTime lastJobUpdate;
}
