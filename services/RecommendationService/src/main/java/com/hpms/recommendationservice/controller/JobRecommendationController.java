package com.hpms.recommendationservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.recommendationservice.dto.JobRecommendationDTO;
import com.hpms.recommendationservice.service.JobRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Recommendations")
public class JobRecommendationController {

    private final JobRecommendationService jobRecommendationService;

    @GetMapping("/{jobSeekerId}")
    @Operation(summary = "Get job recommendations for a job seeker")
    public ApiResponse<?> getJobRecommendations(
            @PathVariable UUID jobSeekerId,
            @RequestParam(defaultValue = "20") int limit) {

        List<JobRecommendationDTO> recommendations =
                jobRecommendationService.recommendJobsForSeeker(jobSeekerId, limit);

        // Check if no recommendations and provide helpful message
        if (recommendations.isEmpty()) {
            String advice = jobRecommendationService.generateNoRecommendationsAdvice(jobSeekerId);
            return ApiResponse.builder()
                    .body(recommendations)
                    .message(advice)
                    .ok(true)
                    .build();
        }

        return ApiResponse.builder()
                .body(recommendations)
                .message("Job recommendations retrieved successfully")
                .ok(true)
                .build();
    }
}
