package com.hpms.userservice.service.impl;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.config.AuthenticationProviderService;
import com.hpms.userservice.constants.AuthProviders;
import com.hpms.commonlib.constants.RoleEnum;
import com.hpms.userservice.dto.JobSeekerAuthReq;
import com.hpms.userservice.dto.JobSeekerRegisterRequest;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.User;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.RoleRepository;
import com.hpms.userservice.service.JobSeekerAuthService;
import com.hpms.userservice.utils.JwtTokenUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.hpms.userservice.dto.SuccessLoginRes.SuccessRegisterResponse;
import static com.hpms.userservice.dto.SuccessLoginRes.getSuccessfulResponse;
import static com.hpms.userservice.utils.AppConstants.LOCAL_PROVIDER_ID;

@Service
@Log
public class JobSeekerAuthServiceImp implements JobSeekerAuthService {

    private JobSeekerProfileRepository jobSeekerProfileRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private AuthenticationProviderService authenticationProviderService;

    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    @Lazy
    public JobSeekerAuthServiceImp(JobSeekerProfileRepository jobSeekerProfileRepository,
                                   RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                                   AuthenticationProviderService authenticationProviderService,
                                   JwtTokenUtils jwtTokenUtils) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationProviderService = authenticationProviderService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    @Override
    public ApiResponse<?> registerJobSeeker(JobSeekerRegisterRequest jobSeekerRegisterRequest) {
        Optional<JobSeeker> customer = jobSeekerProfileRepository.getByUsername(jobSeekerRegisterRequest.getUsername());
        if (customer.isPresent()) {
            return ApiResponse.builder()
                    .ok(false)
                    .message(jobSeekerRegisterRequest.getUsername() + " is Exist")
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .build();
        }
        JobSeeker savedJobSeeker = createJobSeeker(jobSeekerRegisterRequest);
        savedJobSeeker = jobSeekerProfileRepository.save(savedJobSeeker);
        return ApiResponse
                .builder()
                .ok(true)
                .status(HttpStatus.CREATED.value())
                .message("Given user details are successfully registered")
                .body(SuccessRegisterResponse(savedJobSeeker))
                .build();
    }

    @Override
    public ApiResponse<?> login(JobSeekerAuthReq jobSeekerAuthReq) {
        Optional<JobSeeker> jobseeker = jobSeekerProfileRepository.getByUsername(jobSeekerAuthReq.getUsername());
        if (jobseeker.isPresent()) {
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        jobSeekerAuthReq.getUsername(), jobSeekerAuthReq.getPassword(), jobseeker.get().getAuthorities());
                authentication = authenticationProviderService.authenticate(authentication);
                if (authentication.isAuthenticated()) {
                    String accessToken = jwtTokenUtils.generateAccessToken(jobseeker.get());
                    String refreshToken = jwtTokenUtils.generateRefreshToken(jobseeker.get());
                    return ApiResponse.builder()
                            .ok(true)
                            .status(HttpStatus.ACCEPTED.value())
                            .message("Logged in Successfully!")
                            .body(getSuccessfulResponse(jobseeker.get(), accessToken, refreshToken))
                            .build();
                } else {
                    return ApiResponse.builder()
                            .ok(false)
                            .message("Sorry, there is an internal error, we working on it.")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .build();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.NOT_ACCEPTABLE.value())
                        .message("Username or/and Password Not Correct!")
                        .build();
            }
        }

        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .message("Username or/and Password Not Correct!")
                .build();
    }

    private JobSeeker createJobSeeker(JobSeekerRegisterRequest jobSeekerRegisterRequest) {
        String securePassword = passwordEncoder.encode(jobSeekerRegisterRequest.getPassword());
        User user = User.builder().username(jobSeekerRegisterRequest.getUsername())
                .password(securePassword)
                .role(RoleEnum.ROLE_JOBSEEKER)
                .provider(AuthProviders.local)
                .providerId(LOCAL_PROVIDER_ID)
                .build();
        return new JobSeeker(jobSeekerRegisterRequest.getFirstName(), jobSeekerRegisterRequest.getLastName(), new Date(),
                user, jobSeekerRegisterRequest.getJobTitle());
    }

}
