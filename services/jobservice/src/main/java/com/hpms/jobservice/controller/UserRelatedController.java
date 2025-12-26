package com.hpms.jobservice.controller;

import com.hpms.jobservice.dto.JobPostDto;
import com.hpms.jobservice.repository.JobPostRepository;
import com.hpms.jobservice.service.imp.UserRelatedServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs/users")
@RequiredArgsConstructor
public class UserRelatedController {
    private final UserRelatedServiceImpl userRelatedService;

    @GetMapping("/recruiter-job-count/{recruiterId}")
    public int countRecruiterActiveJobs(@PathVariable UUID recruiterId) {
        return userRelatedService.countRecruiterJobs(recruiterId);
    }

    @GetMapping("/company-job-count/{companyId}")
    int countCompanyActiveJobs(@PathVariable UUID companyId) {
        return userRelatedService.countCompanyActiveJobs(companyId);
    }

    @GetMapping("/company-recent-jobs/{companyId}")
    List<JobPostDto> getCompnayRecentJobPosts(@PathVariable UUID companyId) {
        return userRelatedService.getCompnayRecentJobPosts(companyId);
    }

    @GetMapping("/is-js-save-post")
    Boolean isJobPostSaved(UUID jobSeekerCallerId, UUID jobPostId) {
        return userRelatedService.isJobPostSaved(jobSeekerCallerId, jobPostId);
    }

}
