package com.hpms.recommendationservice.listener;

import com.hpms.recommendationservice.dto.JobPostEvent;
import com.hpms.recommendationservice.model.JobPostProfile;
import com.hpms.recommendationservice.repository.JobPostProfileRepository;
import com.hpms.recommendationservice.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostEventListener {

    private final JobPostProfileRepository jobPostProfileRepository;
    private final MatchingService matchingService;

    @KafkaListener(
            topics = "job-events",
            groupId = "recommendation-service-job-consumer"
    )
    @Transactional
    public void handleJobPostEvent(JobPostEvent event) {
        log.info("Received job post event: {}", event);

        switch (event.getEventType()) {
            case "JOB_CREATED":
            case "JOB_UPDATED":
                if(event.isMatchingPropertyChanged()) {
                    handleJobPublished(event);
                } else {
                    syncJobPostProfile(event);
                }
                break;
            case "JOB_PUBLISHED":
                handleJobPublished(event);
                break;
            case "JOB_CLOSED":
                handleJobClosed(event.getJobPostId());
                break;
            case "JOB_DELETED":
                handleJobDeleted(event.getJobPostId());
                break;
        }
    }

    private void syncJobPostProfile(JobPostEvent event) {
        JobPostProfile profile = JobPostProfile.builder()
                .jobPostId(event.getJobPostId())
                .jobTitle(event.getJobTitle())
                .jobType(event.getJobType())
                .employmentType(event.getEmploymentType())
                .minExperienceYears(event.getMinExperienceYears())
                .maxExperienceYears(event.getMaxExperienceYears())
                .skills(event.getSkills())
                .companyId(event.getCompanyId())
                .companyName(event.getCompanyName())
                .publishedOn(event.getPublishedOn())
                .lastJobUpdate(event.getLastJobUpdate())
                .build();

        jobPostProfileRepository.save(profile);
        log.info("Synced job post profile: {}", event.getJobPostId());
    }

    private void handleJobPublished(JobPostEvent event) {
        log.info("Handling job published event for job post: {}", event.getJobPostId());

        // Validate update frequency
        JobPostProfile existingProfile = jobPostProfileRepository.findById(event.getJobPostId())
                .orElse(null);

        if (existingProfile != null && existingProfile.getLastJobUpdate() != null) {
            if (!matchingService.canUpdateProfile(existingProfile.getLastJobUpdate())) {
                log.warn("Job post {} was updated too frequently", event.getJobPostId());
                return;
            }
        }

        // Sync profile
        syncJobPostProfile(event);

        // Match with all searchable job seekers
        matchingService.matchJobWithSeekers(event.getJobPostId());

        log.info("Completed job published and matching for job post: {}", event.getJobPostId());
    }

    private void handleJobClosed(java.util.UUID jobPostId) {
        log.info("Handling job closed event for job post: {}", jobPostId);

        JobPostProfile profile = jobPostProfileRepository.findById(jobPostId).orElse(null);
        if (profile != null) {
            profile.setOpen(false);
            jobPostProfileRepository.save(profile);
        }

        // Clean up all related recommendation logs
        matchingService.cleanupJobMatches(jobPostId);

        log.info("Closed job and cleaned up recommendations for job post: {}", jobPostId);
    }

    private void handleJobDeleted(java.util.UUID jobPostId) {
        log.info("Handling job deleted event for job post: {}", jobPostId);

        // Delete profile
        jobPostProfileRepository.deleteById(jobPostId);

        // Clean up all related recommendation logs
        matchingService.cleanupJobMatches(jobPostId);

        log.info("Deleted job and recommendations for job post: {}", jobPostId);
    }
}