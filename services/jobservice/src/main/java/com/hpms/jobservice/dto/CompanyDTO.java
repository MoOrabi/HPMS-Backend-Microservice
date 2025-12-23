package com.hpms.jobservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDTO {
    private UUID id;

    private String name;

    private String managerFirstName;

    private String managerLastName;

    private String location;

    private String imageUrl;
}
