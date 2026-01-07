package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.JobName;
import com.hpms.userservice.repository.shared.JobNameRepository;
import com.hpms.userservice.service.shared.JobNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JobNameServiceImp implements JobNameService {

    @Autowired
    private JobNameRepository jobNameRepository;

    public List<JobName> getAllJobNames() {
        return jobNameRepository.findAll();
    }

    public List<JobName> getByNameLike(String name) {
        return jobNameRepository.getByNameLike(name);
    }

    public List<JobName> findAllWithLimit(int limit) {
        return jobNameRepository.findAllWithLimit(limit);
    }
}
