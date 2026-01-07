package com.hpms.recommendationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "jobseeker_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobSeekerProfile {

    @Id
    private UUID jobSeekerId;

    private String firstName;
    private String lastName;
    private String careerLevel;
    private String jobTitle;

    @Column(name = "years_of_experience")
    private String yearsOfExperience;


    @ElementCollection
    @CollectionTable(name = "jobseeker_skills", joinColumns = @JoinColumn(name = "jobseeker_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    private String highestDegreeName;
    private String highestDegreeInstitute;

    private String lastJobTitle;
    private String lastJobOrganizationName;
    private int lastJobStartedAt;
    private int lastJobEndedAt;

    @ElementCollection
    @CollectionTable(name = "jobseeker_job_types", joinColumns = @JoinColumn(name = "jobseeker_id"))
    @Column(name = "job_type")
    private Set<String> jobTypesInterestedIn = new HashSet<>();

    private String educationLevel;
    private String city;
    private String country;
    private boolean readyToRelocate;

    @UpdateTimestamp
    private LocalDateTime lastUpdated;

    private LocalDateTime lastSkillsUpdate;
    private LocalDateTime lastProfileUpdate;
}
