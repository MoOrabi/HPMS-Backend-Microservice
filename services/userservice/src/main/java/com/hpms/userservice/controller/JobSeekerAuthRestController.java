package com.hpms.userservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.JobSeekerAuthReq;
import com.hpms.userservice.dto.JobSeekerRegisterRequest;
import com.hpms.userservice.service.impl.JobSeekerAuthServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/auth/jobseeker")
public class JobSeekerAuthRestController {
    private final JobSeekerAuthServiceImp jobSeekerAuthServiceImp;

    @Autowired
    public JobSeekerAuthRestController(JobSeekerAuthServiceImp jobSeekerAuthServiceImp) {
        this.jobSeekerAuthServiceImp = jobSeekerAuthServiceImp;
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody JobSeekerRegisterRequest jobSeekerRegisterRequest) {
        return jobSeekerAuthServiceImp.registerJobSeeker(jobSeekerRegisterRequest);
    }

    @PostMapping("/login")
    public ApiResponse<?> login(@Valid @RequestBody JobSeekerAuthReq jobSeekerAuthReq) {
        return jobSeekerAuthServiceImp.login(jobSeekerAuthReq);
    }

}
