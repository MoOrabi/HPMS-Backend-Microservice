package com.hpms.userservice.mapper.jobseeker;

import com.hpms.userservice.dto.jobseeker.JobSeekerAllInfoDTO;
import com.hpms.userservice.dto.jobseeker.JobSeekerSimpleDto;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper
public interface JobSeekerSourceDestinationMapper {
    JobSeekerAllInfoDTO jobSeekerToJobSeekerInfoDTO(JobSeeker js);
//    JobSeeker jobSeekerInfoDTOToJobSeeker(JobSeekerAllInfoDTO jsAllInfo);

    JobSeekerSimpleDto toSimpleDto(JobSeeker jobSeeker);

   default Page<JobSeekerSimpleDto> toSimpleDtoPage(Page<JobSeeker> jobSeekerPage) {

       return jobSeekerPage.map(this::toSimpleDto);
   }
}
