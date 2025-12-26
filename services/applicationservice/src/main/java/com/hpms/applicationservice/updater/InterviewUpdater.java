package com.hpms.applicationservice.updater;

import com.hpms.applicationservice.dto.InterviewDTO;
import com.hpms.applicationservice.model.Interview;
import com.hpms.applicationservice.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class InterviewUpdater implements BiFunction<Interview, InterviewDTO, Interview> {

    private final InterviewRepository interviewRepository;

    @Override
    public Interview apply(Interview interview, InterviewDTO interviewDTO) {

        interview.setInterviewStatus(interviewDTO.getStatus());
        interview.setUpdatedAt(LocalDateTime.now());
        interview.setStartTime(interviewDTO.getStartTime());
        interview.setEndTime(interviewDTO.getEndTime());
        interview.setLink(interviewDTO.getLink());
        interview.setTitle(interviewDTO.getTitle());
        interview.setTechnicalType(interviewDTO.getTechnicalType());

        return interviewRepository.save(interview);
    }
}
