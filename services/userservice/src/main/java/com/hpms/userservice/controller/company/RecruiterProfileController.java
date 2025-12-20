package com.hpms.userservice.controller.company;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.company.CompanySubscriptionChoices;
import com.hpms.userservice.dto.company.RecruiterUpdateRequest;
import com.hpms.userservice.service.company.RecruiterProfileServices;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Log
@RestController
@RequestMapping("/api/users/recruiter-profile")
public class RecruiterProfileController {

    @Autowired
    private RecruiterProfileServices recruiterProfileService;


    @PostMapping(value = "/profile-photo",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> saveRecruiterProfilePhoto(@RequestHeader(name = "Authorization") String token,
                                                    @RequestPart(value = "photo") final MultipartFile profileFile)
            throws Exception {
        return recruiterProfileService.saveProfilePhoto(token, profileFile);
    }

    @DeleteMapping(value = "/profile-photo")
    public ApiResponse<?> removeRecruiterProfilePhoto(@RequestHeader(name = "Authorization") String token)
            throws Exception{
        return recruiterProfileService.removeRecruiterProfilePhoto(token);
    }

    @PostMapping(value = "/info")
    public ApiResponse<?> updateRecruiterInfo(@RequestHeader(name = "Authorization") String token,
                                              @RequestBody RecruiterUpdateRequest updateRequest)
            throws Exception {
        return recruiterProfileService.updateRecruiterInfo(token, updateRequest);
    }

    @GetMapping(value = "/profile-photo")
    public ApiResponse<?> getRecruiterProfilePhoto(@RequestHeader(name = "Authorization") String token) {
        return recruiterProfileService.getProfilePhoto(token);
    }

    @GetMapping(value = "/my-info")
    public ApiResponse<?> getMyInfo(@RequestHeader(name = "Authorization") String token) {
        return recruiterProfileService.getMyInfo(token);
    }

    @GetMapping(value = "/info")
    public ApiResponse<?> getRecruiterInfoById(@RequestParam(name = "id") String recruiterId) {
        return recruiterProfileService.getRecruiterInfoById(recruiterId);
    }


    @GetMapping(value = "/recruiter-company-basicInfo")
    public ApiResponse<?> getRecruiterCompanyBasicInfo(@RequestHeader(name = "Authorization") String token) {
        return recruiterProfileService.getRecruiterCompanyBasicInfo(token);
    }

    @GetMapping("subscriptionChoices")
    public ApiResponse<?> getRecruiterSubscriptionChoices(@RequestHeader("Authorization") String companyToken) {
        return recruiterProfileService.getRecruiterSubscriptionChoices(companyToken);
    }

    @PutMapping("subscriptionChoices")
    public ApiResponse<?> setRecruiterSubscriptionChoices(@RequestHeader("Authorization") String companyToken,
                                                 @RequestBody CompanySubscriptionChoices subscriptionChoices) {
        return recruiterProfileService.setRecruiterSubscriptionChoices(companyToken, subscriptionChoices);
    }

    @GetMapping("/statistics")
    public ApiResponse<?> getRecruiterStatistics(@RequestHeader("Authorization") String token){
        return recruiterProfileService.getRecruiterStatistics(token);
    }

}
