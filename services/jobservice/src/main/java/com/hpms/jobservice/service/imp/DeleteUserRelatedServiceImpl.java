package com.hpms.jobservice.service.imp;

import com.hpms.commonlib.constants.RoleEnum;
import com.hpms.commonlib.dto.DeleteUserRelatedEvent;
import com.hpms.jobservice.repository.JobPostRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DeleteUserRelatedServiceImpl {
    private final JobPostRepository jobPostRepository;
    public void deleteUserRelatedData(DeleteUserRelatedEvent deleteEvent) {
        jobPostRepository.deleteByCompanyIdOrCreatorId(deleteEvent.getUserId(), deleteEvent.getUserId());
        if(deleteEvent.getRoleEnum().equals(RoleEnum.ROLE_RECRUITER)) {
            jobPostRepository.deleteRecruiterFromRecruiterIdsById(deleteEvent.getUserId());
        }

        if(deleteEvent.getRoleEnum().equals(RoleEnum.ROLE_JOBSEEKER)) {
            jobPostRepository.deleteSaverFromJobseekerSaversById(deleteEvent.getUserId());
        }
    }
}
