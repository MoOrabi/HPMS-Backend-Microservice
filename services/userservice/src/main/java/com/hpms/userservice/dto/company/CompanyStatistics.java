package com.hpms.userservice.dto.company;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyStatistics{
    int companyActiveJobsNumber  ;
    int companyMembersNumber ;
    int pendingInvitations ;
}
