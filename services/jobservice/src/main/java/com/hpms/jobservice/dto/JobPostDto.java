package com.hpms.jobservice.dto;

import com.hpms.commonlib.dto.SelectOption;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostDto {

    private UUID id;
    private String jobTitle;
    private String company ;
    private String companyImageUrl ;
    private String location;
    private LocalDateTime publishedOn;
    private String jobType;
    private String employmentType ;
    private int minExperienceYears;
    private int maxExperienceYears;
    private String industry;
    private String jobName;
    private Integer applicationNum ;
    private String isApplied ;
    private Set<SelectOption> skills;
    private boolean saved;
}
