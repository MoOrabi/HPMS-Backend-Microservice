package com.hpms.userservice.dto.jobseeker;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.userservice.constants.CareerLevel;
import com.hpms.userservice.model.Education;
import com.hpms.userservice.model.jobseeker.Certificate;
import com.hpms.userservice.model.jobseeker.JobExperience;
import com.hpms.userservice.model.jobseeker.JobSeekerLocation;
import com.hpms.userservice.model.jobseeker.Language;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSeekerAllInfoDTO {
    private String firstName;

    private String lastName;

    private String username;

    private String mobileNumberCountryCode;

    private String mobileNumber;

    private String jobTitle;

    @CreatedDate
    private LocalDate createdDate;

    private Date birthDate;

    private String gender;

    private String profilePhoto;

    private JobSeekerLocation livesIn;

    private Boolean readyToRelocate;

    private String Nationality;

    private String yearsOfExperience;

    private CareerLevel careerLevel;

    private String minimumSalaryValue;

    private String minimumSalaryCurrency;

    private Boolean showMinimumSalary;

    private Set<String> jobsTypesUserInterestedIn;

    private Set<String> jobsUserInterestedIn;

    private String jobStatus;

    private Boolean searchable;

    private Boolean openToSuggest;

    private Set<Certificate> certificates ;

    private Set<JobExperience> jobExperiences;

    private Set<Education> educations;

    private Set<SelectOption> skills;

    private Set<Language> languages;

    private String about;

    private String facebookLink;

    private String linkedinLink;

    private String githubLink;

    private String CV ;

}
