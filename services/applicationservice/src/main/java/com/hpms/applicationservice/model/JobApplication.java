package com.hpms.applicationservice.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hpms.applicationservice.constants.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="job_seeker_id", nullable = false)
    private UUID jobSeekerId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<QuestionAnswer> questionAnswer;


    @Column(name="job_id", nullable = false)
    private UUID jobPostId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private boolean viewed;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<Evaluation> evaluations;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<ApplicationComment> comments;

    @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
    private Offer offer;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL)
    private List<TimelineEvent> timelineEvents;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

}
