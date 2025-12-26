package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.constants.ApplicationStatus;
import com.hpms.applicationservice.constants.TimelineEventType;
import com.hpms.applicationservice.dto.TimelineEventCreatorNameAndPhoto;
import com.hpms.applicationservice.dto.TimelineEventResponse;
import com.hpms.applicationservice.model.JobApplication;
import com.hpms.applicationservice.model.TimelineEvent;
import com.hpms.applicationservice.repository.JobApplicationRepository;
import com.hpms.applicationservice.repository.TimelineEventRepository;
import com.hpms.applicationservice.service.client.UserServiceClient;
import com.hpms.applicationservice.util.ApplicationUtils;
import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class TimelineEventService {

    private final ApplicationUtils applicationUtils;

    private final JobApplicationRepository jobApplicationRepository;

    private final TimelineEventRepository timelineRepository;

    private final PublicJwtTokenUtils tokenUtils;

    private final UserServiceClient userServiceClient;

    public ApiResponse<?> moveApplication(String token, UUID appId, String applicationStatus) {
        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(appId);

        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();
            UUID userId = tokenUtils.extractUUID(token.substring(7));

            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                jobApplication.setStatus(Enum.valueOf(ApplicationStatus.class, applicationStatus));
                jobApplicationRepository.save(jobApplication);
                TimelineEventType type = TimelineEventType.Move;
                if(jobApplication.getStatus().equals(ApplicationStatus.HIRED)){
                    type = TimelineEventType.MoveToHired;
                } else if (jobApplication.getStatus().equals(ApplicationStatus.DISQUALIFIED)) {
                    type = TimelineEventType.MoveToDisqualified;
                }
                TimelineEvent timelineEvent = TimelineEvent
                        .builder()
                        .type(type)
                        .application(jobApplication)
                        .creatorId(userId)
                        .build();
                timelineRepository.save((timelineEvent));
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.OK.value())
                        .message("Status Updated to "+applicationStatus+" Successfully")
                        .build();
            }
        }
        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("You have no authority to edit status of this application")
                .build();
    }

    public ApiResponse<?> getTimelineEventsForApplication(String token, UUID appId) {

        UUID userId = tokenUtils.extractUUID(token.substring(7));
        Optional<JobApplication> optionalApplication = jobApplicationRepository.findById(appId);

        if(optionalApplication.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no application with such an id")
                    .build();
        }else {
            JobApplication jobApplication = optionalApplication.get();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                List<TimelineEvent> timelineEvents = timelineRepository.findByApplicationIdOrderByCreatedAtDesc(appId);
                List<TimelineEventResponse> timelineEventResponses = new ArrayList<>();
                for(TimelineEvent event: timelineEvents){
                    TimelineEventCreatorNameAndPhoto timelineEventCreatorNameAndPhoto = TimelineEventCreatorNameAndPhoto
                            .builder().build();
                    try {
                        timelineEventCreatorNameAndPhoto = userServiceClient.getCreatorNameAndPhoto(event.getCreatorId());
                    } catch (ServiceCommunicationException e) {
                        log.error(e.getMessage(), e);
                    }
                    String name = timelineEventCreatorNameAndPhoto.getName();
                    String photo = timelineEventCreatorNameAndPhoto.getPhotoUrl();

                    TimelineEventResponse eventResponse = TimelineEventResponse.builder()
                                    .id(event.getId())
                                            .createdAt(event.getCreatedAt())
                                                    .updatedAt(event.getUpdatedAt())
                                                            .content(event.getContent())
                                                                    .creatorName(name)
                            .creatorPhoto(photo)
                            .creatorId(event.getCreatorId())
                            .applicationStatus(jobApplication.getStatus())
                                                                            .type(event.getType())
                                                                                    .build();
                    timelineEventResponses.add(eventResponse);
                }
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .body(timelineEventResponses)
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access timeline of this application")
                    .build();

        }

    }

//    private String getCreatorName(TimelineEvent event) {
//        String name = "";
//        User user = event.getCreator();
//        if(user.getRole().equals(RoleEnum.ROLE_COMPANY)){
//            Company creator = companyRepository.findById(user.getId()).get();
//            name = creator.getManagerFirstName()+ " " + creator.getManagerLastName();
//        }else if(user.getRole().equals(RoleEnum.ROLE_RECRUITER)){
//            Recruiter creator = recruiterRepository.findById(user.getId()).get();
//            name = creator.getFirstName() + " " + creator.getLastName();
//        }
//        return name;
//    }
//
//    private String getCreatorPhoto(TimelineEvent event) {
//        String photo = "";
//        User user = event.getCreator();
//        if(user.getRole().equals(RoleEnum.ROLE_COMPANY)){
//            Company creator = companyRepository.findById(user.getId()).get();
//            photo = creator.getLogo();
//        }else if(user.getRole().equals(RoleEnum.ROLE_RECRUITER)){
//            Recruiter creator = recruiterRepository.findById(user.getId()).get();
//            photo = creator.getProfilePhoto();
//        }
//        return photo;
//    }
}
