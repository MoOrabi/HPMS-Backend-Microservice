package com.hpms.jobservice.service.imp;

import com.hpms.jobservice.dto.JobPostDto;
import com.hpms.jobservice.mapper.JobPostMapper;
import com.hpms.jobservice.model.JobPost;
import com.hpms.jobservice.repository.JobPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserRelatedServiceImpl {
    private final JobPostRepository jobPostRepository;
    private final JobPostMapper jobPostMapper;

    public int countRecruiterJobs(UUID recruiterId) {
        return jobPostRepository.countActiveJobsForRecruiter(recruiterId);
    }

    public int countCompanyActiveJobs(UUID companyId) {
        return jobPostRepository.countActiveJobsForCompany(companyId);
    }

    public List<JobPostDto> getCompnayRecentJobPosts(UUID companyId) {
        Pageable pageable = PageRequest.of(0, 2, Sort.by(
                Sort.Order.asc("publishedOn")
        ));
        Page<JobPost> jobPostPage = jobPostRepository.getByCompanyId(companyId,pageable);
        return jobPostPage.map(jobPostMapper::toPublicDto).stream().toList();
    }
}
