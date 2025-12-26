package com.hpms.userservice.dto.company;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyStatistics{
    Integer companyActiveJobsNumber  ;
    int companyMembersNumber ;
    int pendingInvitations ;
}
