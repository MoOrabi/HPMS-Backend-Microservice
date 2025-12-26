package com.hpms.applicationservice.dto;

import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobPostCompanyRecruitersAndCreator {
    private UUID companyId;
    private Set<UUID> recruiterIds = new HashSet<>();
    private UUID creatorId;
}
