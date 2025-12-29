package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.constants.EvaluationType;
import com.hpms.applicationservice.constants.InterviewStatus;
import com.hpms.applicationservice.constants.TimelineEventType;
import com.hpms.applicationservice.dto.InterviewDTO;
import com.hpms.applicationservice.model.Evaluation;
import com.hpms.applicationservice.model.Interview;
import com.hpms.applicationservice.model.JobApplication;
import com.hpms.applicationservice.model.TimelineEvent;
import com.hpms.applicationservice.repository.InterviewRepository;
import com.hpms.applicationservice.repository.TimelineEventRepository;
import com.hpms.applicationservice.updater.AssessmentUpdater;
import com.hpms.applicationservice.updater.InterviewUpdater;
import com.hpms.applicationservice.util.ApplicationUtils;
import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class InterviewService {

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private ApplicationUtils applicationUtils;

    @Autowired
    private InterviewUpdater interviewUpdater;

    @Autowired
    private AssessmentUpdater assessmentUpdater;

    @Autowired
    private TimelineEventRepository timelineRepository;

    public ApiResponse<?> addInterview(String token, InterviewDTO interviewDTO) {

        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(interviewDTO.getApplicationId());

        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();
            UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
            if(applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                Evaluation evaluation = Evaluation.builder()
                        .title(interviewDTO.getTitle())
                        .creatorId(userId)
                        .link(interviewDTO.getLink())
                        .evaluationType(EvaluationType.Interview)
                        .technicalType(interviewDTO.getTechnicalType())
                        .application(jobApplication)
                        .build();
                Interview interview = new Interview(evaluation, interviewDTO.getStartTime(),
                        interviewDTO.getEndTime(), InterviewStatus.Delivered);

                Interview savedInterview = interviewRepository.save(interview);
                interviewDTO.setId(savedInterview.getId());
                TimelineEvent timelineEvent = TimelineEvent
                        .builder()
                        .type(TimelineEventType.Interview)
                        .application(jobApplication)
                        .content(interview)
                        .creatorId(userId)
                        .build();
                timelineRepository.save((timelineEvent));
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Interview added successfully")
                        .body(interviewDTO)
                        .build();

                }
            }
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to add interviews to this application")
                    .build();

    }

    public ApiResponse<?> editInterview(String token, UUID evaluationId, InterviewDTO interviewDTO) {
        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(interviewDTO.getApplicationId());

        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();
            UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                Optional<Interview> optionalInterview = interviewRepository.findById(evaluationId);

                if(optionalInterview.isEmpty()){
                    return ApiResponse.builder()
                            .ok(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("There is no interview with such an id")
                            .build();
                }else {
                    Interview interview = optionalInterview.get();
                    interviewUpdater.apply(interview, interviewDTO);
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Assessment updated successfully")
                            .body(interviewDTO)
                            .build();
                }
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to edit interviews of this application")
                    .build();
        }
    }

    public ApiResponse<?> editInterviewStatus(String token, UUID evaluationId, InterviewStatus interviewStatus) {

        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        Optional<Interview> optionalInterview = interviewRepository.findById(evaluationId);

        if(optionalInterview.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no interview with such an id")
                    .build();
        }else {
            Interview interview = optionalInterview.get();
            JobApplication jobApplication = interview.getApplication();
            if (applicationUtils.checkJobSeekerIsConcernedWithApplication(userId, jobApplication) &&
                    InterviewStatus.Visited.equals(interviewStatus)
                    ||
                    (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication) &&
                                    Arrays.asList(InterviewStatus.Attended, InterviewStatus.Missed)
                                            .contains(interviewStatus))){
                interview.setInterviewStatus(interviewStatus);

                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Interview Status updated successfully")
                        .body(interviewRepository.save(interview))
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to edit interviews of this application")
                    .build();

        }




    }

    public ApiResponse<?> deleteInterview(String token, UUID evaluationId) {

        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        Optional<Interview> optionalInterview = interviewRepository.findById(evaluationId);

        if(optionalInterview.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no interview with such an id")
                    .build();
        }else {
            Interview interview = optionalInterview.get();
            JobApplication jobApplication = interview.getApplication();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                interviewRepository.delete(interview);
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Interview Deleted successfully")
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to delete interviews of this application")
                    .build();

        }
    }

    public ApiResponse<?> getInterviewForEmployer(String token, UUID interviewId) {

        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        Optional<Interview> optionalInterview = interviewRepository.findById(interviewId);

        if(optionalInterview.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no interview with such an id")
                    .build();
        }else {
            Interview interview = optionalInterview.get();
            JobApplication jobApplication = interview.getApplication();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .body(interviewRepository.findById(interviewId))
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access interviews of this application")
                    .build();

        }
    }
}

