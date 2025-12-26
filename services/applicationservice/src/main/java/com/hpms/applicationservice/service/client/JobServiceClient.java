package com.hpms.applicationservice.service.client;

import com.hpms.applicationservice.dto.ApplicationAnswerDTO;
import com.hpms.applicationservice.dto.JobPostCompanyRecruitersAndCreator;
import com.hpms.applicationservice.model.QuestionAnswer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.UUID;

@HttpExchange(url = "http://job-service/api/jobs")
public interface JobServiceClient {

    @GetExchange("/job-post/company-recruiters-creator/{post-id}")
    JobPostCompanyRecruitersAndCreator getJobPostCompanyRecruitersAndCreator(@PathVariable(name = "post-id") UUID postId);

    @GetExchange("/post-questions/questions-details")
    List<ApplicationAnswerDTO> getQuestionDetailsFromAnswers(@RequestBody List<QuestionAnswer> answers);
}
