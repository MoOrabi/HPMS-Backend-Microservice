package com.hpms.userservice.service;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.RecruiterLoginRequest;
import com.hpms.userservice.dto.RecruiterSignUpRequest;
import org.springframework.web.multipart.MultipartFile;

public interface RecruiterAuthService {

    public ApiResponse<?> registerRecruiter(RecruiterSignUpRequest request, MultipartFile profilePhoto) throws Exception;

    public ApiResponse<?> loginRecruiter(RecruiterLoginRequest loginRequest);

}
