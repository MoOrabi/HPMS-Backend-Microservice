package com.hpms.referenceservice.service.impl;

import com.hpms.referenceservice.model.Skill;
import com.hpms.referenceservice.repository.SkillRepository;
import com.hpms.referenceservice.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class SkillServiceImp implements SkillService {

    @Autowired
    private SkillRepository skillRepository;

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public List<Skill> getByNameLike(String name) {
        return skillRepository.getByNameLike(name);
    }

    public List<Skill> findAllWithLimit(int limit) {
        return skillRepository.findAllWithLimit(limit);
    }
}
