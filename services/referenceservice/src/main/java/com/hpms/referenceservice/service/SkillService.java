package com.hpms.referenceservice.service;

import com.hpms.referenceservice.model.Skill;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface SkillService {
    public List<Skill> getAllSkills();

    public List<Skill> getByNameLike(String name);

    public List<Skill> findAllWithLimit(int limit);
}
