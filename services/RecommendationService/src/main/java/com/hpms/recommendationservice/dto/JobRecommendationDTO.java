package com.hpms.recommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRecommendationDTO {
    private UUID jobPostId;
    private String jobTitle;
    private String city;
    private String country;
    private String jobType;
    private String employmentType;
    private double minSalary;
    private double maxSalary;
    private String currency;
    private UUID companyId;
    private String companyName;

    // Recommendation metadata
    private double matchScore;  // 0.0 to 1.0
    private List<String> matchReasons;  // Why this was recommended
}