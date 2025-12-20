package com.hpms.userservice.service.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.jobseeker.Certificate;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.jobseeker.CertificateRepository;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private JwtTokenUtils tokenUtils;
    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Autowired
    private FrequentlyUsed frequentlyUsed;

    public ApiResponse<?> addCertificate(String token, Certificate certificate) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        if (jobSeeker.getCertificates() == null) {
            jobSeeker.setCertificates(new HashSet<>());
        }

        certificate.setJobSeeker(jobSeeker);
        jobSeeker.getCertificates().add(certificate);
        jobSeekerProfileRepository.save(jobSeeker);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(certificate)
                .message("Certificate Saved Successfully")
                .build();
    }

    public ApiResponse<?> editCertificate(String token, Certificate newCertificate, String certificateId) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Optional<Certificate> optionalCertificate = certificateRepository.findById(UUID.fromString(certificateId));

        if (optionalCertificate.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no Certificate for the entered id")
                    .build();
        }

        Certificate certificate = optionalCertificate.get();

        if (!certificate.getJobSeeker().equals(jobSeeker)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to change others certificates")
                    .build();
        }

        certificate.setTitle(newCertificate.getTitle());
        certificate.setMonth(newCertificate.getMonth());
        certificate.setYear(newCertificate.getYear());

        certificateRepository.save(certificate);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(certificate)
                .message("Certificate updated Successfully")
                .build();
    }

    public ApiResponse<?> deleteCertificate(String token, String certificateId) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Optional<Certificate> optionalCertificate = certificateRepository.findById(UUID.fromString(certificateId));

        if (optionalCertificate.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no Certificate for the entered id")
                    .build();
        }

        Certificate certificate = optionalCertificate.get();

        if (!certificate.getJobSeeker().equals(jobSeeker)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to delete others certificates")
                    .build();
        }

        certificateRepository.delete(certificate);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(certificate)
                .message("Certificate deleted Successfully")
                .build();
    }

    public ApiResponse<?> getCertificates(String token) {
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
                .body(jobSeeker.getCertificates())
                .build();
    }
}
