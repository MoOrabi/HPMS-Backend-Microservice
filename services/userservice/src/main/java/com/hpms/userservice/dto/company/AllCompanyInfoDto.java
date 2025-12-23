package com.hpms.userservice.dto.company;

import com.hpms.userservice.dto.JobPostDto;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AllCompanyInfoDto {

    private CompanyBasicsInfoResponse basicInfoDto;

    private CompanyAboutInfoResponse AboutInfoDto;

    private List<JobPostDto> jobPosts;

    private boolean isComplete;

}
