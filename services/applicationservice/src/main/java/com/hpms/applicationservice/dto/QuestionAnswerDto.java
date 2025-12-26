package com.hpms.applicationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class QuestionAnswerDto {

    @NotNull
    private UUID questionId;

    @NotNull
    @NotBlank
    private String questionAnswer ;

}
