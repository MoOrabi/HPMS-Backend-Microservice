package com.hpms.recommendationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationScore {
    private double totalScore;
    private List<String> reasons;
}
