package com.hpms.userservice.controller.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.jobseeker.JobExperience;
import com.hpms.userservice.service.jobseeker.JobExperienceService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Log
@RestController
@RequestMapping("/api/users/jobseeker-job-experience")
public class JobExperienceController {

    @Autowired
    private JobExperienceService jobExperienceService;

    @PostMapping
    public ApiResponse<?> saveJobExperience(@RequestHeader(name = "Authorization") String token,
                                            @RequestBody JobExperience jobExperience) {
        return jobExperienceService.addJobExperience(token, jobExperience);
    }

    @PutMapping
    public ApiResponse<?> editJobExperience(@RequestHeader(name = "Authorization") String token,
                                            @RequestBody JobExperience jobExperience,
                                            @RequestParam("expId") String id) {
        return jobExperienceService.editJobExperience(token, jobExperience, id);
    }

    @DeleteMapping
    public ApiResponse<?> deleteJobExperience(@RequestHeader(name = "Authorization") String token,
                                              @RequestParam("expId") String id) {
        return jobExperienceService.deleteJobExperience(token, id);
    }

    @GetMapping
    public ApiResponse<?> getEducations(@RequestHeader(name = "Authorization") String token) {
        return jobExperienceService.getJobExperience(token);
    }
}
