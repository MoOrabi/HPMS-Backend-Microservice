package com.hpms.recommendationservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.recommendationservice.dto.CandidateRecommendationDTO;
import com.hpms.recommendationservice.service.CandidateRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
            @RequestParam(defaultValue = "50") int limit) {

        List<CandidateRecommendationDTO> recommendations =
                candidateRecommendationService.recommendCandidatesForJob(jobPostId, limit);

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