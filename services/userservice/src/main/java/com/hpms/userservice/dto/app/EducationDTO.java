package com.hpms.userservice.dto.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {

    private UUID id;

    private String degree;

    private String institution;

    private String grade;

    private String fieldOfStudy;

    private Date start;

    private Date end;
}
