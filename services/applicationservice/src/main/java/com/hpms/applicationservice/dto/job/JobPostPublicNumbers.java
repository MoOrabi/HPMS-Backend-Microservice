package com.hpms.applicationservice.dto.job;

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
