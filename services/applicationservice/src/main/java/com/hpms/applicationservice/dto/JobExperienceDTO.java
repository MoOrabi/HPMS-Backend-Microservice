package com.hpms.applicationservice.dto;

import jakarta.persistence.Lob;

import java.util.UUID;

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
