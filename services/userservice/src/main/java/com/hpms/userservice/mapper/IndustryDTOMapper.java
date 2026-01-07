package com.hpms.userservice.mapper;

import com.hpms.userservice.dto.IndustryDTO;
import com.hpms.userservice.model.shared.Industry;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class IndustryDTOMapper implements Function<Industry, IndustryDTO> {

    @Override
    public IndustryDTO apply(Industry industry) {
        return IndustryDTO.builder()
                .id(industry.getId())
                .name(industry.getName())
                .build();
    }
}
