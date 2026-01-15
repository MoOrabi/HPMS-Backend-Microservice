package com.hpms.referenceservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.dto.SelectOption;
import com.hpms.referenceservice.service.PublicDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("api/references/jobs")
@RequiredArgsConstructor
public class PublicDataController {
    private final PublicDataService publicDataService;

    @GetMapping("skills-name")
    public Set<SelectOption> getSkillName(@RequestBody Set<Long> skillIds) {
        return publicDataService.getSkillsNames(skillIds);
    }
}
