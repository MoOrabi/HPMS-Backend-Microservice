package com.hpms.recommendationservice.scheduler;

import com.hpms.recommendationservice.model.JobPostProfile;
import com.hpms.recommendationservice.model.JobSeekerProfile;
import com.hpms.recommendationservice.model.RecommendationLog;
import com.hpms.recommendationservice.repository.JobPostProfileRepository;
import com.hpms.recommendationservice.repository.JobSeekerProfileRepository;
import com.hpms.recommendationservice.repository.RecommendationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MatchCleanupScheduler {

    private final RecommendationLogRepository recommendationLogRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobPostProfileRepository jobPostProfileRepository;

    private static final int MAX_SEEKER_MATCHES = 30;
    private static final int MAX_JOB_MATCHES = 10;

    /**
     * Run cleanup job daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void cleanupExcessMatches() {
        log.info("Starting match cleanup job...");

        try {
            // Clean up excess matches for job seekers
            cleanupSeekerMatches();

            // Clean up excess matches for job posts
            cleanupJobMatches();

            log.info("Match cleanup job completed successfully");
        } catch (Exception e) {
            log.error("Error during match cleanup job", e);
        }
    }

    private void cleanupSeekerMatches() {
        log.info("Cleaning up excess job seeker matches...");

        List<UUID> seekerIds = jobSeekerProfileRepository.findAll()
                .stream()
                .map(JobSeekerProfile::getJobSeekerId)
                .toList();

        int totalDeleted = 0;
        for (UUID seekerId : seekerIds) {
            long matchCount = recommendationLogRepository.countBySeekerId(
                    seekerId);

            if (matchCount > MAX_SEEKER_MATCHES) {
                // Get all matches sorted by score
                List<RecommendationLog> matches = recommendationLogRepository
                        .findBySeekerIdOrderByScoreDesc(seekerId);

                // Keep top MAX_SEEKER_MATCHES, delete the rest
                List<RecommendationLog> toDelete = matches.subList(MAX_SEEKER_MATCHES, matches.size());

                // Validate that deletion won't affect jobs below their limit
                toDelete = toDelete.stream()
                        .filter(match -> canDeleteMatch(match.getJobPostId()))
                        .toList();

                recommendationLogRepository.deleteAll(toDelete);
                totalDeleted += toDelete.size();

                log.debug("Deleted {} excess matches for job seeker {}", toDelete.size(), seekerId);
            }
        }

        log.info("Cleaned up {} excess job seeker matches", totalDeleted);
    }

    private void cleanupJobMatches() {
        log.info("Cleaning up excess job post matches...");

        List<UUID> jobPostIds = jobPostProfileRepository.findAll()
                .stream()
                .map(JobPostProfile::getJobPostId)
                .toList();

        int totalDeleted = 0;
        for (UUID jobPostId : jobPostIds) {
            long matchCount = recommendationLogRepository.countByJobPostId(
                    jobPostId);


            if (matchCount > MAX_JOB_MATCHES) {
                // Get all matches sorted by score
                List<RecommendationLog> matches = recommendationLogRepository
                        .findByJobPostIdOrderByScoreDesc(jobPostId);

                // Keep top MAX_JOB_MATCHES, delete the rest
                List<RecommendationLog> toDelete = matches.subList(MAX_JOB_MATCHES, matches.size());

                // Validate that deletion won't affect seekers below their limit
                toDelete = toDelete.stream()
                        .filter(match -> canDeleteSeekerMatch(match.getJobSeekerId()))
                        .toList();

                recommendationLogRepository.deleteAll(toDelete);
                totalDeleted += toDelete.size();

                log.debug("Deleted {} excess matches for job post {}", toDelete.size(), jobPostId);
            }
        }

        log.info("Cleaned up {} excess job post matches", totalDeleted);
    }

    /**
     * Check if a match can be deleted without bringing job below its minimum limit
     */
    private boolean canDeleteMatch(UUID jobPostId) {
        long currentMatches = recommendationLogRepository.countByJobPostId(
                jobPostId);
        return currentMatches > MAX_JOB_MATCHES;
    }

    /**
     * Check if a seeker match can be deleted without bringing seeker below minimum limit
     */
    private boolean canDeleteSeekerMatch(UUID jobSeekerId) {
        long currentMatches = recommendationLogRepository.countBySeekerId(
                jobSeekerId);
        return currentMatches > MAX_SEEKER_MATCHES;
    }
}
