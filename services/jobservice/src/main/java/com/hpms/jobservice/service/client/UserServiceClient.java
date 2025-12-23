package com.hpms.jobservice.service.client;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.jobservice.dto.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.UUID;

@HttpExchange(url = "http://user-service/api/users")
public interface UserServiceClient {

    @GetExchange("/jobs/post-related-data")
    JobRelatedDataDTO getJobsRelatedData(@RequestBody JobRelatedDataRequest request);

    @GetExchange("/jobs/company-recruiter-id/{userId}")
    CompanyAndRecruiterIds getCompanyAndRecruiterIds(@PathVariable UUID userId);
}
