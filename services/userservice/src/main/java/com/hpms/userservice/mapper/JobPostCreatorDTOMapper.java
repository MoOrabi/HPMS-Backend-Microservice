package com.hpms.userservice.mapper;

import com.hpms.userservice.constants.RoleEnum;
import com.hpms.userservice.dto.JobPostCreatorDTO;
import com.hpms.userservice.model.Recruiter;
import com.hpms.userservice.model.User;
import com.hpms.userservice.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JobPostCreatorDTOMapper implements Function<User, JobPostCreatorDTO> {
    private final RecruiterRepository recruiterRepository;

    @Override
    public JobPostCreatorDTO apply(User user) {
        JobPostCreatorDTO jobPostCreatorDTO = JobPostCreatorDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .build();
        if(user.getRole().equals(RoleEnum.ROLE_RECRUITER)) {
            Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(user.getId());
            if(optionalRecruiter.isEmpty()) {
                return null;
            }
            Recruiter recruiter = optionalRecruiter.get();
            jobPostCreatorDTO.setTitle(recruiter.getJobTitle());
            jobPostCreatorDTO.setImageUrl(recruiter.getProfilePhoto());
        }
        return jobPostCreatorDTO;
    }
}
