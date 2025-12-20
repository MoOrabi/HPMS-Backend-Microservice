package com.hpms.userservice.dto.jobseeker;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerNameAndEmail {

    private String firstName;

    private String lastName;

    private String username;
}
