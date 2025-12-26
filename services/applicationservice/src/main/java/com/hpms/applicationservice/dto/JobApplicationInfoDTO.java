package com.hpms.applicationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class JobApplicationInfoDTO {

    private UUID id;

    private UUID applicantId;

    private String applicantFirstName;

    private String applicantLastName;

    private String ProfilePhoto;

    private String applicantJobTitle;

    private String applicantCareerLevel;

    private Set<EducationDTO> applicantEducations;

    private JobSeekerLocationDTO applicantLocation;

    private String status ;

    private boolean viewed;

}
