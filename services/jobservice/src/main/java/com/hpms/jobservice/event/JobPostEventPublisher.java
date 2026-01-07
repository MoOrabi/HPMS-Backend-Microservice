package com.hpms.jobservice.event;

import com.hpms.jobservice.model.JobPost;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "job-events";

    public void publishJobCreated(JobPost jobPost) {
        JobPostEventDTO event = buildEventDTO(jobPost, "JOB_CREATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published JOB_CREATED event for job post: {}", jobPost.getId());
    }

    public void publishJobUpdated(JobPost jobPost) {
        JobPostEventDTO event = buildEventDTO(jobPost, "JOB_UPDATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published JOB_UPDATED event for job post: {}", jobPost.getId());
    }

    public void publishJobPublished(JobPost jobPost, LocalDateTime lastJobUpdate) {
        JobPostEventDTO event = buildEventDTO(jobPost, "JOB_PUBLISHED");
        event.setLastJobUpdate(lastJobUpdate);
        kafkaTemplate.send(TOPIC, event);
        log.info("Published JOB_PUBLISHED event for job post: {}", jobPost.getId());
    }

    public void publishJobClosed(UUID jobPostId) {
        JobPostEventDTO event = JobPostEventDTO.builder()
                .jobPostId(jobPostId)
                .eventType("JOB_CLOSED")
                .build();
        kafkaTemplate.send(TOPIC, event);
        log.info("Published JOB_CLOSED event for job post: {}", jobPostId);
    }

    public void publishJobDeleted(UUID jobPostId) {
        JobPostEventDTO event = JobPostEventDTO.builder()
                .jobPostId(jobPostId)
                .eventType("JOB_DELETED")
                .build();
        kafkaTemplate.send(TOPIC, event);
        log.info("Published JOB_DELETED event for job post: {}", jobPostId);
    }

    private JobPostEventDTO buildEventDTO(JobPost jobPost, String eventType) {
        return JobPostEventDTO.builder()
                .jobPostId(jobPost.getId())
                .jobTitle(jobPost.getJobTitle())
                .jobType(jobPost.getJobType() != null ? jobPost.getJobType().name() : null)
                .employmentType(jobPost.getEmploymentType() != null ? jobPost.getEmploymentType().name() : null)
                .minExperienceYears(jobPost.getMinExperienceYears())
                .maxExperienceYears(jobPost.getMaxExperienceYears())
                .minSalary(jobPost.getMinSalary())
                .maxSalary(jobPost.getMaxSalary())
                .currency(jobPost.getCurrency())
                .skillIds(jobPost.getSkillIds())
                .industryId(jobPost.getIndustryId())
                .jobNameId(jobPost.getJobNameId())
                .companyId(jobPost.getCompanyId())
                .open(jobPost.isOpen())
                .deleted(jobPost.isDeleted())
                .publishedOn(jobPost.getPublishedOn())
                .eventType(eventType)
                .build();
    }
}
