package com.hpms.recommendationservice.service;

import com.hpms.recommendationservice.dto.RecommendationScore;
import com.hpms.recommendationservice.model.JobPostProfile;
import com.hpms.recommendationservice.model.JobSeekerProfile;
import com.hpms.recommendationservice.model.RecommendationLog;
import com.hpms.recommendationservice.repository.JobPostProfileRepository;
import com.hpms.recommendationservice.repository.JobSeekerProfileRepository;
import com.hpms.recommendationservice.repository.RecommendationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobPostProfileRepository jobPostProfileRepository;
    private final RecommendationEngine recommendationEngine;
    private final RecommendationLogRepository recommendationLogRepository;

    private static final int MIN_UPDATE_INTERVAL_HOURS = 1;
    private static final double MIN_MATCH_SCORE = 0;

    /**
     * Validates if enough time has passed since last update
     */
    public boolean canUpdateProfile(LocalDateTime lastUpdate) {
        if (lastUpdate == null) {
            return true;
        }
        Duration duration = Duration.between(lastUpdate, LocalDateTime.now());
        return duration.toHours() >= MIN_UPDATE_INTERVAL_HOURS;
    }

    /**
     * Re-match a job seeker with all active jobs after skills update
     */
    @Transactional
    public void rematchJobSeekerWithJobs(UUID jobSeekerId) {
        log.info("Re-matching job seeker {} with active jobs", jobSeekerId);

        JobSeekerProfile seeker = jobSeekerProfileRepository.findById(jobSeekerId)
                .orElseThrow(() -> new RuntimeException("Job seeker not found"));


        // Delete old matches for this seeker
        recommendationLogRepository.deleteBySeekerIdCascade(jobSeekerId);

        // Get all active job posts
        List<JobPostProfile> activeJobs = jobPostProfileRepository.findByOpenTrueAndDeletedFalse();

        // Calculate scores and create new matches
        List<RecommendationLog> newMatches = new ArrayList<>();
        for (JobPostProfile job : activeJobs) {
            RecommendationScore score = recommendationEngine.calculateJobMatchScore(seeker, job);
            if (score.getTotalScore() >= MIN_MATCH_SCORE) {
                RecommendationLog log = RecommendationLog.builder()
                        .jobSeekerId(jobSeekerId)
                        .jobPostId(job.getJobPostId())
                        .score(score.getTotalScore())
                        .matchReasons(score.getReasons())
                        .build();
                newMatches.add(log);
            }
        }

        recommendationLogRepository.saveAll(newMatches);
        log.info("Created {} new matches for job seeker {}", newMatches.size(), jobSeekerId);
    }

    /**
     * Match a newly published job with all searchable job seekers
     */
    @Transactional
    public void matchJobWithSeekers(UUID jobPostId) {
        log.info("Matching job post {} with searchable job seekers", jobPostId);

        JobPostProfile job = jobPostProfileRepository.findById(jobPostId)
                .orElseThrow(() -> new RuntimeException("Job post not found"));

        if (!job.isOpen() || job.isDeleted()) {
            log.info("Job post {} is not active", jobPostId);
            return;
        }

        // Get all searchable job seekers
        List<JobSeekerProfile> seekers = jobSeekerProfileRepository.findAll();

        // Calculate scores and create matches
        List<RecommendationLog> jobMatches = new ArrayList<>();
//        List<RecommendationLog> candidateMatches = new ArrayList<>();

        for (JobSeekerProfile seeker : seekers) {
            RecommendationScore score = recommendationEngine.calculateJobMatchScore(seeker, job);
            if (score.getTotalScore() >= MIN_MATCH_SCORE) {
                // Create job-to-seeker match
                RecommendationLog jobToSeeker = RecommendationLog.builder()
                        .jobSeekerId(seeker.getJobSeekerId())
                        .jobPostId(jobPostId)
                        .score(score.getTotalScore())
                        .build();
                jobMatches.add(jobToSeeker);

//                // Create candidate-to-job match
//                RecommendationLog candidateToJob = RecommendationLog.builder()
//                        .jobSeekerId(seeker.getJobSeekerId())
//                        .jobPostId(jobPostId)
//                        .score(score.getTotalScore())
//                        .build();
//                candidateMatches.add(candidateToJob);
            }
        }

        recommendationLogRepository.saveAll(jobMatches);
//        recommendationLogRepository.saveAll(candidateMatches);
        log.info("Created {} job-to-seeker and {} candidate-to-job matches for job post {}",
                jobMatches.size(),
//                candidateMatches.size()
                0
                , jobPostId);
    }

    /**
     * Clean up matches for a closed or deleted job
     */
    @Transactional
    public void cleanupJobMatches(UUID jobPostId) {
        log.info("Cleaning up matches for job post {}", jobPostId);
        recommendationLogRepository.deleteByJobPostIdCascade(jobPostId);
    }

    /**
     * Clean up matches for a deleted job seeker
     */
    @Transactional
    public void cleanupSeekerMatches(UUID jobSeekerId) {
        log.info("Cleaning up matches for job seeker {}", jobSeekerId);
        recommendationLogRepository.deleteBySeekerIdCascade(jobSeekerId);
    }
}
