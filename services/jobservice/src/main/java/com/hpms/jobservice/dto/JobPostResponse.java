package com.hpms.jobservice.dto;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.jobservice.constants.EmploymentType;
import com.hpms.jobservice.constants.JobType;
import com.hpms.jobservice.model.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
public class JobPostResponse {

    private UUID id;
    private String jobTitle;
    private JobType jobType;
    private EmploymentType employmentType ;
    private int minExperienceYears;
    private int maxExperienceYears;
    private IndustryDTO industry;
    private JobNameDTO jobName;
    private double minSalary;
    private double maxSalary;
    private String currency;
    private String description;
    private String requirements;
    private String benefits;
    private boolean draft ;
    private boolean open ;
    private LocalDateTime createdOn ;
    private LocalDateTime updatedOn ;
    private long maxApplication;
    private String gender;
    private String educationLevel;
//    private List<JobApplication> jobApplications;
    private CompanyDTO company;
    private String companyLocation;
    private JobPostCreatorDTO creator;
    private List<Question> questions;
    private List<RecruiterNameAndPhoto> recruitersTeam;
    private Set<SelectOption> skills;
    private boolean deleted;
    private JobApplicationNumberPerStatus numberPerStatus;
    private JobPostDashboardNumbers dashboardNumbers;
}
