package com.hpms.userservice.dto.jobseeker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerProfessionalInfoDTO {

    private String yearsOfExperience;

    private Set<String> skills;
}
