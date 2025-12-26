package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.constants.TechnicalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EvaluationDTO {

    private UUID id;

    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String link;

    private TechnicalType technicalType;

    private String result;

    private UUID applicationId;

}
