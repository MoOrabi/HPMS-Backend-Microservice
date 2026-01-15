package com.hpms.jobservice.event;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.jobservice.dto.CompanyNameLogoAndLocation;
import com.hpms.jobservice.model.JobPost;
import com.hpms.jobservice.service.client.ReferenceServiceClient;
import com.hpms.jobservice.service.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobPostEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "job-events";
    private final UserServiceClient userServiceClient;
    private final ReferenceServiceClient referenceServiceClient;

    public void publishJobMatchingPropUpdated(JobPost jobPost) {
        JobPostEventDTO event = buildEventDTO(jobPost, "JOB_MATCHING_PROP_UPDATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published JOB_MATCHING_PROP_UPDATED event for job post: {}", jobPost.getId());
    }

    public void publishJobUpdated(JobPost jobPost) {
        JobPostEventDTO event = buildEventDTO(jobPost, "JOB_UPDATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published JOB_UPDATED event for job post: {}", jobPost.getId());
    }

    public void publishJobPublished(JobPost jobPost) {
        JobPostEventDTO event = buildEventDTO(jobPost, "JOB_PUBLISHED");
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
        CompanyNameLogoAndLocation companyLocationAndLogoDTO = userServiceClient
                .getCompanyNameLogoAndLocation(jobPost.getCompanyId());
        Set<SelectOption> skills = referenceServiceClient.getSkillsNames(jobPost.getSkillIds());
        return JobPostEventDTO.builder()
                .jobPostId(jobPost.getId())
                .jobTitle(jobPost.getJobTitle())
                .jobType(jobPost.getJobType() != null ? jobPost.getJobType().name() : null)
                .employmentType(jobPost.getEmploymentType() != null ? jobPost.getEmploymentType().name() : null)
                .minExperienceYears(jobPost.getMinExperienceYears())
                .maxExperienceYears(jobPost.getMaxExperienceYears())
                .skills(skills)
                .companyId(jobPost.getCompanyId())
                .companyName(companyLocationAndLogoDTO.getCompanyName())
                .city(companyLocationAndLogoDTO.getCity())
                .country(companyLocationAndLogoDTO.getCountry())
                .companyLogo(companyLocationAndLogoDTO.getLogo())
                .open(jobPost.isOpen())
                .deleted(jobPost.isDeleted())
                .publishedOn(jobPost.getPublishedOn())
                .eventType(eventType)
                .build();
    }
}
