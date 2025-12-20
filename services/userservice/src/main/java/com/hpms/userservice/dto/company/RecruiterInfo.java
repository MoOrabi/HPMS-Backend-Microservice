package com.hpms.userservice.dto.company;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecruiterInfo {

    private UUID id;

    private String username;

    private String firstName;

    private String lastName;

    private String phoneNumberCountryCode;

    private String phoneNumber;

    private String jobTitle;

    private LocalDateTime profileCreatedAt;

    private String profilePhoto;

}
