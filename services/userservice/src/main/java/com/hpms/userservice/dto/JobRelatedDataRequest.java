package com.hpms.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRelatedDataRequest {
    private UUID jobPostId;
    private UUID companyId;
    private UUID creatorId;
    private Set<UUID> recruiterIds = new HashSet<>();
    private Set<Long> skillIds;
    private Long industryId;
    private Long jobNameId;
    private UUID jobSeekerCallerId;
}
