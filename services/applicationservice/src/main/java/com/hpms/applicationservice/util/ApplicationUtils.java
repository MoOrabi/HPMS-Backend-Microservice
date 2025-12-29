package com.hpms.applicationservice.util;

import com.hpms.applicationservice.dto.JobPostCompanyRecruitersAndCreator;
import com.hpms.applicationservice.model.JobApplication;
import com.hpms.applicationservice.model.Offer;
import com.hpms.applicationservice.repository.JobApplicationRepository;
import com.hpms.applicationservice.repository.OfferRepository;
import com.hpms.applicationservice.service.client.JobServiceClient;
import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.commonlib.util.FileUploadUtil;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApplicationUtils {

    private final JobApplicationRepository jobApplicationRepository;

    private final OfferRepository offerRepository;

    private final JobServiceClient jobServiceClient;

    public ApiResponse<?> getJobApplication(UUID jobApplicationId) {
        Optional<JobApplication> jobApplicationOptional = jobApplicationRepository
                .findById(jobApplicationId);

        if (jobApplicationOptional.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("No Application available for that id.")
                    .build();
        } else {
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .body(jobApplicationOptional.get())
                    .build();
        }
    }

    public boolean checkJobSeekerIsConcernedWithApplication(UUID userId, JobApplication jobApplication){
        return jobApplication.getJobSeekerId().equals(userId);
    }

    public boolean checkEmployerIsConcernedWithApplication(UUID userId, JobApplication jobApplication){

        JobPostCompanyRecruitersAndCreator jobPostCompanyRecruitersAndCreator = JobPostCompanyRecruitersAndCreator.builder().build();
        try {
            jobPostCompanyRecruitersAndCreator = jobServiceClient.getJobPostCompanyRecruitersAndCreator(jobApplication.getJobPostId());
        } catch (ServiceCommunicationException e) {
            log.error(e.getMessage(), e);
        }
        Set<UUID> recruiterTeamUsers = jobPostCompanyRecruitersAndCreator.getRecruiterIds();
        return jobPostCompanyRecruitersAndCreator.getCompanyId().equals(userId) ||
                recruiterTeamUsers.contains(userId) || jobPostCompanyRecruitersAndCreator.getCreatorId().equals(userId);
    }

    public boolean checkUserIsConcernedWithApplication(UUID userId, JobApplication jobApplication){
        return checkEmployerIsConcernedWithApplication(userId, jobApplication) ||
                checkJobSeekerIsConcernedWithApplication(userId, jobApplication);
    }

    public ApiResponse<?> addFileToOffer(String token, MultipartFile file, String place,
                                                         int sizeInKiloBytes, String directory,
                                                         String successMessage,
                                                         HashSet<String> allowedExtensions, UUID offerId) throws Exception {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        String fileName = StringUtils.cleanPath(Objects
                .requireNonNull(file.getOriginalFilename()));

        if (file.getContentType() == null || !allowedExtensions.contains(file.getContentType())) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(file.getOriginalFilename() + " has no valid extension")
                    .build();
        }

        if (file.getSize() > sizeInKiloBytes * 1024L) {
            return ApiResponse.builder().ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(file.getOriginalFilename() + " size is above allowed size( "+sizeInKiloBytes/1024+"MB).")
                    .build();
        }



        String uploadDir = directory + userId + "/" + offerId.toString();

        Optional<Offer> optionalOffer = offerRepository.findById(offerId);

        if(optionalOffer.isEmpty()){
            return ApiResponse.builder().ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no offer for the entered offer id")
                    .build();
        }

        Offer offer = optionalOffer.get();

        JobApplication jobApplication = offer.getApplication();

        if(!((place.equals("emp-offer") && checkEmployerIsConcernedWithApplication(userId, jobApplication)) ||
                (place.equals("js-offer") && checkJobSeekerIsConcernedWithApplication(userId, jobApplication)))){
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to edit this offer")
                    .build();
        }

        String pathWithFileName = FileUploadUtil
                .saveFile(uploadDir, fileName, file);
        switch (place) {
            case "emp-offer" -> {
                offer.setDocumentByCompany(pathWithFileName);
                offer.setSizeOfDocumentByCompany((double) file.getSize() /(1024*1024));
            }
            case "js-offer" -> {
                offer.setDocumentByJobSeeker(pathWithFileName);
                offer.setSizeOfDocumentByJobSeeker((double) file.getSize() /(1024*1024));
            }
            default -> {
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(file.getOriginalFilename() + " has no valid place")
                        .build();
            }
        }

        offerRepository.save(offer);
        return ApiResponse.builder().ok(true)
                .status(HttpStatus.OK.value())
                .body(pathWithFileName)
                .message(successMessage).build();
    }

}
