package com.hpms.userservice.mapper.company;

import com.hpms.userservice.dto.company.CompanyBasicsInfoResponse;
import com.hpms.userservice.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class BasicsInfoMapper {
    public CompanyBasicsInfoResponse toDto(Company company) {
        return CompanyBasicsInfoResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .tagline(company.getTagline())
                .rank(company.getCompanyRank())
                .industry(company.getIndustry())
                .isSubscribe(false)
                .companySize(company.getCompanySize() != null ? company.getCompanySize().getSize() : null)
                .mainBranchLocation(company.getMainBranchLocation())
                .subscribersNumber(company.getSubscribers().size())
                .CoverImage(company.getCoverPhoto())
                .profileImage(company.getLogo())
                .build();
    }

    public Page<CompanyBasicsInfoResponse> toPage(Page<Company> companyPage) {
        return companyPage.map(this::toDto);
    }
}
