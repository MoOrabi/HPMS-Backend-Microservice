package com.hpms.userservice.dto.jobseeker;

import com.hpms.userservice.constants.CareerLevel;
import com.hpms.userservice.model.jobseeker.JobSeekerLocation;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerSimpleDto {

    private UUID id ;

    private String firstName;

    private String lastName;

    private String username;

    private String jobTitle;

    private String profilePhoto;

    private JobSeekerLocation livesIn;

    private String yearsOfExperience;

    private CareerLevel careerLevel;

    private String jobStatus;

    private String about;

}
