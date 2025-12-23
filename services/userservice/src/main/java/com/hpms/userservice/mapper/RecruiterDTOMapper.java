package com.hpms.userservice.mapper;

import com.hpms.userservice.dto.RecruiterNameAndPhoto;
import com.hpms.userservice.model.Recruiter;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class RecruiterDTOMapper implements Function<Recruiter, RecruiterNameAndPhoto> {

    @Override
    public RecruiterNameAndPhoto apply(Recruiter recruiter) {
        return RecruiterNameAndPhoto.builder()
                .id(recruiter.getId())
                .title(recruiter.getJobTitle())
                .photo(recruiter.getProfilePhoto())
                .name(recruiter.getName())
                .build();
    }
}
