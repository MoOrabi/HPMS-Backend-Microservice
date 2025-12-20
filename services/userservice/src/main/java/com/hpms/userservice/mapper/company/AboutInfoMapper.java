package com.hpms.userservice.mapper.company;

import com.hpms.userservice.dto.company.CompanyAboutInfoResponse;
import com.hpms.userservice.model.AboutCompany;
import org.springframework.stereotype.Component;

@Component
public class AboutInfoMapper {

    public CompanyAboutInfoResponse toDto(AboutCompany aboutCompany) {
        return CompanyAboutInfoResponse.builder()
                .about(aboutCompany.getAbout())
                .website(aboutCompany.getWebsite())
                .foundingDate(aboutCompany.getFoundingDate())
                .socialLinks(aboutCompany.getSocialLinks())
                .companyBenefits(aboutCompany.getCompanyBenefits())
                .build();
    }
}
