package com.hpms.userservice.controller.company;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.company.CompanyAboutInfoRequest;
import com.hpms.userservice.dto.company.CompanyBasicInfoRequest;
import com.hpms.userservice.dto.company.CompanySubscriptionChoices;
import com.hpms.userservice.model.shared.Benefit;
import com.hpms.userservice.service.company.CompanyProfileServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/users/company-profile/")
public class CompanyProfileController {

    @Autowired
    private CompanyProfileServices companyProfileServices;

    @GetMapping("{companyId}")
    public ApiResponse<?> getAllCompanyInfo(@PathVariable UUID companyId) {
        return companyProfileServices.getAllCompanyInfo(companyId);
    }

    @PutMapping("basic-info")
    public ApiResponse<?> updateBasicInfo(@RequestHeader("Authorization") String companyToken,
                                          @RequestBody CompanyBasicInfoRequest basicInfoDto) {
        return companyProfileServices.updateBasicInfo(companyToken, basicInfoDto);
    }

    @PutMapping("about-info")
    public ApiResponse<?> updateAboutInfo(@RequestHeader("Authorization") String companyToken,
                                          @RequestBody CompanyAboutInfoRequest aboutInfoRequest) {
        return companyProfileServices.updateAboutInfo(companyToken, aboutInfoRequest);
    }

    @PutMapping("subscribe/{companyId}")
    public ApiResponse<?> subscribeCompany(@RequestHeader("Authorization") String userToken,
                                           @PathVariable("companyId") UUID companyId) {
        return companyProfileServices.subscribeToCompany(userToken, companyId);
    }

    @PostMapping(value = "/profile-photo",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> saveCompanyProfilePhoto(@RequestHeader(name = "Authorization") String token,
                                                  @RequestPart(value = "photo") final MultipartFile profileFile)
            throws Exception {
        return companyProfileServices.saveLogo(token, profileFile);
    }

    @GetMapping(value = "/profile-photo")
    public ApiResponse<?> getCompanyProfilePhoto(@RequestHeader(name = "Authorization") String token) {
        return companyProfileServices.getLogo(token);
    }

    @PostMapping(value = "/cover-photo",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> saveCompanyCoverPhoto(@RequestHeader(name = "Authorization") String token,
                                                @RequestPart(value = "photo") final MultipartFile coverFile)
            throws Exception {
        return companyProfileServices.saveCoverPhoto(token, coverFile);
    }

    @GetMapping(value = "/cover-photo")
    public ApiResponse<?> getCompanyCoverPhoto(@RequestHeader(name = "Authorization") String token) {
        return companyProfileServices.getCoverPhoto(token);
    }

    @GetMapping(value = "is-mobile-exist")
    public ApiResponse<?> isMobileNumberExist(@RequestParam(name = "mobileNumberCountryCode") String mobileNumberCountryCode,
                                              @RequestParam(name = "mobileNumber") String mobileNumber) {
        return companyProfileServices.checkNumberIsExist(mobileNumberCountryCode, mobileNumber);
    }

    @PutMapping("/benefits")
    public ApiResponse<?> updateBenefits(@RequestHeader(name = "Authorization") String companyToken,
                                         @RequestBody List<Benefit> benefits) {
        return companyProfileServices.updateBenefits(companyToken, benefits);
    }

    @GetMapping("/statistics")
    public ApiResponse<?> getCompanyStatistics(@RequestHeader("Authorization") String companyToken){
        return companyProfileServices.getCompanyStatistics(companyToken);
    }

    @GetMapping("/name")
    public ApiResponse<?> getNameOfUser(@RequestHeader("Authorization") String companyToken){
        return  companyProfileServices.getNameOfCompany(companyToken) ;
    }

    @GetMapping("subscriptionChoices")
    public ApiResponse<?> getCompanySubscriptionChoices(@RequestHeader("Authorization") String companyToken) {
        return companyProfileServices.getCompanySubscriptionChoices(companyToken);
    }

    @PutMapping("subscriptionChoices")
    public ApiResponse<?> setCompanySubscriptionChoices(@RequestHeader("Authorization") String companyToken,
                                          @RequestBody CompanySubscriptionChoices subscriptionChoices) {
        return companyProfileServices.setCompanySubscriptionChoices(companyToken, subscriptionChoices);
    }

}
