package com.hpms.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRelatedDataDTO {
    private CompanyDTO company;
    private JobPostCreatorDTO creator;
    private List<RecruiterNameAndPhoto> recruiters;
    private List<SkillDTO> skills;
    private IndustryDTO industry;
    private JobNameDTO jobName;
    private Boolean isJobSavedForJS;
}