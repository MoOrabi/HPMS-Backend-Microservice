package com.hpms.userservice.dto.jobseeker;

import com.hpms.userservice.model.jobseeker.JobSeekerLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerBasicInfoDTO {

    private String firstName;

    private String lastName;

    private String mobileNumberCountryCode;

    private String mobileNumber;

    private Date birthDate;

    private String Nationality;

    private JobSeekerLocation livesIn;

    private String gender;

    private String about;

    private String facebookLink;

    private String linkedinLink;

    private String githubLink;

    private Boolean readyToRelocate;
}
