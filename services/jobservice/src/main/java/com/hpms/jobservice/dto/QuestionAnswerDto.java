package com.hpms.jobservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDto {

    @NotNull
    private UUID questionId;

    @NotNull
    @NotBlank
    private String questionAnswer ;

}
