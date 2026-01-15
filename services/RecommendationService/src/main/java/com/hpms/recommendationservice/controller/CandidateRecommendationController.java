package com.hpms.recommendationservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.dto.PageResponse;
import com.hpms.recommendationservice.dto.CandidateRecommendationDTO;
import com.hpms.recommendationservice.service.CandidateRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations/candidates")
@RequiredArgsConstructor
@Tag(name = "Candidate Recommendations")
public class CandidateRecommendationController {

    private final CandidateRecommendationService candidateRecommendationService;

    @GetMapping("/{jobPostId}")
    @Operation(summary = "Get candidate recommendations for a job post")
    public ApiResponse<?> getCandidateRecommendations(
            @PathVariable UUID jobPostId,
            @RequestParam(defaultValue = "5", name = "page_size") int pageSize,
            @RequestParam(defaultValue = "0", name = "page_number") int pageNumber) {

        PageResponse<CandidateRecommendationDTO> recommendations =
                candidateRecommendationService.recommendCandidatesForJob(jobPostId, pageSize, pageNumber);

        // Check if no recommendations and provide helpful message
        if (recommendations.isEmpty()) {
            String advice = candidateRecommendationService.generateNoCandidatesAdvice(jobPostId);
            return ApiResponse.builder()
                    .body(recommendations)
                    .message(advice)
                    .ok(true)
                    .build();
        }

        return ApiResponse.builder()
                .body(recommendations)
                .message("Candidate recommendations retrieved successfully")
                .ok(true)
                .build();
    }
}