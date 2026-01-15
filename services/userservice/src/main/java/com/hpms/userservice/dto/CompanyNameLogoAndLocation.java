package com.hpms.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyNameLogoAndLocation {
    private String companyName;
    private String city;
    private String country;
    private String logo;
}
