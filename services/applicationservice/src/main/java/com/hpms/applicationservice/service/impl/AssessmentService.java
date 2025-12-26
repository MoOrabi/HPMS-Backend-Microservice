package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.constants.AssessmentStatus;
import com.hpms.applicationservice.constants.EvaluationType;
import com.hpms.applicationservice.constants.TimelineEventType;
import com.hpms.applicationservice.dto.AssessmentDTO;
import com.hpms.applicationservice.dto.InterviewDTO;
import com.hpms.applicationservice.model.*;
import com.hpms.applicationservice.repository.*;
import com.hpms.applicationservice.service.client.UserServiceClient;
import com.hpms.applicationservice.updater.AssessmentUpdater;
import com.hpms.applicationservice.util.ApplicationUtils;
import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class AssessmentService {

    @Autowired
    private PublicJwtTokenUtils tokenUtils;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private InterviewRepository interviewRepository;

    @Autowired
    private JobApplicationRepository jobApplicationRepository;

    @Autowired
    private ApplicationUtils applicationUtils;

    @Autowired
    private AssessmentUpdater assessmentUpdater;

    @Autowired
    private TimelineEventRepository timelineRepository;

    @Autowired
    private EvaluationRepository evaluationRepository;

    @Autowired
    private UserServiceClient userServiceClient;

    public ApiResponse<?> addAssessment(String token, AssessmentDTO assessmentDTO) {

        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(assessmentDTO.getApplicationId());

        UUID userId = tokenUtils.extractUUID(token.substring(7));
        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                Evaluation evaluation = Evaluation.builder()
                        .title(assessmentDTO.getTitle())
                        .creatorId(userId)
                        .link(assessmentDTO.getLink())
                        .evaluationType(EvaluationType.Assessment)
                        .technicalType(assessmentDTO.getTechnicalType())
                        .application(jobApplication)
                        .build();

                Assessment assessment = new Assessment(evaluation, assessmentDTO.getAvailableTill(), assessmentDTO.getDuration(),
                        AssessmentStatus.Delivered, null);

                Assessment savedAssessment = assessmentRepository.save(assessment);
                assessmentDTO.setId(savedAssessment.getId());
                TimelineEvent timelineEvent = TimelineEvent
                        .builder()
                        .type(TimelineEventType.Assessment)
                        .application(jobApplication)
                        .content(savedAssessment)
                        .creatorId(userId)
                        .build();
                timelineRepository.save((timelineEvent));
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Assessment added successfully")
                        .body(assessmentDTO)
                        .build();
            }
        }
        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.UNAUTHORIZED.value())
                .message("You have no authority to add assessments to this application")
                .build();

    }

    public ApiResponse<?> editAssessment(String token, UUID evaluationId, AssessmentDTO assessmentDTO) {
        UUID userId = tokenUtils.extractUUID(token.substring(7));
        ApiResponse<?> getJobApplicationResponse = applicationUtils.getJobApplication(assessmentDTO.getApplicationId());

        if(!getJobApplicationResponse.isOk()){
            return getJobApplicationResponse;
        }else {
            JobApplication jobApplication = (JobApplication) getJobApplicationResponse.getBody();

            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                Optional<Assessment> optionalAssessment = assessmentRepository.findById(evaluationId);

                if(optionalAssessment.isEmpty()){
                    return ApiResponse.builder()
                            .ok(false)
                            .status(HttpStatus.BAD_REQUEST.value())
                            .message("There is no assessment with such an id")
                            .build();
                }else {
                    Assessment assessment = optionalAssessment.get();
                    assessmentUpdater.apply(assessment, assessmentDTO);
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Assessment updated successfully")
                            .body(assessmentDTO)
                            .build();
                }
            }
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to edit assessments of this application")
                    .build();
        }
    }

    public ApiResponse<?> editAssessmentStatus(String token, UUID evaluationId, AssessmentStatus assessmentStatus) {
        UUID userId = tokenUtils.extractUUID(token.substring(7));
        Optional<Assessment>  optionalAssessment= assessmentRepository.findById(evaluationId);

        if(optionalAssessment.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no interview with such an id")
                    .build();
        }else {
            Assessment assessment = optionalAssessment.get();
            JobApplication jobApplication = assessment.getApplication();
            if (applicationUtils.checkJobSeekerIsConcernedWithApplication(userId, jobApplication) &&
                    Arrays.asList(AssessmentStatus.Visited, AssessmentStatus.Completed).contains(assessmentStatus)
                    ||
                    (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication) &&
                                    AssessmentStatus.Missed.equals(assessmentStatus))){
                assessment.setAssessmentStatus(assessmentStatus);

                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Assessment Status updated successfully")
                        .body(assessmentRepository.save(assessment))
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to edit assessments of this application")
                    .build();

        }


    }

    public ApiResponse<?> editAssessmentResult(String token, UUID evaluationId, String assessmentResult) {


        UUID userId = tokenUtils.extractUUID(token.substring(7));
        Optional<Assessment> optionalAssessment = assessmentRepository.findById(evaluationId);

        if(optionalAssessment.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no assessment with such an id")
                    .build();
        }else {
            Assessment assessment = optionalAssessment.get();
            JobApplication jobApplication = assessment.getApplication();
            if (applicationUtils.checkUserIsConcernedWithApplication(userId, jobApplication)){
                assessment.setResult(assessmentResult);

                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Assessment Result updated successfully")
                        .body(assessmentRepository.save(assessment))
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to edit assessments of this application")
                    .build();

        }

    }

    public ApiResponse<?> deleteAssessment(String token, UUID evaluationId) {
        UUID userId = tokenUtils.extractUUID(token.substring(7));
        Optional<Assessment> optionalAssessment = assessmentRepository.findById(evaluationId);

        if(optionalAssessment.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no assessment with such an id")
                    .build();
        }else {
            Assessment assessment = optionalAssessment.get();
            JobApplication jobApplication = assessment.getApplication();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                assessmentRepository.delete(assessment);
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message("Assessment Deleted successfully")
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to delete assessments of this application")
                    .build();

        }

    }

    public ApiResponse<?> getAssessmentForEmployer(String token, UUID assessmentId) {

        UUID userId = tokenUtils.extractUUID(token.substring(7));
        Optional<Assessment> optionalAssessment = assessmentRepository.findById(assessmentId);

        if(optionalAssessment.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no assessment with such an id")
                    .build();
        }else {
            Assessment assessment = optionalAssessment.get();
            JobApplication jobApplication = assessment.getApplication();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .body(assessmentRepository.findById(assessmentId).get())
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access assessments of this application")
                    .build();



        }
    }

    public ApiResponse<?> getEvaluationsForApplication(String token, UUID appId, EvaluationType evaluationType) {

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
            if (applicationUtils.checkUserIsConcernedWithApplication(userId, jobApplication)){
                List<Evaluation> evaluations = jobApplication.getEvaluations().stream()
                        .filter(evaluation -> evaluation.getEvaluationType().equals(evaluationType))
                        .toList();
                if(evaluationType.equals(EvaluationType.Assessment)){
                    List<AssessmentDTO> assessmentDTOS= new ArrayList<>();
                    for (Evaluation evaluation :
                            evaluations) {
                        Optional<Assessment> optionalAssessment = assessmentRepository.findById(evaluation.getId());


                        if(optionalAssessment.isPresent()){
                            Assessment assessment = optionalAssessment.get();
                            String evaluationCreatorFirstAndLastName = null;
                            try {
                                evaluationCreatorFirstAndLastName = userServiceClient.getCreatorName(assessment.getCreatorId());
                            } catch (ServiceCommunicationException e) {
                                log.error(e.getMessage(), e);
                            }
                            AssessmentDTO assessmentDTO = new AssessmentDTO(assessment);
                            assessmentDTO.setCreatedBy(evaluationCreatorFirstAndLastName);
                            assessmentDTOS.add(assessmentDTO);
                        }
                    }
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .body(assessmentDTOS)
                            .build();
                }else if(evaluationType.equals(EvaluationType.Interview)){
                    List<InterviewDTO> interviewDTOS= new ArrayList<>();
                    for (Evaluation evaluation :
                            evaluations) {
                        Optional<Interview> optionalInterview = interviewRepository.findById(evaluation.getId());

                        if(optionalInterview.isPresent()){
                            Interview interview = optionalInterview.get();
                            InterviewDTO interviewDTO = new InterviewDTO(interview);
                            String evaluationCreatorFirstAndLastName = null;
                            try {
                                evaluationCreatorFirstAndLastName = userServiceClient.getCreatorName(interview.getCreatorId());
                            } catch (ServiceCommunicationException e) {
                                log.error(e.getMessage(), e);
                            }
                            interviewDTO.setCreatedBy(evaluationCreatorFirstAndLastName);
                            interviewDTOS.add(interviewDTO);
                        }
                    }
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .body(interviewDTOS)
                            .build();
                }
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access evaluations of this application")
                    .build();

        }
    }

    public ApiResponse<?> markEvaluationsViewed(String token, UUID appId, EvaluationType evaluationType) {

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
            if (applicationUtils.checkJobSeekerIsConcernedWithApplication(userId, jobApplication)){
                List<Evaluation> evaluations = jobApplication.getEvaluations().stream()
                        .filter(evaluation -> evaluation.getEvaluationType().equals(evaluationType))
                        .toList();
                for (Evaluation evaluation :
                        evaluations) {
                        evaluation.setViewed(true);
                }
                evaluationRepository.saveAll(evaluations);
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .message(evaluationType.name()+"s updated successfully")
                        .build();

            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access evaluations of this application")
                    .build();

        }
    }
}

