package com.hpms.userservice.service.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.jobseeker.JobExperience;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.jobseeker.JobExperienceRepository;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;


@Service
public class JobExperienceService {

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Autowired
    private JobExperienceRepository jobExperienceRepository;
    @Autowired
    private JwtTokenUtils tokenUtils;
    @Autowired
    private FrequentlyUsed frequentlyUsed;

    public ApiResponse<?> addJobExperience(String token, JobExperience jobExperience) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        if (jobSeeker.getJobExperiences() == null) {
            jobSeeker.setJobExperiences(new HashSet<>());
        }
        jobExperience.setJobSeeker(jobSeeker);
        jobSeeker.getJobExperiences().add(jobExperience);
        jobSeekerProfileRepository.save(jobSeeker);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(jobExperience)
                .message("Job Experience Saved Successfully")
                .build();
    }

    public ApiResponse<?> editJobExperience(String token, JobExperience newJobExperience, String jobExperienceId) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Optional<JobExperience> optionalJobExperience = jobExperienceRepository.findById(
                UUID.fromString(jobExperienceId));
        if (optionalJobExperience.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no Job Experience for the entered id")
                    .build();
        }

        JobExperience jobExperience = optionalJobExperience.get();

        if (!jobExperience.getJobSeeker().equals(jobSeeker)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to change others job experiences")
                    .build();
        }

        jobExperience.setName(newJobExperience.getName());
        jobExperience.setJobCategory(newJobExperience.getJobCategory());
        jobExperience.setJobType(newJobExperience.getJobType());
        jobExperience.setPlace(newJobExperience.getPlace());
        jobExperience.setStartMonth(newJobExperience.getStartMonth());
        jobExperience.setEndMonth(newJobExperience.getEndMonth());
        jobExperience.setStartYear(newJobExperience.getStartYear());
        jobExperience.setEndYear(newJobExperience.getEndYear());
        jobExperience.setAbout(newJobExperience.getAbout());

        jobExperienceRepository.save(jobExperience);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(jobExperience)
                .message("Job Experience updated Successfully")
                .build();
    }

    public ApiResponse<?> deleteJobExperience(String token, String jobExperienceId) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Optional<JobExperience> optionalJobExperience = jobExperienceRepository.findById(
                UUID.fromString(jobExperienceId));
        if (optionalJobExperience.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no Job Experience for the entered id")
                    .build();
        }

        JobExperience jobExperience = optionalJobExperience.get();

        if (!jobExperience.getJobSeeker().equals(jobSeeker)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to delete others job experiences")
                    .build();
        }

        jobExperienceRepository.delete(jobExperience);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(true)
                .message("Job Experience deleted Successfully")
                .build();
    }

    public ApiResponse<?> getJobExperience(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(jobSeeker.getJobExperiences())
                .build();
    }
}
