package com.hpms.applicationservice.service;

import com.hpms.applicationservice.dto.JobApplicationNumberPerStatus;
import com.hpms.applicationservice.dto.QuestionAnswerDto;
import com.hpms.commonlib.dto.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface JobApplicationService {

    ApiResponse<?> getJobApplicationAnswers(String token, UUID appId);

    ApiResponse<?> getJobApplications(UUID jobId);

    ApiResponse<?> getJobApplicationProfileInfo(String token, UUID appId);

    ApiResponse<?> submitJobApplication(String jobSeekerToken, UUID jobId, List<QuestionAnswerDto> answerDtoList);

    ApiResponse<?> deleteJobApplication(String jobSeekerToken, UUID jobApplicationId);

    ApiResponse<?> getApplicationNumbersByStatus(String token, UUID jobId);

    ApiResponse<?> isJobSeekerAppliedToJobPost(String token, UUID jobId);


    JobApplicationNumberPerStatus getPostNumbersPerStatus(UUID postId);
}
