package com.hpms.jobservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.jobservice.dto.JobPostRequest;
import com.hpms.jobservice.dto.JobSettingDto;
import com.hpms.jobservice.service.JobPostService;
import com.hpms.jobservice.service.imp.JobPostServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

// apis for company and recruiters , api needed for jobSeeker will be in separate class
@RestController
@RequestMapping("/api/jobs/posts")
public class JobPostController {

    @Autowired
    private JobPostServiceImp jobPostService;

    @GetMapping
    public ApiResponse<?> getCompanyJobs(@Valid @RequestHeader("Authorization") String token,
                                         @RequestParam(value = "page",defaultValue = "0") int page,
                                         @RequestParam(value = "size",defaultValue = "5") int size) {
        return this.jobPostService.getCompanyJobs(token, page, size);
    }

    @GetMapping("/published/{companyId}")
    public ApiResponse<?> getPublishedJobs(@Valid @RequestHeader("Authorization") String token,
                                           @PathVariable UUID companyId,
                                           @RequestParam(value = "page",defaultValue = "0")int page,
                                           @RequestParam(value = "size",defaultValue = "5")int size) {
        return jobPostService.getPublishedJobs(token,companyId, page ,size);
    }
    @GetMapping("/init-info/{postId}")
    public ApiResponse<?> getJobInitInfo(@Valid @RequestHeader("Authorization") String token,
                                         @PathVariable("postId") UUID postId) {
        return jobPostService.getJobInitInfo(token, postId);
    }

    @GetMapping("/info-for-user/{postId}")
    public ApiResponse<?> getJobInfo(@Valid @RequestHeader("Authorization") String token,
                                         @PathVariable("postId") UUID postId) {
        return jobPostService.getJobInfoForAnyUser(token, postId);
    }

    @GetMapping("/info-for-app/{postId}")
    public ApiResponse<?> getJobInfoForAppPage(@Valid @RequestHeader("Authorization") String token,
                                     @PathVariable("postId") UUID postId) {
        return jobPostService.getJobInfoForAppPage(token, postId);
    }

    @GetMapping("/advanced-setting/{postId}")
    public ApiResponse<?> getJobAdvancedSetting(@Valid @RequestHeader("Authorization") String token,
                                                @PathVariable("postId") UUID postId) {
        return jobPostService.getJobAdvancedSetting(token, postId);
    }

    @GetMapping("/count-active")
    public ApiResponse<?> getNumberOfActivePosts(@RequestHeader(name = "Authorization") String token) {
        return this.jobPostService.getNumberOfActiveJobPosts(token);
    }

    @PostMapping
    public ApiResponse<?> createInitJobInfo(@Valid @RequestHeader("Authorization") String token,
                                            @Valid @RequestBody JobPostRequest jobPostRequest) {
        return this.jobPostService.createInitJobInfo(token, jobPostRequest);
    }

    @PutMapping("/basics-info")
    public ApiResponse<?> updateInitJobInfo(@Valid @RequestHeader("Authorization") String token,
                                            @Valid @RequestBody JobPostRequest jobPostRequest) {
        return this.jobPostService.updateInitJobInfo(token, jobPostRequest);
    }

    @PutMapping("/advanced-setting")
    public ApiResponse<?> publishOrSaveJob(@Valid @RequestHeader("Authorization") String token,
                                           @Valid @RequestBody JobSettingDto JobSettingDto) {
        return this.jobPostService.publishOrSaveJob(token, JobSettingDto);
    }

//    @PutMapping("/{postId}/save")
//    public ApiResponse<?> saveJobPost(@Valid @RequestHeader("Authorization") String token,
//                                      @PathVariable UUID postId){
//        return  jobPostService.saveJobPost(token, postId);
//    }

    @PutMapping("/{postId}/close")
    public ApiResponse<?> closeJobPost(@RequestHeader(name = "Authorization") String token,
                                        @PathVariable UUID postId){
        return  this.jobPostService.closeJobPost(token,postId);
    }

    @GetMapping("/saved")
    public ApiResponse<?> getSavedJobs(@Valid @RequestHeader("Authorization") String token,
                                       @RequestParam(value = "page",defaultValue = "0")int page,
                                       @RequestParam(value = "size",defaultValue = "5")int size) {
        return jobPostService.getSavedJobs(token,page,size);
    }

//    @PutMapping("/saved/{id}")
//    public ApiResponse<?> toggleSavedJob(@Valid @RequestHeader("Authorization") String token, @PathVariable UUID id) {
//        return jobPostService.toggleSavedJob(token,id);
//    }


    @DeleteMapping("/new/{postId}")
    public ApiResponse<?> deleteNewJobPost(@RequestHeader(name = "Authorization") String token,
                                           @PathVariable UUID postId){
        return  this.jobPostService.deleteNewJobPost(token,postId);
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<?> deleteJobPost(@RequestHeader(name = "Authorization") String token,
                                           @PathVariable UUID postId){
        return  this.jobPostService.deleteJobPost(token,postId);
    }

//    @GetMapping("/apps-posts")
//    public ApiResponse<?> getApplicationsJobPostsForJobSeeker(@Valid @RequestHeader("Authorization") String token) {
//        return jobPostService.getApplicationsJobPostsForJobSeeker(token);
//    }

}
