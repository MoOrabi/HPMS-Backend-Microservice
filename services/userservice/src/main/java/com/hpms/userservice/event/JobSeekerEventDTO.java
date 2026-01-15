package com.hpms.userservice.event;

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
public class JobSeekerEventDTO {
    private UUID jobSeekerId;

    private String firstName;
    private String lastName;
    private String photo;
    private String jobTitle;

    private String yearsOfExperience;

    private String careerLevel;

    private String highestDegreeName;
    private String highestDegreeInstitute;

    private String lastJobTitle;
    private String lastJobOrganizationName;
    private String lastJobStartedAt;
    private String lastJobEndedAt;

    private Set<SelectOption> skills;

    private String city;
    private String country;

    private Set<String> jobTypesInterestedIn;

    private boolean readyToRelocate;

    private boolean openToSuggest;

    private String eventType;
}
