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
public class QuestionDto {

    public UUID id ;


    @NotNull
    @NotBlank
    public String question;

    @NotNull
    @NotBlank
    public String questionType;



}
