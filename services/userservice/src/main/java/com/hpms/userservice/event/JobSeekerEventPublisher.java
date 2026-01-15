package com.hpms.userservice.event;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.userservice.model.Education;
import com.hpms.userservice.model.jobseeker.JobExperience;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.repository.jobseeker.EducationRepository;
import com.hpms.userservice.repository.jobseeker.JobExperienceRepository;
import com.hpms.userservice.service.client.ReferenceServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobSeekerEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final JobExperienceRepository jobExperienceRepository;
    private final EducationRepository educationRepository;
    private final ReferenceServiceClient referenceServiceClient;

    private static final String TOPIC = "jobseeker-events";

    public void publishMatchPropUpdated(JobSeeker jobSeeker) {
        JobSeekerEventDTO event = buildEventDTO(jobSeeker, "MATCHING_PROP_UPDATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published PROFILE_UPDATED event for job seeker: {}", jobSeeker.getId());
    }

    public void publishProfileUpdated(JobSeeker jobSeeker) {
        JobSeekerEventDTO event = buildEventDTO(jobSeeker, "PROFILE_UPDATED");
        kafkaTemplate.send(TOPIC, event);
        log.info("Published PROFILE_UPDATED event for job seeker: {}", jobSeeker.getId());
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
        JobExperience experience = jobExperienceRepository.findFirstDistinctByJobSeekerIdOrderByStartYearDescStartMonthDesc(jobSeeker.getId());
        Education education = educationRepository.findFirstDistnictByJobSeekerIdOrderByStartDesc(jobSeeker.getId());
        Set<SelectOption> skills = referenceServiceClient.getSkillsNames(jobSeeker.getSkillIds());
        return JobSeekerEventDTO.builder()
                .jobSeekerId(jobSeeker.getId())
                .firstName(jobSeeker.getFirstName())
                .lastName(jobSeeker.getLastName())
                .photo(jobSeeker.getProfilePhoto())
                .jobTitle(jobSeeker.getJobTitle())
                .yearsOfExperience(jobSeeker.getYearsOfExperience())
                .careerLevel(jobSeeker.getCareerLevel() != null ? jobSeeker.getCareerLevel().name() : null)
                .skills(skills)
                .jobTypesInterestedIn(jobSeeker.getJobsTypesUserInterestedIn())
                .lastJobOrganizationName(experience!=null ? experience.getPlace() : null)
                .lastJobTitle(experience!=null ? experience.getName() : null)
                .lastJobStartedAt(experience!=null ? experience.getStartYear() : null)
                .lastJobEndedAt(experience!=null ? experience.getEndYear() : null)
                .highestDegreeInstitute(education!=null ? education.getInstitution() : null)
                .highestDegreeName(education!=null ? education.getDegree() : null)
                .readyToRelocate(jobSeeker.isReadyToRelocate())
                .openToSuggest(jobSeeker.getOpenToSuggest())
                .eventType(eventType)
                .build();
    }
}
