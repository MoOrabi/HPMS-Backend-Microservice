package com.hpms.userservice.event;

import com.hpms.userservice.model.jobseeker.JobSeeker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "jobseeker-events";

    public void publishProfileCreated(JobSeeker jobSeeker) {
        JobSeekerEventDTO event = buildEventDTO(jobSeeker, "PROFILE_CREATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published PROFILE_CREATED event for job seeker: {}", jobSeeker.getId());
    }

    public void publishProfileUpdated(JobSeeker jobSeeker) {
        JobSeekerEventDTO event = buildEventDTO(jobSeeker, "PROFILE_UPDATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published PROFILE_UPDATED event for job seeker: {}", jobSeeker.getId());
    }

    public void publishSkillsUpdated(JobSeeker jobSeeker, LocalDateTime lastSkillsUpdate) {
        JobSeekerEventDTO event = buildEventDTO(jobSeeker, "SKILLS_UPDATED");
        event.setLastSkillsUpdate(lastSkillsUpdate);
        kafkaTemplate.send(TOPIC, event);
        log.info("Published SKILLS_UPDATED event for job seeker: {}", jobSeeker.getId());
    }

    public void publishProfileDeleted(UUID jobSeekerId) {
        JobSeekerEventDTO event = JobSeekerEventDTO.builder()
                .jobSeekerId(jobSeekerId)
                .eventType("PROFILE_DELETED")
                .build();
        kafkaTemplate.send(TOPIC, event);
        log.info("Published PROFILE_DELETED event for job seeker: {}", jobSeekerId);
    }

    private JobSeekerEventDTO buildEventDTO(JobSeeker jobSeeker, String eventType) {
        return JobSeekerEventDTO.builder()
                .jobSeekerId(jobSeeker.getId())
                .firstName(jobSeeker.getFirstName())
                .lastName(jobSeeker.getLastName())
                .jobTitle(jobSeeker.getJobTitle())
                .yearsOfExperience(jobSeeker.getYearsOfExperience())
                .careerLevel(jobSeeker.getCareerLevel() != null ? jobSeeker.getCareerLevel().name() : null)
                .minimumSalaryValue(jobSeeker.getMinimumSalaryValue())
                .minimumSalaryCurrency(jobSeeker.getMinimumSalaryCurrency())
                .skills(jobSeeker.getSkills())
                .jobTypesInterestedIn(jobSeeker.getJobsTypesUserInterestedIn())
                .jobsInterestedIn(jobSeeker.getJobsUserInterestedIn())
                .readyToRelocate(jobSeeker.isReadyToRelocate())
                .searchable(jobSeeker.getSearchable())
                .openToSuggest(jobSeeker.getOpenToSuggest())
                .eventType(eventType)
                .build();
    }
}
