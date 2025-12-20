package com.hpms.userservice.dto;

import com.hpms.userservice.model.Company;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterSignUpRequest {

    private String id;

    private String firstName;

    private String lastName;

    private Company company;

    private String phoneNumberCountryCode;

    private String phoneNumber;

    private String jobTitle;

    private String username;

    private String password;
}
