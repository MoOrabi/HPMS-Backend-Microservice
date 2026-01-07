package com.hpms.recommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateRecommendationDTO {
    private UUID jobSeekerId;
    private String firstName;
    private String lastName;
    private String careerLevel;
    private String jobTitle;

    private String highestDegreeName;
    private String highestDegreeInstitute;

    private String lastJobTitle;
    private String lastJobOrganizationName;
    private int lastJobStartedAt;
    private int lastJobEndedAt;

    private Set<String> skills;

    private double matchScore;
    private List<String> matchReasons;
}
