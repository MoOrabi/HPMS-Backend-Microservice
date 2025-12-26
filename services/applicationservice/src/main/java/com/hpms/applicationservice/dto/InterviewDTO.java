package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.InterviewStatus;
import com.hpms.applicationservice.model.Interview;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Builder
public class InterviewDTO extends EvaluationDTO {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private InterviewStatus status;

    public InterviewDTO(Interview interview) {
        setId(interview.getId());
        setTitle(interview.getTitle());
        setCreatedAt(interview.getCreatedAt());
        setUpdatedAt(interview.getUpdatedAt());
        setLink(interview.getLink());
        setTechnicalType(interview.getTechnicalType());
        setApplicationId(interview.getApplication().getId());
        setStartTime(interview.getStartTime());
        setEndTime(interview.getEndTime());
        setStatus(interview.getInterviewStatus());
    }

}
