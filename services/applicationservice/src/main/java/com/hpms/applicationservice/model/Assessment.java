package com.hpms.applicationservice.model;

import com.hpms.applicationservice.constants.AssessmentStatus;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Assessment extends Evaluation {

    private LocalDateTime availableTill;

    private String duration;

    private AssessmentStatus assessmentStatus = AssessmentStatus.Delivered;

    private String result;

    public Assessment(Evaluation evaluation, LocalDateTime availableTill, String duration,
                      AssessmentStatus assessmentStatus, String result) {
        super(evaluation.getTitle(), evaluation.getLink(), evaluation.getEvaluationType(), evaluation.getTechnicalType()
                , evaluation.isViewed(), evaluation.getApplication(), evaluation.getCreatorId());
        this.availableTill = availableTill;
        this.duration = duration;
        this.assessmentStatus = assessmentStatus;
        this.result = result;

    }
}
