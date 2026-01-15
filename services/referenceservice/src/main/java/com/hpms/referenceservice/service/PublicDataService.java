package com.hpms.referenceservice.service;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.referenceservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PublicDataService {
    private final SkillRepository skillRepository;

    public Set<SelectOption> getSkillsNames(@RequestBody Set<Long> skillIds) {
        return skillRepository.findAllNamesByIds(skillIds);
    }
}
