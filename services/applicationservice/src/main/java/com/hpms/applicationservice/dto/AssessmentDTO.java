package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.AssessmentStatus;
import com.hpms.applicationservice.model.Assessment;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Builder
public class AssessmentDTO extends EvaluationDTO {

    private LocalDateTime availableTill;

    private String duration;

    private AssessmentStatus status;

    public AssessmentDTO(Assessment assessment) {
        setId(assessment.getId());
        setTitle(assessment.getTitle());
        setCreatedAt(assessment.getCreatedAt());
        setUpdatedAt(assessment.getUpdatedAt());
        setLink(assessment.getLink());
        setTechnicalType(assessment.getTechnicalType());
        setResult(assessment.getResult());
        setApplicationId(assessment.getApplication().getId());
        setAvailableTill(assessment.getAvailableTill());
        setDuration(assessment.getDuration());
        setStatus(assessment.getAssessmentStatus());
    }
}
