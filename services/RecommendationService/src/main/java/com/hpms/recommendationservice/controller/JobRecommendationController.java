package com.hpms.recommendationservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.dto.PageResponse;
import com.hpms.recommendationservice.dto.JobRecommendationDTO;
import com.hpms.recommendationservice.service.JobRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Recommendations")
public class JobRecommendationController {

    private final JobRecommendationService jobRecommendationService;

    @GetMapping
    @Operation(summary = "Get job recommendations for a job seeker")
    public ApiResponse<?> getJobRecommendations(
            @RequestHeader(name = "Authorization") String token,
            @RequestParam(defaultValue = "10", name = "page_size") int pageSize,
            @RequestParam(defaultValue = "0", name = "page_number") int pageNumber) {

        PageResponse<JobRecommendationDTO> recommendations =
                jobRecommendationService.recommendJobsForSeeker(token, pageSize, pageNumber);

        // Check if no recommendations and provide helpful message
        if (recommendations.isEmpty()) {
            String advice = jobRecommendationService.generateNoRecommendationsAdvice(token);
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
