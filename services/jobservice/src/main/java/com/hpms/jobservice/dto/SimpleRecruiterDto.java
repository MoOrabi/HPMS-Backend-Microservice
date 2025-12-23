package com.hpms.jobservice.dto;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRecruiterDto {

    private UUID id ;

    private String imageUrl ;

    private String fullName ;

    private String jobTitle ;

}
