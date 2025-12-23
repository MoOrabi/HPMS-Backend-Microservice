package com.hpms.jobservice.model;

import com.hpms.jobservice.constants.EmploymentType;
import com.hpms.jobservice.constants.JobType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobPost {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String jobTitle;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private EmploymentType employmentType ;

    private int minExperienceYears;

    private int maxExperienceYears;

    private double minSalary;

    private double maxSalary;

    private String currency;

    @Column(length = 2000)
    private String description;

    @Column(length = 2000)
    private String requirements;

    @Column(length = 2000)
    private String benefits;

    private boolean draft ;

    private boolean open ;

    @CreationTimestamp
    private LocalDateTime createdOn ;

    @UpdateTimestamp
    private LocalDateTime updatedOn ;

    @Column(name = "published_on", nullable = false, updatable = false)
    private LocalDateTime publishedOn;

    // advanced Setting
    private long maxApplication;

    private String gender;

    private String educationLevel;

//    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobPost", cascade = CascadeType.ALL)
//    private List<JobApplication> jobApplications;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobPost" ,cascade = CascadeType.ALL)
    private List<Question> questions;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id")
    )
    @Column(name = "skill_id")
    private Set<Long> skillIds;

    @Column(name="industry_id", nullable = false)
    private Long industryId;

    @Column(name="job_name_id", nullable = false)
    private Long jobNameId;

    // Store company ID only
    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    // Store creator (user) ID only
    @Column(name = "creator_id", nullable = false)
    private UUID creatorId;

    // Store recruiter IDs only
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "job_recruiters",
            joinColumns = @JoinColumn(name = "job_id")
    )
    @Column(name = "recruiter_id")
    private Set<UUID> recruiterIds;

    private boolean deleted;

}
