package com.hpms.applicationservice.service.impl;

import com.hpms.applicationservice.repository.JobApplicationRepository;
import com.hpms.commonlib.dto.DeleteJobRelatedEvent;
import com.hpms.commonlib.dto.DeleteUserRelatedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DeleteUserRelatedServiceImpl {
    private final JobApplicationRepository jobApplicationRepository;

    public void deleteUserRelatedData(DeleteUserRelatedEvent deleteEvent) {
        jobApplicationRepository.deleteByJobSeekerId(deleteEvent.getUserId());
    }
}
