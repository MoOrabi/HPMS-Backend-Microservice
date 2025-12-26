package com.hpms.applicationservice.dto;

import com.hpms.applicationservice.model.QuestionAnswer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ApplicantProfileResponse {

    private UUID applicationId;

    private JobSeekerAllInfoDTO jobSeekerInfo;

    private List<QuestionAnswer> questionAnswers;

    private String status;


}
