package com.hpms.recommendationservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "recommendation_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID jobSeekerId;
    private UUID jobPostId;

    private double score;

    private List<String> matchReasons;

    @CreationTimestamp
    private LocalDateTime recommendedAt;

    private boolean clicked;
    private boolean applied;
}