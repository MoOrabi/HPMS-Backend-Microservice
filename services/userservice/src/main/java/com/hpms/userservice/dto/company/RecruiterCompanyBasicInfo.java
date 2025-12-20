package com.hpms.userservice.dto.company;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class RecruiterCompanyBasicInfo {
    private String firstName;
    private String lastName;
    private String companyName ;
    private String companyId ;

}
