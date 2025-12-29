package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.constants.TimelineEventType;
import com.hpms.applicationservice.dto.ApplicationCommentDTO;
import com.hpms.applicationservice.dto.ApplicationCommentResponse;
import com.hpms.applicationservice.mapper.ApplicationCommentMapper;
import com.hpms.applicationservice.model.ApplicationComment;
import com.hpms.applicationservice.model.JobApplication;
import com.hpms.applicationservice.model.TimelineEvent;
import com.hpms.applicationservice.repository.ApplicationCommentRepository;
import com.hpms.applicationservice.repository.JobApplicationRepository;
import com.hpms.applicationservice.repository.TimelineEventRepository;
import com.hpms.applicationservice.util.ApplicationUtils;
import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApplicationCommentService {

    private final ApplicationCommentRepository applicationCommentRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final ApplicationUtils applicationUtils;

    private final ApplicationCommentMapper applicationCommentMapper;

    private final TimelineEventRepository timelineRepository;

    public ApiResponse<?> addApplicationComment(String token, ApplicationCommentDTO commentDTO) {

        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(commentDTO.getApplicationId());
        System.out.println(commentDTO.getApplicationId());
        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();
            UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));

            System.out.println(jobApplication.getId());
            System.out.println(applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication));
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                ApplicationComment applicationComment = ApplicationComment
                        .builder()
                        .type(commentDTO.getType())
                        .content(commentDTO.getContent())
                        .creatorId(userId)
                        .application(jobApplication)
                        .build();
                System.out.println("Hi 1");
                ApplicationComment savedComment = applicationCommentRepository.save(applicationComment);
                commentDTO.setId(savedComment.getId());
                TimelineEvent timelineEvent = TimelineEvent
                        .builder()
                        .type(TimelineEventType.Comment)
                        .application(jobApplication)
                        .content(savedComment)
                        .creatorId(userId)
                        .build();
                timelineRepository.save(timelineEvent);
                System.out.println("Hi 2");
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Application Comment added successfully")
                        .body(commentDTO)
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to add comments to this application")
                    .build();
        }
    }

    public ApiResponse<?> getCommentsOfApplication(String token, UUID appId) {

        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        Optional<JobApplication> optionalApplication = jobApplicationRepository.findById(appId);

        if(optionalApplication.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no application with such an id")
                    .build();
        }else {
            JobApplication jobApplication = optionalApplication.get();
            List<ApplicationComment> comments = jobApplication.getComments();
            List<ApplicationCommentResponse> commentResponses = new ArrayList<>();
            for (ApplicationComment comment :
                    comments) {
                commentResponses.add(applicationCommentMapper.ApplicationCommentToResponse(comment));
            }
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .body(commentResponses)
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access comments of this application")
                    .build();

        }


    }
}
