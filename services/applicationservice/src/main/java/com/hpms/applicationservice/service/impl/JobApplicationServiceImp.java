package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.constants.ApplicationStatus;
import com.hpms.applicationservice.dto.*;
import com.hpms.applicationservice.model.JobApplication;
import com.hpms.applicationservice.model.QuestionAnswer;
import com.hpms.applicationservice.repository.JobApplicationRepository;
import com.hpms.applicationservice.service.JobApplicationService;
import com.hpms.applicationservice.service.client.JobServiceClient;
import com.hpms.applicationservice.service.client.UserServiceClient;
import com.hpms.applicationservice.util.ApplicationUtils;
import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class JobApplicationServiceImp implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;

    private final ApplicationUtils applicationUtils;

    private final UserServiceClient userServiceClient;

    private final JobServiceClient jobServiceClient;

    @Override
    public ApiResponse<?> getJobApplicationAnswers(String token, UUID appId) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobApplication> optionalJobApplication = jobApplicationRepository.findById(appId);
        if(optionalJobApplication.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no application with such an id")
                    .build();
        }else {
            JobApplication jobApplication = optionalJobApplication.get();
            if(applicationUtils.checkUserIsConcernedWithApplication(userId, jobApplication)){

                List<QuestionAnswer> questionAnswers = jobApplication.getQuestionAnswer();
                List<QuestionAnswerDto> questionAnswerDtos = questionAnswers.stream().map(answer ->
                        QuestionAnswerDto.builder()
                                .questionAnswer(answer.getAnswer())
                                .questionId(answer.getQuestionId())
                                .build()
                ).toList();
                List<ApplicationAnswerDTO> applicationAnswerDTOS = new ArrayList<>();
                try {
                    applicationAnswerDTOS = jobServiceClient.getQuestionDetailsFromAnswers(questionAnswerDtos);
                } catch (ServiceCommunicationException e) {
                    log.error(e.getMessage(), e);
                }
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.OK.value())
                        .message("Answers for application retrieved successfully")
                        .body(applicationAnswerDTOS)
                        .build();
            }
            else {
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .message("You have no access to this")
                        .build();
            }

        }


    }

    @Override
    public ApiResponse<?> getJobApplications(UUID jobId) {

        List<JobApplication> jobApplications = jobApplicationRepository.findByJobPostId(jobId);

        List<JobApplicationInfoDTO> applicationInfoDTOS = new ArrayList<>();

        for (JobApplication jobApplication:
             jobApplications) {
            UUID applicantId = jobApplication.getJobSeekerId();
            ApplicantDTO applicantDTO = ApplicantDTO.builder().build();
            try {
                applicantDTO = userServiceClient.getApplicantInfo(applicantId);
            } catch (ServiceCommunicationException ex) {
                log.error(ex.getMessage(), ex);
            }
            JobApplicationInfoDTO applicationInfoDTO = new JobApplicationInfoDTO(jobApplication.getId(),
                    applicantId, applicantDTO.getFirstName(), applicantDTO.getLastName(), applicantDTO.getProfilePhoto() , applicantDTO.getJobTitle(),
                    applicantDTO.getCareerLevel(), applicantDTO.getEducations(), applicantDTO.getLivesIn(),jobApplication.getStatus().toString(),
                    jobApplication.isViewed());
            applicationInfoDTOS.add(applicationInfoDTO);
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(applicationInfoDTOS)
                .message("Success")
                .build();
    }

    @Override
    public ApiResponse<?> getJobApplicationProfileInfo(String token, UUID appId) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobApplication> optionalApplication = jobApplicationRepository.findById(appId);

        if(optionalApplication.isEmpty()){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no application with such an id")
                    .build();
        }else {
            JobApplication jobApplication = optionalApplication.get();
            List<QuestionAnswer> answers = jobApplication.getQuestionAnswer();
            UUID applicantId = jobApplication.getJobSeekerId();
            JobSeekerAllInfoDTO applicantDTO = JobSeekerAllInfoDTO.builder().build();
            try {
                applicantDTO = userServiceClient.getJobSeekerAllInfo(applicantId);
            } catch (ServiceCommunicationException e) {
                log.error(e.getMessage(), e);
            }

            ApplicantProfileResponse profileResponse = ApplicantProfileResponse.builder()
                    .applicationId(jobApplication.getId())
                    .jobSeekerInfo(applicantDTO)
                    .questionAnswers(answers)
                    .status(jobApplication.getStatus().getValue())
                    .build();
            if (applicationUtils.checkEmployerIsConcernedWithApplication(userId, jobApplication)){
                return ApiResponse.builder()
                        .ok(true)
                        .status(HttpStatus.ACCEPTED.value())
                        .body(profileResponse)
                        .build();
            }

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to access comments of this application")
                    .build();

        }


    }

    @Override
    public ApiResponse<?> submitJobApplication(String jobSeekerToken, UUID jobId, List<QuestionAnswerDto> answerDtoList) {
        UUID jobSeekerId = PublicJwtTokenUtils.extractUUID(jobSeekerToken.substring(7));
        boolean isSubmittedBefore = jobApplicationRepository.existsByJobPostIdAndJobSeekerId(jobId,jobSeekerId) ;
        if(isSubmittedBefore){
            return ApiResponse.builder()
                    .ok(false)
                    .message("User Submit Application Before")
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .build();
        }


        List<QuestionAnswer> questionAnswersList =  extractQuestionAnswer(answerDtoList);

        JobApplication jobApplication = JobApplication.builder()
                .jobSeekerId(jobSeekerId)
                .questionAnswer(questionAnswersList)
                .jobPostId(jobId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .status(ApplicationStatus.APPLIED)
                .build();
        jobApplicationRepository.save(jobApplication);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Application Submitted Successfully")
                .build();
    }

    @Override
    public ApiResponse<?> deleteJobApplication(String jobSeekerToken, UUID jobApplicationId) {
        UUID jobSeekerId = PublicJwtTokenUtils.extractUUID(jobSeekerToken.substring(7));
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository.getByIdAndJobSeekerId(jobApplicationId,jobSeekerId);
        if(jobApplicationOptional.isEmpty()){
            return ApiResponse.getDefaultErrorResponse();
        }
        jobApplicationRepository.delete(jobApplicationOptional.get());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Application Deleted Successfully")
                .build();
    }

    private List<QuestionAnswer> extractQuestionAnswer(List<QuestionAnswerDto> answerDtoList) {
        List<QuestionAnswer> questionAnswerList = new ArrayList<>();
        answerDtoList.forEach(answerDto -> {
          QuestionAnswer questionAnswer = QuestionAnswer.builder()
                  .answer(answerDto.getQuestionAnswer())
                  .questionId(answerDto.getQuestionId())
                  .build();
          questionAnswerList.add(questionAnswer);
        });
        return questionAnswerList;
    }

    @Override
    public ApiResponse<?> getApplicationNumbersByStatus(String token, UUID postId) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        JobApplicationNumberPerStatus applicationNumberPerStatus = getPostNumbersPerStatus(postId);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(applicationNumberPerStatus)
                .build();
    }

    @Override
    public ApiResponse<?> isJobSeekerAppliedToJobPost(String token, UUID postId) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        boolean isApplied = jobApplicationRepository.existsByJobPostIdAndJobSeekerId(postId,userId);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(isApplied)
                .build();
    }

    @Override
    public JobApplicationNumberPerStatus getPostNumbersPerStatus(UUID postId) {
        int appliedNumber = jobApplicationRepository
                .countAllApplications(postId);
        int disqualifiedNumber = jobApplicationRepository
                .countApplicationsByStatus(postId, ApplicationStatus.DISQUALIFIED);
        int phoneScreenNumber = jobApplicationRepository
                .countApplicationsByStatus(postId, ApplicationStatus.PHONE_SCREEN);
        int assessmentNumber = jobApplicationRepository
                .countApplicationsByStatus(postId, ApplicationStatus.ASSESSMENT);
        int interviewNumber = jobApplicationRepository
                .countApplicationsByStatus(postId, ApplicationStatus.INTERVIEW);
        int offerNumber = jobApplicationRepository
                .countApplicationsByStatus(postId, ApplicationStatus.OFFER);
        int hiredNumber = jobApplicationRepository
                .countApplicationsByStatus(postId, ApplicationStatus.HIRED);

        return JobApplicationNumberPerStatus
                .builder()
                .applied(appliedNumber)
                .disqualified(disqualifiedNumber)
                .phoneScreen(phoneScreenNumber)
                .assessment(assessmentNumber)
                .interview(interviewNumber)
                .offer(offerNumber)
                .hired(hiredNumber).build();
    }

}
