package com.hpms.userservice.dto.jobseeker;

import com.hpms.commonlib.dto.SelectOption;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerProfessionalInfoDTO {

    private String yearsOfExperience;

    private Set<SelectOption> skills = new HashSet<>();
}
