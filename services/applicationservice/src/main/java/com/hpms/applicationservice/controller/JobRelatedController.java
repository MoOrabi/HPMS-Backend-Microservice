package com.hpms.applicationservice.controller;

import com.hpms.applicationservice.dto.JobApplicationNumberPerStatus;
import com.hpms.applicationservice.dto.job.JobPostForApplication;
import com.hpms.applicationservice.dto.job.JobPostPublicNumbers;
import com.hpms.applicationservice.service.impl.JobRelatedServiceImpl;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/apps/jobs")
@RequiredArgsConstructor
public class JobRelatedController {
    private final JobRelatedServiceImpl jobRelatedService;

    @GetMapping("/numbers-per-status/{post-id}")
    JobApplicationNumberPerStatus getPostNumbersPerStatus(@PathVariable(name = "post-id") UUID postId) {
        return jobRelatedService.getPostNumbersPerStatus(postId);
    }

    @GetMapping("/public-numbers/{post-id}")
    JobPostPublicNumbers getPostPublicNumbers(@PathVariable(name = "post-id") UUID postId) {
        return jobRelatedService.getPostPublicNumbers(postId);
    }

    @GetMapping("/posts-for-apps/{user-id}")
    List<JobPostForApplication> getJobPostsForApplications(@PathVariable(name = "user-id") UUID userId) {
        return jobRelatedService.getJobPostsForApplications(userId);
    }

    @GetMapping("/num-of-apps/{post-id}")
    Integer getNumberOfApplications(@PathVariable(name = "post-id")UUID postId) {
        return jobRelatedService.getNumberOfApplications(postId);
    }
}
