package com.hpms.applicationservice.mapper;

import com.hpms.applicationservice.dto.InterviewDTO;
import com.hpms.applicationservice.model.Interview;
import org.mapstruct.Mapper;

import java.time.LocalDateTime;

@Mapper
public class InterviewMapper {

    public Interview interviewDTOToInterview(InterviewDTO interviewDTO) {
        return Interview.builder()
                .interviewStatus(interviewDTO.getStatus())
                .link(interviewDTO.getLink())
                .updatedAt(LocalDateTime.now())
                .startTime(interviewDTO.getStartTime())
                .endTime(interviewDTO.getEndTime())
                .title(interviewDTO.getTitle())
                .technicalType(interviewDTO.getTechnicalType())
                .build();
    }
}
