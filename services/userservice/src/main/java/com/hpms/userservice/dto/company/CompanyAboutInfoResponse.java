package com.hpms.userservice.dto.company;

import com.hpms.userservice.model.Benefit;
import com.hpms.userservice.model.UserSocialLink;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyAboutInfoResponse {

    private String about;

    private String website;

    private Date foundingDate;

    private List<UserSocialLink> socialLinks;

    private List<Benefit> companyBenefits;



}
