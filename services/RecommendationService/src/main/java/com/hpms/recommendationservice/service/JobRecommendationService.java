package com.hpms.recommendationservice.service;

import com.hpms.commonlib.handler.ResourceNotFoundException;
import com.hpms.recommendationservice.dto.JobRecommendationDTO;
import com.hpms.recommendationservice.model.JobPostProfile;
import com.hpms.recommendationservice.model.JobSeekerProfile;
import com.hpms.recommendationservice.model.RecommendationLog;
import com.hpms.recommendationservice.repository.JobPostProfileRepository;
import com.hpms.recommendationservice.repository.JobSeekerProfileRepository;
import com.hpms.recommendationservice.repository.RecommendationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobRecommendationService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobPostProfileRepository jobPostProfileRepository;
    private final RecommendationLogRepository recommendationLogRepository;

    public List<JobRecommendationDTO> recommendJobsForSeeker(
            UUID jobSeekerId,
            int limit) {

        log.info("Getting job recommendations for job seeker: {}", jobSeekerId);

        List<RecommendationLog> recommendationLogs = recommendationLogRepository.findByJobPostIdOrderByScoreAscWithLimit(jobSeekerId, limit);
        // Convert to DTOs
        return recommendationLogs.stream()
                .map(this::toJobRecommendationDTO)
                .toList();
    }

    private JobRecommendationDTO toJobRecommendationDTO(RecommendationLog recommendationLog) {
        UUID postId = recommendationLog.getJobPostId();
        Optional<JobPostProfile> jobPostProfile = jobPostProfileRepository.findById(postId);
        if(jobPostProfile.isEmpty()) {
            return null;
        }
        JobPostProfile job = jobPostProfile.get();

        return JobRecommendationDTO.builder()
                .jobPostId(job.getJobPostId())
                .jobTitle(job.getJobTitle())
                .city(job.getCity())
                .country(job.getCountry())
                .jobType(job.getJobType())
                .employmentType(job.getEmploymentType())
                .companyId(job.getCompanyId())
                .companyName(job.getCompanyName())
                .matchScore(recommendationLog.getScore())
                .matchReasons(recommendationLog.getMatchReasons())
                .build();
    }

    /**
     * Generate helpful advice for job seekers with no recommendations
     */
    public String generateNoRecommendationsAdvice(UUID jobSeekerId) {
        JobSeekerProfile seeker = jobSeekerProfileRepository.findById(jobSeekerId)
                .orElseThrow(() -> new ResourceNotFoundException("Job seeker not found"));

        StringBuilder advice = new StringBuilder("We couldn't find any job recommendations at the moment. Here's how to improve your matches:\n\n");
        boolean hasIssues = false;

        if (seeker.getSkills() == null || seeker.getSkills().isEmpty()) {
            advice.append("• Add your skills to help us match you with relevant jobs\n");
            hasIssues = true;
        }

        if (seeker.getYearsOfExperience() == null || seeker.getYearsOfExperience().isEmpty()) {
            advice.append("• Specify your years of experience\n");
            hasIssues = true;
        }

        if (seeker.getJobTypesInterestedIn() == null || seeker.getJobTypesInterestedIn().isEmpty()) {
            advice.append("• Select the job types you're interested in (Full-time, Part-time, etc.)\n");
            hasIssues = true;
        }

        if (!hasIssues) {
            advice = new StringBuilder("Your profile looks complete! We'll notify you when new jobs matching your profile become available.");
        } else {
            advice.append("\nComplete your profile to increase your chances of finding the perfect job!");
        }

        return advice.toString();
    }

}
