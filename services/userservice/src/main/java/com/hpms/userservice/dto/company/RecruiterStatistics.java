package com.hpms.userservice.dto.company;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecruiterStatistics {

    private int activePostsNumber;

    private int companyNumberOfMembers;

}
