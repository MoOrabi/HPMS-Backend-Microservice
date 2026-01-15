package com.hpms.jobservice.dto;

import com.hpms.commonlib.dto.SelectOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRelatedDataDTO {
    private CompanyDTO company;
    private JobPostCreatorDTO creator;
    private List<RecruiterNameAndPhoto> recruiters;
    private Set<SelectOption> skills;
    private IndustryDTO industry;
    private JobNameDTO jobName;
    private Boolean isJobSavedForJS;
}