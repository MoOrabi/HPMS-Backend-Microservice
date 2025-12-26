package com.hpms.applicationservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
public class JobApplicationFormDto {

    private UUID jobPostId;

    private List<QuestionDto> questionDtosList;

    private String jobTitle ;

}
