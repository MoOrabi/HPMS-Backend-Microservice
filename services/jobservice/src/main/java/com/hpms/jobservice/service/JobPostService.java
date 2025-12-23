package com.hpms.jobservice.service;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.jobservice.dto.JobPostRequest;
import com.hpms.jobservice.dto.JobSettingDto;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface JobPostService {
    ApiResponse<?> createInitJobInfo(String token, JobPostRequest jobPostRequest);
    ApiResponse<?> getJobInfoForAnyUser(String token, UUID postId);
    ApiResponse<?> publishOrSaveJob(String token, JobSettingDto jobSettingDto);
    ApiResponse<?> getCompanyJobs(String token , int page, int size);
    ApiResponse<?> getNumberOfActiveJobPosts(String token);
    ApiResponse<?> getJobInitInfo(String token, UUID postId);
    ApiResponse<?> updateInitJobInfo(String token, JobPostRequest jobPostRequest);
    ApiResponse<?> getJobAdvancedSetting(String token, UUID postId);
    ApiResponse<?> deleteNewJobPost(String token, UUID postId);
    ApiResponse<?> getPublishedJobs(String token, UUID companyId, int page, int size);
//    ApiResponse<?> saveJobPost(String token, UUID postId);
    ApiResponse<?> getSavedJobs(String token,int page ,int size);

    ApiResponse<?> deleteJobPost(String token, UUID postId);

    ApiResponse<?> closeJobPost(String token, UUID postId);

//    ApiResponse<?> getApplicationsJobPostsForJobSeeker(String token);


//    ApiResponse<?> toggleSavedJob(String token, UUID id);

    ApiResponse<?> getJobInfoForAppPage(String token, UUID postId);
}
