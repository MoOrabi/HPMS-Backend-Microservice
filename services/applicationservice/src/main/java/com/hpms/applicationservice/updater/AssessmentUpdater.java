package com.hpms.applicationservice.updater;

import com.hpms.applicationservice.dto.AssessmentDTO;
import com.hpms.applicationservice.model.Assessment;
import com.hpms.applicationservice.repository.AssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class AssessmentUpdater implements BiFunction<Assessment, AssessmentDTO, Assessment> {

    private final AssessmentRepository assessmentRepository;

    @Override
    public Assessment apply(Assessment assessment, AssessmentDTO assessmentDTO) {

        assessment.setAssessmentStatus(assessmentDTO.getStatus());
        assessment.setUpdatedAt(LocalDateTime.now());
        assessment.setAvailableTill(assessmentDTO.getAvailableTill());
        assessment.setDuration(assessmentDTO.getDuration());
        assessment.setResult(assessmentDTO.getResult());
        assessment.setLink(assessmentDTO.getLink());
        assessment.setTitle(assessmentDTO.getTitle());
        assessment.setTechnicalType(assessmentDTO.getTechnicalType());

        return assessmentRepository.save(assessment);
    }
}
