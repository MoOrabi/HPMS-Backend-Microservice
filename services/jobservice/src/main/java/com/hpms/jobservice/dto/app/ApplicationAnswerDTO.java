package com.hpms.jobservice.dto.app;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Builder
public class ApplicationAnswerDTO {

    public UUID questionId ;

    public String question;

    public String questionType;

    private String questionAnswer ;

}
