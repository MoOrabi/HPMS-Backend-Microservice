package com.hpms.userservice.service.company;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.jobservice.service.client.JobServiceClient;
import com.hpms.userservice.dto.company.*;
import com.hpms.userservice.mapper.RecruiterInfoMapper;
import com.hpms.userservice.model.Recruiter;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.RecruiterRepository;
import com.hpms.userservice.utils.FrequentlyUsed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

@Service
public class RecruiterProfileServices {

    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private JobServiceClient jobServiceClient;
    @Autowired
    private FrequentlyUsed frequentlyUsed;
    @Autowired
    private RecruiterInfoMapper recruiterInfoMapper;

    public ApiResponse<?> saveProfilePhoto(String token, MultipartFile profileFile) throws Exception {
        return frequentlyUsed.addFileToUser(token, profileFile, "rec-profile", 2 * 1048,
                "recruiter-files/profile/",
                "Photo Uploaded Successfully", recruiterRepository, FrequentlyUsed.imageExtensions, null);
    }

    public ApiResponse<?> getProfilePhoto(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }

        String filePathWithName = recruiter.getProfilePhoto();

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(filePathWithName)
                .build();
    }

    public ApiResponse<?> getMyInfo(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }

        RecruiterInfo recruiterInfo = recruiterInfoMapper.recruiterToInfo(recruiter);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(recruiterInfo)
                .build();
    }

    public ApiResponse<?> getRecruiterInfoById(String recruiterId) {
        Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(UUID.fromString(recruiterId));

        if (optionalRecruiter.isEmpty()) {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .ok(false)
                    .message("No recruiter for entered Id")
                    .build();
        } else {
            Recruiter recruiter = optionalRecruiter.get();
            RecruiterInfo recruiterInfo = recruiterInfoMapper.recruiterToInfo(recruiter);
            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .ok(true)
                    .body(recruiterInfo)
                    .build();
        }

    }

    public ApiResponse<?>updateRecruiterInfo(String token, RecruiterUpdateRequest updateRequest) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }

        recruiter.setFirstName(updateRequest.getFirstName());
        recruiter.setLastName(updateRequest.getLastName());
        recruiter.setPhoneNumberCountryCode(updateRequest.getPhoneNumberCountryCode());
        recruiter.setPhoneNumber(updateRequest.getPhoneNumber());

        recruiter = recruiterRepository.save(recruiter);

        RecruiterInfo recruiterInfo = recruiterInfoMapper.recruiterToInfo(recruiter);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(recruiterInfo)
                .message("Recruiter Updated Successfully")
                .build();

    }

    public ApiResponse<?> getRecruiterCompanyBasicInfo (String token ) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }


        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(
                        RecruiterCompanyBasicInfo.builder()
                                .firstName(recruiter.getFirstName())
                                .lastName(recruiter.getLastName())
                                .companyName(recruiter.getCompany().getName())
                                .companyId(recruiter.getCompany().getId().toString())
                                .build()
                )
                .message("Got Company name Successfully")
                .build();
    }

    public ApiResponse<?> getRecruiterSubscriptionChoices(String companyToken) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(companyToken);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }

        CompanySubscriptionChoices subscriptionChoices = new CompanySubscriptionChoices(
                recruiter.isReceiveNewApplicantsEmails(), recruiter.isReceiveTalentSuggestionsEmails());
        return ApiResponse.builder()
                .ok(true)
                .message(HttpStatus.OK.getReasonPhrase())
                .body(subscriptionChoices)
                .status(HttpStatus.OK.value())
                .build();
    }

    public ApiResponse<?> setRecruiterSubscriptionChoices(String token, CompanySubscriptionChoices subscriptionChoices) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }

        recruiter.setReceiveNewApplicantsEmails(subscriptionChoices.isReceiveNewApplicantsEmails());
        recruiter.setReceiveTalentSuggestionsEmails(subscriptionChoices.isReceiveTalentSuggestionsEmails());
        recruiterRepository.save(recruiter);
        return ApiResponse.builder()
                .ok(true)
                .message("Subscription choices saved")
                .body(subscriptionChoices)
                .status(HttpStatus.OK.value())
                .build();
    }


    public ApiResponse<?> removeRecruiterProfilePhoto(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }

        recruiter.setProfilePhoto("");
        recruiterRepository.save(recruiter);

        return ApiResponse.builder()
                .ok(true)
                .message("Recruiter Profile Photo Deleted successfully")
                .status(HttpStatus.OK.value())
                .build();

    }

    public ApiResponse<?> getRecruiterStatistics(String token) {

        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Recruiter recruiter;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            recruiter = (Recruiter) isThereUserFromToken.getBody();
        }

        int companyNumberOfMembers = companyRepository.getNumberOfRecruitersForCompany(recruiter.getCompany().getId());

        int activePostsNumber = jobServiceClient.countRecruiterActiveJobs(recruiter.getId());

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(RecruiterStatistics.builder()
                        .companyNumberOfMembers(companyNumberOfMembers)
                        .activePostsNumber(activePostsNumber)
                        .build())
                .build() ;
    }
}
