package com.hpms.jobservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
public class QuestionDto {

    public UUID id ;

    @NotNull
    @NotBlank
    public String question;

    @NotNull
    @NotBlank
    public String questionType;

    public QuestionDto(UUID id, String question, String questionType) {
        this.id = id;
        this.question = question;
        this.questionType = questionType;
    }
}
