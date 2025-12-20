package com.hpms.userservice.service;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.JobSeekerAuthReq;
import com.hpms.userservice.dto.JobSeekerRegisterRequest;

public interface JobSeekerAuthService {

    ApiResponse<?> registerJobSeeker(JobSeekerRegisterRequest jobSeekerRegisterRequest);

    ApiResponse<?> login(JobSeekerAuthReq jobSeekerAuthReq);

}
