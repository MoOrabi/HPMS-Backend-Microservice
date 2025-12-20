package com.hpms.userservice.dto.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterUpdateRequest {

    private String firstName;

    private String lastName;

    private String phoneNumberCountryCode;

    private String phoneNumber;
}
