package com.hpms.userservice.dto.app;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobExperienceDTO {
    private UUID id;
    private String name;
    private String place;

    @Lob
    private String about;

    private String jobCategory;

    private String jobType;

    private String startMonth;

    private String startYear;

    private String endMonth;

    private String endYear;
}
