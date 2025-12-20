package com.hpms.userservice.dto.company;

import lombok.*;
import org.springframework.data.domain.Page;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllCompanyInfoDto {

    private CompanyBasicsInfoResponse basicInfoDto;

    private CompanyAboutInfoResponse AboutInfoDto;

//    private Page<JobPostDto> jobPosts;

    private boolean isComplete;

}
