package com.hpms.userservice.controller.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.jobseeker.JobSeekerBasicInfoDTO;
import com.hpms.userservice.dto.jobseeker.JobSeekerCareerInterestsDTO;
import com.hpms.userservice.dto.jobseeker.JobSeekerProfessionalInfoDTO;
import com.hpms.userservice.service.jobseeker.JobSeekerProfileService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Log
@RestController
@RequestMapping("/api/users/jobseeker-profile")
public class JobSeekerProfileController {

    @Autowired
    private JobSeekerProfileService jobSeekerProfileServices;

    @PostMapping(value = "/profile-photo",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> saveProfilePhoto(@RequestHeader(name = "Authorization") String token,
                                           @RequestPart(value = "photo") final MultipartFile profileFile) throws Exception {
        return jobSeekerProfileServices.saveProfilePhoto(token, profileFile);
    }


    @GetMapping(value = "/profile-photo")
    public ApiResponse<?> getProfilePhoto(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getProfilePhoto(token);
    }

    @PostMapping(value = "/basic-info")
    public ApiResponse<?> saveBasicInfo(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody JobSeekerBasicInfoDTO basicInfoDTO) {
        return jobSeekerProfileServices.saveBasicInfo(token, basicInfoDTO);
    }

    @GetMapping(value = "/basic-info")
    public ApiResponse<?> getBasicInfo(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getBasicInfo(token);
    }

    @PostMapping(value = "/career-interests")
    public ApiResponse<?> saveCareerInterests(@RequestHeader(name = "Authorization") String token,
                                              @RequestBody JobSeekerCareerInterestsDTO careerInterestsDTO) {
        return jobSeekerProfileServices.saveCareerInterests(token, careerInterestsDTO);
    }

    @GetMapping(value = "/career-interests")
    public ApiResponse<?> getCareerInterests(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getCareerInterests(token);
    }

    @PostMapping(value = "/professional-info")
    public ApiResponse<?> saveProfessionalInfo(@RequestHeader(name = "Authorization") String token,
                                               @RequestBody JobSeekerProfessionalInfoDTO professionalInfoDTO) {
        return jobSeekerProfileServices.saveProfessionalInfo(token, professionalInfoDTO);
    }

    @GetMapping(value = "/professional-info")
    public ApiResponse<?> getProfessionalInfo(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getProfessionalInfo(token);
    }

    @PostMapping(value = "/work-sample",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> addWorkSample(@RequestHeader(name = "Authorization") String token,
                                        @RequestPart(value = "photo") final MultipartFile profileFile,
                                        @RequestPart(value = "title", required = false) String title,
                                        @RequestPart(value = "description", required = false) String description,
                                        @RequestPart(value = "link", required = false) String link)
            throws Exception {
        return jobSeekerProfileServices.addWorkSample(token, profileFile, title, description, link);
    }

    @GetMapping(value = "/work-samples-urls")
    public ApiResponse<?> getWorkSamplesURLs(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getWorkSamples(token);
    }

    @GetMapping(value = "/work-samples-urls-by-userId")
    public ApiResponse<?> getWorkSamplesByUserId(@RequestHeader(name = "Authorization") String token,@RequestParam String userId){
        return jobSeekerProfileServices.getWorkSamplesById(token ,userId) ;
    }


    @DeleteMapping(value = "/work-sample")
    public ApiResponse<?> deleteWorkSample(@RequestHeader(name = "Authorization") String token
            , @RequestParam("sampleId") String sampleId) {
        return jobSeekerProfileServices.deleteWorkSample(token, sampleId);
    }




    @PostMapping(value = "/cv",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> addCV(@RequestHeader(name = "Authorization") String token,
                                @RequestPart(value = "file") final MultipartFile file)
            throws Exception {
        return jobSeekerProfileServices.setCV(token, file);
    }

    @GetMapping(value = "/cv-url")
    public ApiResponse<?> getCV(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getCV(token);
    }

    @DeleteMapping(value = "/cv")
    public ApiResponse<?> deleteCV(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.deleteCV(token);
    }

    @PostMapping(value = "/job-title")
    public ApiResponse<?> saveJobTitle(@RequestHeader(name = "Authorization") String token,
                                       @RequestBody String jobTitle) {
        return jobSeekerProfileServices.setJobTitle(token, jobTitle);
    }

    @GetMapping(value = "/job-title")
    public ApiResponse<?> getJobTitle(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getJobTitle(token);
    }

    @GetMapping(value = "/is-mobile-exist")
    public ApiResponse<?> isMobileNumberExist(@RequestParam(name = "prefix") String prefix,
                                              @RequestParam(name = "number") String number) {
        return jobSeekerProfileServices.checkNumberIsExist(prefix, number);
    }

    @GetMapping(value = "/other-info")
    public ApiResponse<?> getAllInfoOfOtherJobSeeker(@RequestHeader(name = "Authorization") String token,
                                                     @RequestParam("userId") UUID userId) {
        return jobSeekerProfileServices.getJobSeekerAllInfo(token, userId);
    }

    @PostMapping(value = "/receive-job-alerts-status")
    public ApiResponse<?> setJobAlerts(@RequestHeader(name = "Authorization") String token,
                                       @RequestParam(name = "value") boolean value) {
        return jobSeekerProfileServices.setJobAlertsStatus(token, value);
    }

    @GetMapping(value = "/receive-job-alerts-status")
    public ApiResponse<?> getJobAlertsStatus(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getJobAlertsStatus(token);
    }

    @PostMapping(value = "/private-status")
    public ApiResponse<?> setPrivateStatus(@RequestHeader(name = "Authorization") String token,
                                           @RequestParam(name = "value") boolean value) {
        return jobSeekerProfileServices.setPrivateStatus(token, value);
    }

    @GetMapping(value = "/private-status")
    public ApiResponse<?> getPrivateStatus(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getPrivateStatus(token);
    }

    @GetMapping(value = "/name-email")
    public ApiResponse<?> getNameAndEmail(@RequestHeader(name = "Authorization") String token) {
        return jobSeekerProfileServices.getNameAndEmail(token);
    }


}
