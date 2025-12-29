package com.hpms.jobservice.controller;

import com.hpms.jobservice.dto.QuestionAnswerDto;
import com.hpms.jobservice.dto.app.ApplicationAnswerDTO;
import com.hpms.jobservice.dto.app.JobPostCompanyRecruitersAndCreator;
import com.hpms.jobservice.service.imp.AppRelatedServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs/posts")
@RequiredArgsConstructor
public class AppRelatedController {
    private final AppRelatedServiceImpl appRelatedService;

    @GetMapping("/company-recruiters-creator/{post-id}")
    JobPostCompanyRecruitersAndCreator getJobPostCompanyRecruitersAndCreator(@PathVariable(name = "post-id") UUID postId) {
        return appRelatedService.getJobPostCompanyRecruitersAndCreator(postId);
    }

    @GetMapping("/questions/questions-details")
    List<ApplicationAnswerDTO> getQuestionDetailsFromAnswers(@RequestBody List<QuestionAnswerDto> answers) {
        return appRelatedService.getQuestionDetailsFromAnswers(answers);
    }

}
