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
@Table(name = "jobpost_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobPostProfile {

    @Id
    private UUID jobPostId;

    private String jobTitle;

    private String jobType;

    private String employmentType;

    private String city;
    private String country;
    private boolean remote = false;

    private int minExperienceYears;
    private int maxExperienceYears;

    @ElementCollection
    @CollectionTable(name = "jobpost_skills", joinColumns = @JoinColumn(name = "jobpost_id"))
    @Column(name = "skill")
    private Set<String> skills = new HashSet<>();

    private UUID companyId;
    private String companyName;
    private String companyLogo;

    private boolean open = true;
    private boolean deleted = false;

    private LocalDateTime publishedOn;
}
