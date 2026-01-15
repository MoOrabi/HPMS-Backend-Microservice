package com.hpms.jobservice.dto.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyLocationAndLogoDTO {
    private UUID id;

    private String location;

    private String city;

    private String country;

    private String logo;
}
