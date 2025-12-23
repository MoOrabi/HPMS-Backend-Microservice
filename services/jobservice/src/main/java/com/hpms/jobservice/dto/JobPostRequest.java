package com.hpms.jobservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostRequest {

    private UUID id ;

    @NotNull(message = "Title cannot be null")
    @NotBlank(message = "Title cannot be blank")
    private String jobTitle;

    @NotNull(message = "Job type cannot be null")
    private String jobType;

    @NotNull(message = "Employment type cannot be null")
    @NotBlank
    private String employmentType;

    @NotNull(message = "Description cannot be null")
    @NotBlank(message = "Description cannot be blank")
    @Size(max = 2000, message = "Description length must be less than or equal to 3000 characters")
    private String description;

    @NotNull(message = "Requirements cannot be null")
    @NotBlank(message = "Requirements cannot be blank")
    @Size(max = 2000, message = "Requirements length must be less than or equal to 3000 characters")
    private String requirements;

    @Min(value = 0, message = "Minimum experience years cannot be negative")
    @Max(value = 100, message = "Maximum experience years cannot exceed 50 years")
    private int minExperienceYears;

    @Max(value = 100, message = "Maximum experience years cannot exceed 50 years")
    @Min(value = 0, message = "Minimum experience years cannot be negative")
    private int maxExperienceYears;

    @NotNull
    private Set<Long> skills;

    @NotNull
    private Long jobNameId;

    @NotNull
    private long industryId;

    @Min(value = 0, message = "Minimum salary cannot be negative")
    private double minSalary;

    @Min(value = 0, message = "Maximum salary cannot be negative")
    private double maxSalary;

    private String currency;

    @Size(max = 2000, message = "Benefits length must be less than or equal to 3000 characters")
    private String benefits;

}
 