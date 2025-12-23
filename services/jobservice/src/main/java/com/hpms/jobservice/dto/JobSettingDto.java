package com.hpms.jobservice.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobSettingDto {
    private String title ;
    private UUID jobPostId;
    private UUID companyId;
    private UUID creatorId ;
    private List<QuestionDto> questionDtos;
    private long maxApplication;
    private String gender;
    private String educationLevel;
    private SimpleRecruiterDto admin;
    private List<SimpleRecruiterDto> selectedTeamMembers;
    private List<SimpleRecruiterDto> companyTeamMembers;
    private boolean publish;
    private boolean open;

}
