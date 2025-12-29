package com.hpms.jobservice.dto;

import com.hpms.jobservice.constants.EmploymentType;
import com.hpms.jobservice.constants.JobType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Builder
public class JobPostForApplication {

    private UUID jobId;
    private String jobTitle;
    private JobType jobType;
    private EmploymentType employmentType ;
    private boolean open ;
    private LocalDateTime appUpdatedAt ;
    private String companyLogo;
    private String companyLocation;
    private JobPostPublicNumbers publicNumbers;
    private boolean deleted;
    private UUID applicationId;
    private String applicationStatus;
    private int newActionsNumber;
}
