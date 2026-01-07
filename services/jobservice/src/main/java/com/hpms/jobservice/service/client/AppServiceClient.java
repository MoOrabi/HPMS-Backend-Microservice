package com.hpms.jobservice.service.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import com.hpms.jobservice.dto.JobApplicationNumberPerStatus;
import com.hpms.jobservice.dto.JobPostPublicNumbers;
import com.hpms.jobservice.dto.JobPostForApplication;

import java.util.List;
import java.util.UUID;

@HttpExchange(url = "http://applications-service/api/apps/jobs")
public interface AppServiceClient {

    @GetExchange("/numbers-per-status/{post-id}")
    JobApplicationNumberPerStatus getPostNumbersPerStatus(@PathVariable(name = "post-id") UUID postId);

    @GetExchange("/public-numbers/{post-id}")
    JobPostPublicNumbers getPostPublicNumbers(@PathVariable(name = "post-id") UUID postId);

    @GetExchange("/posts-for-apps/{user-id}")
    List<JobPostForApplication> getJobPostsForApplications(@PathVariable(name = "user-id") UUID userId);

    @GetExchange("/num-of-apps/{post-id}")
    Integer getNumberOfApplications(@PathVariable(name = "post-id")UUID postId);
}
