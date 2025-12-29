package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.repository.JobApplicationRepository;
import com.hpms.commonlib.dto.DeleteJobRelatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DeleteJobRelatedServiceImpl {

    private final JobApplicationRepository jobApplicationRepository;

    public void deleteJobRelatedData(DeleteJobRelatedEvent deleteEvent) {
        jobApplicationRepository.deleteByJobPostId(deleteEvent.getJobId());
    }
}
