package com.hpms.jobservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JobPostPublicNumbers {
    private long applied;
    private long viewed;
    private long inConsideration;
    private long rejected;
}
