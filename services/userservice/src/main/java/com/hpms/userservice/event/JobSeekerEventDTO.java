package com.hpms.userservice.event;

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
public class JobSeekerEventDTO {
    private UUID jobSeekerId;
    private String firstName;
    private String lastName;
    private String jobTitle;
    private String yearsOfExperience;
    private String careerLevel;
    private String minimumSalaryValue;
    private String minimumSalaryCurrency;
    private Set<String> skills;
    private Set<String> jobTypesInterestedIn;
    private Set<String> jobsInterestedIn;
    private String educationLevel;
    private String location;
    private boolean readyToRelocate;
    private String gender;
    private boolean searchable;
    private boolean openToSuggest;
    private LocalDateTime lastSkillsUpdate;
    private LocalDateTime lastProfileUpdate;
    private String eventType;
}
