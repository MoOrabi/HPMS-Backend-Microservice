package com.hpms.recommendationservice.listener;

import com.hpms.recommendationservice.dto.JobSeekerEvent;
import com.hpms.recommendationservice.model.JobSeekerProfile;
import com.hpms.recommendationservice.repository.JobSeekerProfileRepository;
import com.hpms.recommendationservice.service.MatchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerEventListener {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final MatchingService matchingService;

    @KafkaListener(
            topics = "jobseeker-events",
            groupId = "recommendation-service-jobseeker-consumer"
    )
    @Transactional
    public void handleJobSeekerEvent(JobSeekerEvent event) {
        log.info("Received job seeker event: {}", event);

        switch (event.getEventType()) {
            case "PROFILE_UPDATED":
                if(event.isMatchingPropertyChanged()) {
                    syncJobSeekerProfile(event);
                } else {
                    handleSkillsUpdate(event);
                }
                break;
            case "PROFILE_DELETED":
                handleProfileDeletion(event.getJobSeekerId());
                break;
        }
    }

    private void syncJobSeekerProfile(JobSeekerEvent event) {
        JobSeekerProfile profile = JobSeekerProfile.builder()
                .jobSeekerId(event.getJobSeekerId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .jobTitle(event.getJobTitle())
                .yearsOfExperience(event.getYearsOfExperience())
                .careerLevel(event.getCareerLevel())
                .skills(event.getSkills())
                .jobTypesInterestedIn(event.getJobTypesInterestedIn())
                .readyToRelocate(event.isReadyToRelocate())
                .lastProfileUpdate(event.getLastProfileUpdate())
                .lastSkillsUpdate(event.getLastSkillsUpdate())
                .build();
        jobSeekerProfileRepository.save(profile);

        log.info("Synced job seeker profile: {}", event.getJobSeekerId());
    }

    private void handleSkillsUpdate(JobSeekerEvent event) {
        log.info("Handling skills update for job seeker: {}", event.getJobSeekerId());

        // Validate update frequency
        JobSeekerProfile existingProfile = jobSeekerProfileRepository.findById(event.getJobSeekerId())
                .orElse(null);

        if (existingProfile != null && existingProfile.getLastSkillsUpdate() != null) {
            if (!matchingService.canUpdateProfile(existingProfile.getLastSkillsUpdate())) {
                log.warn("Job seeker {} attempted to update skills too frequently", event.getJobSeekerId());
                return;
            }
        }

        // Sync profile with updated skills
        syncJobSeekerProfile(event);

        // Re-match with all active jobs
        matchingService.rematchJobSeekerWithJobs(event.getJobSeekerId());

        log.info("Completed skills update and re-matching for job seeker: {}", event.getJobSeekerId());
    }

    private void handleProfileDeletion(UUID jobSeekerId) {
        log.info("Handling profile deletion for job seeker: {}", jobSeekerId);

        // Delete profile
        jobSeekerProfileRepository.deleteById(jobSeekerId);

        // Clean up all related recommendation logs
        matchingService.cleanupSeekerMatches(jobSeekerId);

        log.info("Deleted profile and recommendations for job seeker: {}", jobSeekerId);
    }
}
