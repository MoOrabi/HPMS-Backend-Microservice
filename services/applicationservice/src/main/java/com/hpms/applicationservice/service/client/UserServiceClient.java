package com.hpms.applicationservice.service.client;

import com.hpms.applicationservice.dto.ApplicantDTO;
import com.hpms.applicationservice.dto.JobSeekerAllInfoDTO;
import com.hpms.applicationservice.dto.CreatorNameAndPhoto;
import com.hpms.commonlib.constants.RoleEnum;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange(url = "http://user-service/api/users/apps")
public interface UserServiceClient {

    @GetExchange("/get-role/{user-id}")
    RoleEnum getUserRole(@PathVariable(name = "user-id") UUID userId);

    @GetExchange("/applicant-info/{userId}")
    ApplicantDTO getApplicantInfo(@PathVariable UUID userId);

    @GetExchange("/creator-name-photo/{user-id}")
    CreatorNameAndPhoto getCreatorNameAndPhoto(@PathVariable(name = "user-id") UUID userId);

    @GetExchange("/creator-name/{user-id}")
    String getCreatorName(@PathVariable(name = "user-id") UUID userId);

    @GetExchange("/jobseeker-all-info/{user-id}")
    JobSeekerAllInfoDTO getJobSeekerAllInfo(@PathVariable(name = "user-id") UUID userId);
}
