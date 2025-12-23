package com.hpms.userservice.mapper;

import com.hpms.userservice.dto.CompanyDTO;
import com.hpms.userservice.model.Company;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CompanyDTOMapper implements Function<Company, CompanyDTO> {

    @Override
    public CompanyDTO apply(Company company) {
        return CompanyDTO.builder()
                .id(company.getId())
                .name(company.getName())
                .managerFirstName(company.getManagerFirstName())
                .managerLastName(company.getManagerLastName())
                .location(company.getMainBranchLocation()!=null?company.getMainBranchLocation().getCity() + " " + company.getMainBranchLocation().getCountry(): null)
                .imageUrl(company.getLogo())
                .build();
    }
}
