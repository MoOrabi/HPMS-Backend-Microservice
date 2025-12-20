package com.hpms.userservice.mapper.company;

import com.hpms.userservice.dto.company.AllCompanyInfoDto;
import com.hpms.userservice.dto.company.CompanyAboutInfoResponse;
import com.hpms.userservice.dto.company.CompanyBasicsInfoResponse;
import com.hpms.userservice.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public AllCompanyInfoDto toDto(Company company) {
        CompanyBasicsInfoResponse basicInfoDto = CompanyBasicsInfoResponse.builder()
                .name(company.getName())
                .tagline(company.getTagline())
                .mainBranchLocation(company.getMainBranchLocation())
                .industry(company.getIndustry())
                .rank(company.getCompanyRank())
                .subscribersNumber(0)
                .CoverImage(company.getCoverPhoto())
                .profileImage(company.getLogo())
                .isSubscribe(false)
                .build();
            if(company.getCompanySize()!=null){
                basicInfoDto.setCompanySize(company.getCompanySize().getSize());
            }
        CompanyAboutInfoResponse aboutInfoDto = null;
        System.out.println(company.getAboutCompany());
        if (company.getAboutCompany() != null) {
            aboutInfoDto = CompanyAboutInfoResponse.builder()
                    .about(company.getAboutCompany().getAbout())
                    .website(company.getAboutCompany().getWebsite())
                    .foundingDate(company.getAboutCompany().getFoundingDate())
                    .socialLinks(company.getAboutCompany().getSocialLinks())
                    .companyBenefits(company.getAboutCompany().getCompanyBenefits())
                    .build();
        }
        return AllCompanyInfoDto.builder()
                .AboutInfoDto(aboutInfoDto)
                .basicInfoDto(basicInfoDto)
                .build();
    }

    public Page<AllCompanyInfoDto> toPage(Page<Company> companyPage) {
        return companyPage.map(this::toDto);
    }

}
