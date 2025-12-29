package com.hpms.userservice.controller;

import com.hpms.commonlib.constants.RoleEnum;
import com.hpms.userservice.dto.app.ApplicantDTO;
import com.hpms.userservice.dto.app.JobSeekerAllInfoForAppDTO;
import com.hpms.userservice.dto.app.TimelineEventCreatorNameAndPhoto;
import com.hpms.userservice.service.impl.AppRelatedServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/apps")
@RequiredArgsConstructor
public class AppRelatedController {
    private final AppRelatedServiceImpl appRelatedServiceImpl;

    @GetMapping("/get-role/{user-id}")
    RoleEnum getUserRole(@PathVariable(name = "user-id") UUID userId) {
        return appRelatedServiceImpl.getUserRole(userId);
    }

    @GetMapping("/applicant-info/{userId}")
    ApplicantDTO getApplicantInfo(@PathVariable UUID userId) {
        return appRelatedServiceImpl.getApplicantInfo(userId);
    }

    @GetMapping("/creator-name-photo/{user-id}")
    TimelineEventCreatorNameAndPhoto getCreatorNameAndPhoto(@PathVariable(name = "user-id") UUID userId){
        return appRelatedServiceImpl.getCreatorNameAndPhoto(userId);
    }

    @GetMapping("/creator-name/{user-id}")
    String getCreatorName(@PathVariable(name = "user-id") UUID userId) {
        return appRelatedServiceImpl.getCreatorName(userId);
    }

    @GetMapping("/jobseeker-all-info/{user-id}")
    JobSeekerAllInfoForAppDTO getJobSeekerAllInfo(@PathVariable(name = "user-id") UUID userId) {
        return appRelatedServiceImpl.getJobSeekerAllInfo(userId);
    }
}
