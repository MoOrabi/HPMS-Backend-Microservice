package com.hpms.applicationservice.controller;

import com.hpms.applicationservice.dto.QuestionAnswerDto;
import com.hpms.applicationservice.service.impl.JobApplicationServiceImp;
import com.hpms.commonlib.dto.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/apps")
public class JobApplicationController {

    @Autowired
    private JobApplicationServiceImp jobApplicationService;

    @GetMapping("/is-applied")
    public ApiResponse<?> isJobSeekerAppliedToJobPost(@Valid @RequestHeader("Authorization") String token,
                                                      @RequestParam UUID jobId){
        return  jobApplicationService.isJobSeekerAppliedToJobPost(token, jobId);
    }

    @GetMapping("/answers/{appId}")
    public ApiResponse<?> getJobApplicationAnswers(@RequestHeader("Authorization") String token ,
            @PathVariable UUID appId){
        return  jobApplicationService.getJobApplicationAnswers(token, appId);
    }

    @GetMapping("/info/{jobId}")
    public ApiResponse<?> getJobApplications(@PathVariable UUID jobId) {
        return jobApplicationService.getJobApplications(jobId);
    }

    @GetMapping("/app-info/{jobApplicationId}")
    public ApiResponse<?> getJobApplicantProfile(@Valid @RequestHeader("Authorization") String jobSeekerToken,
                                               @PathVariable UUID jobApplicationId){
        return jobApplicationService.getJobApplicationProfileInfo(jobSeekerToken,jobApplicationId);
    }

    @PostMapping("/{jobId}")
    public ApiResponse<?> submitJobApplication(@Valid @RequestHeader("Authorization") String jobSeekerToken,
                                               @Valid @PathVariable UUID jobId,
                                               @Valid @RequestBody List<QuestionAnswerDto> answerDtoList) {
        return jobApplicationService.submitJobApplication(jobSeekerToken, jobId, answerDtoList);
    }

    // Job jobSeeker delete it
    @DeleteMapping("/{jobApplicationId}")
    public ApiResponse<?> deleteJobApplication(@Valid @RequestHeader("Authorization") String jobSeekerToken,
                                               @PathVariable UUID jobApplicationId){
        return jobApplicationService.deleteJobApplication(jobSeekerToken,jobApplicationId);
    }

    @GetMapping("/numbers/{appId}")
    public ApiResponse<?> getApplicationNumbersByStatus(@Valid @RequestHeader("Authorization") String token,
                                                        @PathVariable UUID appId) {
        return jobApplicationService.getApplicationNumbersByStatus(token, appId);
    }

}
