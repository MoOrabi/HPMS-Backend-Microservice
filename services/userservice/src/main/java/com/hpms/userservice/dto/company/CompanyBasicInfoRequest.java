package com.hpms.userservice.dto.company;

import com.hpms.userservice.constants.CompanySize;
import com.hpms.userservice.model.Location;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyBasicInfoRequest {

    private String tagline;

    private CompanySize companySize ;

    private Location mainBranchLocation;

    private String industry;
}
