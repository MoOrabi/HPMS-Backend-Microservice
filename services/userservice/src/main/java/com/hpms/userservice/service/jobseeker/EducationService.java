package com.hpms.userservice.service.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.Education;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.jobseeker.EducationRepository;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;


@Service
public class EducationService {

    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Autowired
    private EducationRepository educationRepository;
    @Autowired
    private JwtTokenUtils tokenUtils;
    @Autowired
    private FrequentlyUsed frequentlyUsed;

    public ApiResponse<?> addEducation(String token, Education education) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }


        if (jobSeeker.getEducations() == null) {
            jobSeeker.setEducations(new HashSet<>());
        }
        education.setJobSeeker(jobSeeker);
        jobSeeker.getEducations().add(education);
        jobSeekerProfileRepository.save(jobSeeker);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(education)
                .message("Education Saved Successfully")
                .build();
    }

    public ApiResponse<?> editEducation(String token, Education newEducation, String educationId) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Optional<Education> optionalEducation = educationRepository.findById(UUID.fromString(educationId));

        if (optionalEducation.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no Education for the entered id")
                    .build();
        }

        Education education = optionalEducation.get();

        if (!education.getJobSeeker().equals(jobSeeker)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to change others educations")
                    .build();
        }


        education.setDegree(newEducation.getDegree());
        education.setInstitution(newEducation.getInstitution());
        education.setFieldOfStudy(newEducation.getFieldOfStudy());
        education.setStart(newEducation.getStart());
        education.setEnd(newEducation.getEnd());
        education.setGrade(newEducation.getGrade());
        educationRepository.save(education);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(education)
                .message("Education updated Successfully")
                .build();
    }

    public ApiResponse<?> deleteEducation(String token, String educationId) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Optional<Education> optionalEducation = educationRepository.findById(UUID.fromString(educationId));

        if (optionalEducation.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no Education for the entered id")
                    .build();
        }

        Education education = optionalEducation.get();

        if (!education.getJobSeeker().equals(jobSeeker)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to delete others educations")
                    .build();
        }

        educationRepository.delete(education);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(true)
                .message("Education deleted Successfully")
                .build();
    }

    public ApiResponse<?> getEducations(String token) {
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
                .body(jobSeeker.getEducations())
                .build();
    }
}
