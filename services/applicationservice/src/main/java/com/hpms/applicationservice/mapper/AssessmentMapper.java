package com.hpms.applicationservice.mapper;

import com.hpms.applicationservice.dto.AssessmentDTO;
import com.hpms.applicationservice.model.Assessment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

public class AssessmentMapper {

    public Assessment assessmentDTOToAssessment(AssessmentDTO assessmentDTO) {
        return Assessment.builder()
                .assessmentStatus(assessmentDTO.getStatus())
                .link(assessmentDTO.getLink())
                .updatedAt(LocalDateTime.now())
                .availableTill(assessmentDTO.getAvailableTill())
                .duration(assessmentDTO.getDuration())
                .title(assessmentDTO.getTitle())
                .technicalType(assessmentDTO.getTechnicalType())
                .result(assessmentDTO.getResult())
                .build();
    }
}
