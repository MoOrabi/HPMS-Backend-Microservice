package com.hpms.applicationservice.model;

import com.hpms.applicationservice.constants.InterviewStatus;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Interview extends Evaluation {


    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private InterviewStatus interviewStatus = InterviewStatus.Delivered;

    public Interview(Evaluation evaluation, LocalDateTime startTime, LocalDateTime endTime, InterviewStatus interviewStatus) {
        super(evaluation.getTitle(), evaluation.getLink(), evaluation.getEvaluationType(), evaluation.getTechnicalType(),
                evaluation.isViewed(), evaluation.getApplication(), evaluation.getCreatorId());
        this.startTime = startTime;
        this.endTime = endTime;
        this.interviewStatus = interviewStatus;
    }

}
