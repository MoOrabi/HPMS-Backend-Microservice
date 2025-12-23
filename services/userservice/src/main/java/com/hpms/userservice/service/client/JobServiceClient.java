package com.hpms.jobservice.service.client;

import com.hpms.userservice.dto.JobPostDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.UUID;

@HttpExchange(url = "http://job-service/api/jobs/users")
public interface JobServiceClient {
    @GetExchange("/recruiter-job-count/{recruiterId}")
    int countRecruiterActiveJobs(@PathVariable UUID recruiterId);

    @GetExchange("/company-job-count/{companyId}")
    int countCompanyActiveJobs(@PathVariable UUID companyId);

    @GetExchange("/company-recent-jobs/{companyId}")
    List<JobPostDto> getCompnayRecentJobPosts(@PathVariable UUID companyId);
}
