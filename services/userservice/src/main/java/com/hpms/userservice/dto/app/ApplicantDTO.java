package com.hpms.userservice.dto.app;

import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantDTO {
    String firstName;
    String lastName;
    String profilePhoto;
    String jobTitle;
    String careerLevel;
    Set<EducationDTO> educations;
    JobSeekerLocationDTO livesIn;
}
