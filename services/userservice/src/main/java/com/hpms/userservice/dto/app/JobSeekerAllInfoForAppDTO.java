package com.hpms.userservice.dto.app;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerAllInfoForAppDTO {
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

    private String livesIn;

    private Boolean readyToRelocate;

    private String Nationality;

    private String yearsOfExperience;

    private String careerLevel;

    private String minimumSalaryValue;

    private String minimumSalaryCurrency;

    private Boolean showMinimumSalary;

    private Set<String> jobsTypesUserInterestedIn;

    private Set<String> jobsUserInterestedIn;

    private String jobStatus;

    private Boolean searchable;

    private Boolean openToSuggest;

    private List<CertificateDTO> certificates ;

    private List<JobExperienceDTO> jobExperiences;

    private List<EducationDTO> educations;

    private Set<String> skills;

    private List<LanguageDTO> languages;

    private String about;

    private String facebookLink;

    private String linkedinLink;

    private String githubLink;

    private String CV ;

}
