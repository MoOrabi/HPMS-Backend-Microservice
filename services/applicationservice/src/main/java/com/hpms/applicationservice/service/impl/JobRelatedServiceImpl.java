package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.constants.ApplicationStatus;
import com.hpms.applicationservice.dto.JobApplicationNumberPerStatus;
import com.hpms.applicationservice.dto.job.JobPostForApplication;
import com.hpms.applicationservice.dto.job.JobPostPublicNumbers;
import com.hpms.applicationservice.model.JobApplication;
import com.hpms.applicationservice.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobRelatedServiceImpl {
    private final JobApplicationRepository jobApplicationRepository;
    private final JobApplicationServiceImp jobApplicationServiceImp;

    public JobApplicationNumberPerStatus getPostNumbersPerStatus(UUID postId) {
        return jobApplicationServiceImp.getPostNumbersPerStatus(postId);
    }

    public JobPostPublicNumbers getPostPublicNumbers(UUID postId) {
        List<JobApplication> applications = jobApplicationRepository.findByJobPostId(postId);

        long applied = applications.size();
        long viewed = applications.stream().filter(JobApplication::isViewed).count();
        long inConsideration = applications.stream().filter(jobApplication ->
                !(new ArrayList<>(Arrays.asList(null, ApplicationStatus.APPLIED, ApplicationStatus.DISQUALIFIED))
                        .contains(jobApplication.getStatus()))
        ).count();
        long rejected = applications.stream().filter(jobApplication ->
                        jobApplication.getStatus().equals(ApplicationStatus.DISQUALIFIED))
                .count();

        return JobPostPublicNumbers
                .builder()
                .applied(applied)
                .viewed(viewed)
                .inConsideration(inConsideration)
                .rejected(rejected)
                .build();
    }

    public List<JobPostForApplication> getJobPostsForApplications(UUID userId) {
        List<JobApplication> applications = jobApplicationRepository.findByJobSeekerId(userId);
        List<JobPostForApplication> jobPostsForApplications = new ArrayList<>();
        for (JobApplication jobApplication : applications) {
            jobPostsForApplications.add(JobPostForApplication.builder()
                            .applicationId(jobApplication.getId())
                            .applicationStatus(jobApplication.getStatus().name())
                            .publicNumbers(getPostPublicNumbers(jobApplication.getJobPostId()))
                            .appUpdatedAt(jobApplication.getUpdatedAt())
                            .newActionsNumber(getApplicationNewActionsNumber(jobApplication))
                    .build());
        }
        return jobPostsForApplications;
    }

    public Integer getNumberOfApplications(UUID postId) {
        return jobApplicationRepository.countAllApplications(postId);
    }

    private int getApplicationNewActionsNumber(JobApplication application) {
        int evaluationsNumber = 0;
        if(application.getEvaluations() != null) {
            evaluationsNumber = application.getEvaluations().stream().filter((app) -> !app.isViewed()).toList().size();
        }
        int offerNumbers = 0;
        int offerCommentsNumber = 0;
        if(application.getOffer() != null){
            offerNumbers = (application.getOffer().isViewed())? 0: 1;
            offerCommentsNumber = application.getOffer().getComments().stream().filter(
                    (comment) -> !comment.isViewed()).toList().size();
        }

        return evaluationsNumber + offerNumbers + offerCommentsNumber;
    }
}
