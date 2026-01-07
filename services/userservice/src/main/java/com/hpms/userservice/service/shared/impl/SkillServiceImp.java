package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.Skill;
import com.hpms.userservice.repository.shared.SkillRepository;
import com.hpms.userservice.service.shared.SkillService;
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
