package com.hpms.userservice.mapper;

import com.hpms.userservice.dto.SkillDTO;
import com.hpms.userservice.model.shared.Skill;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class SkillDTOMapper implements Function<Skill, SkillDTO> {

    @Override
    public SkillDTO apply(Skill skill) {
        return SkillDTO.builder()
                .id(skill.getId())
                .name(skill.getName())
                .build();
    }
}
