package com.hpms.jobservice.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JobApplicationNumberPerStatus {
    private int applied;
    private int disqualified;
    private int phoneScreen;
    private int assessment;
    private int interview;
    private int offer;
    private int hired;
}
