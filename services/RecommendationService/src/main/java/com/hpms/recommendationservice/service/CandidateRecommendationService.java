package com.hpms.recommendationservice.service;

import com.hpms.commonlib.handler.ResourceNotFoundException;
import com.hpms.recommendationservice.dto.CandidateRecommendationDTO;
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
public class CandidateRecommendationService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final JobPostProfileRepository jobPostProfileRepository;
    private final RecommendationLogRepository recommendationLogRepository;

    public List<CandidateRecommendationDTO> recommendCandidatesForJob(
            UUID jobPostId,
            int limit) {

        log.info("Getting candidate recommendations for job post: {}", jobPostId);

        List<RecommendationLog> recommendationLogs = recommendationLogRepository.findByJobPostIdOrderByScoreAscWithLimit(jobPostId, limit);
        // Convert to DTOs
        return recommendationLogs.stream()
                .map(this::toCandidateRecommendationDTO)
                .toList();
    }
    private CandidateRecommendationDTO toCandidateRecommendationDTO(RecommendationLog recommendationLog) {
        UUID seekerId = recommendationLog.getJobSeekerId();
        Optional<JobSeekerProfile> seekerProfile = jobSeekerProfileRepository.findById(seekerId);
        if(seekerProfile.isEmpty()) {
            return null;
        }
        JobSeekerProfile seeker = seekerProfile.get();

        return CandidateRecommendationDTO.builder()
                .jobSeekerId(seeker.getJobSeekerId())
                .firstName(seeker.getFirstName())
                .lastName(seeker.getLastName())
                .careerLevel(seeker.getCareerLevel())
                .jobTitle(seeker.getJobTitle())
                .skills(seeker.getSkills())
                .highestDegreeName(seeker.getHighestDegreeName())
                .highestDegreeInstitute(seeker.getHighestDegreeInstitute())
                .lastJobTitle(seeker.getLastJobTitle())
                .lastJobOrganizationName(seeker.getLastJobOrganizationName())
                .lastJobStartedAt(seeker.getLastJobStartedAt())
                .lastJobEndedAt(seeker.getLastJobEndedAt())
                .matchScore(recommendationLog.getScore())
                .matchReasons(recommendationLog.getMatchReasons())
                .build();
    }

    /**
     * Generate helpful advice for jobs with no candidate recommendations
     */
    public String generateNoCandidatesAdvice(UUID jobPostId) {
        JobPostProfile job = jobPostProfileRepository.findById(jobPostId)
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found"));

        StringBuilder advice = new StringBuilder("No matching candidates found at the moment. Here's how to attract more candidates:\n\n");
        boolean hasIssues = false;

        if (job.getSkills() == null || job.getSkills().isEmpty()) {
            advice.append("• Add required skills to your job posting\n");
            hasIssues = true;
        }

        if (job.getMinExperienceYears() == 0 && job.getMaxExperienceYears() == 0) {
            advice.append("• Specify the required experience range\n");
            hasIssues = true;
        }

        if (job.getJobType() == null || job.getJobType().isEmpty()) {
            advice.append("• Specify the job type (Remote, On-site, Hybrid)\n");
            hasIssues = true;
        }

        if (!job.isOpen()) {
            advice.append("• Open your job post to receive applications\n");
            hasIssues = true;
        }

        if (!hasIssues) {
            advice = new StringBuilder("Your job posting looks complete! Matching candidates will be available soon.");
        } else {
            advice.append("\nComplete your job posting details to attract the right candidates!");
        }

        return advice.toString();
    }

}
