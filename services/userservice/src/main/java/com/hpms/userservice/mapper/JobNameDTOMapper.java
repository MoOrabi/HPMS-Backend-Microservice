package com.hpms.userservice.mapper;

import com.hpms.userservice.dto.JobNameDTO;
import com.hpms.userservice.model.shared.JobName;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class JobNameDTOMapper implements Function<JobName, JobNameDTO> {

    @Override
    public JobNameDTO apply(JobName jobName) {
        return JobNameDTO.builder()
                .id(jobName.getId())
                .name(jobName.getName())
                .build();
    }
}
