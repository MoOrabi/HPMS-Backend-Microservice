package com.hpms.jobservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JobPostDashboardNumbers {
    private long applied;
    private long newApps;
    private long comments;
}
