package com.hpms.userservice.dto.jobseeker;

import com.hpms.userservice.constants.CareerLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerCareerInterestsDTO {

    private CareerLevel careerLevel;

    private String minimumSalaryValue;

    private String minimumSalaryCurrency;

    private Boolean showMinimumSalary;

    private Set<String> jobsTypesUserInterestedIn;

    private Set<String> jobsUserInterestedIn;

    private String jobStatus;

    private Boolean openToSuggest;

    private Boolean searchable;

}
