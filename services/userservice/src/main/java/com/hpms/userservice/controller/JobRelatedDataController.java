package com.hpms.userservice.controller;

import com.hpms.userservice.dto.CompanyAndRecruiterIds;
import com.hpms.userservice.dto.CompanyLocationAndLogoDTO;
import com.hpms.userservice.dto.JobRelatedDataDTO;
import com.hpms.userservice.dto.JobRelatedDataRequest;
import com.hpms.userservice.service.JobRelatedDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/jobs")
@RequiredArgsConstructor
public class JobRelatedDataController {

    private final JobRelatedDataService jobRelatedDataService;

    @GetMapping("/post-related-data")
    public JobRelatedDataDTO getJobRelatedData(@RequestBody JobRelatedDataRequest request) {
        return jobRelatedDataService.getJobRelatedData(request);
    }

    @GetMapping("/company-recruiter-id/{userId}")
    public CompanyAndRecruiterIds getCompanyAndRecruiterIds(@PathVariable UUID userId) {
        return jobRelatedDataService.getCompanyAndRecruiterIds(userId);
    }

    @GetMapping("/company-location-logo")
    List<CompanyLocationAndLogoDTO> getCompanyLocationAndLogos(@RequestBody List<UUID> companyIds) {
        return jobRelatedDataService.getCompanyLocationAndLogos(companyIds);
    }

}
