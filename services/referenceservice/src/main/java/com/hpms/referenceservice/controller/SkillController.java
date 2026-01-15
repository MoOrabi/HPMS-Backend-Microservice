package com.hpms.referenceservice.controller;

import com.hpms.referenceservice.model.Skill;
import com.hpms.referenceservice.service.impl.SkillServiceImp;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Log
@RestController
@RequestMapping("/api/references/APIs")
public class SkillController {

    @Autowired
    private SkillServiceImp skillService;

    @GetMapping("/skills")
    public List<Skill> getAllSkillNames() {
        return skillService.getAllSkills();
    }

    @GetMapping("/skillsLike")
    public List<Skill> getByNameLike(@RequestParam(name = "stringToSearch") String stringToSearch) {
        return skillService.getByNameLike(stringToSearch);
    }

    @GetMapping("/skillsLimit")
    public List<Skill> getAllWithLimitHundred(@RequestParam(name = "limit") int limit) {
        return skillService.findAllWithLimit(limit);
    }
}
