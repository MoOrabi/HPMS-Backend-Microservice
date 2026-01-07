package com.hpms.userservice.service.shared;

import com.hpms.userservice.model.shared.JobName;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface JobNameService {

    public List<JobName> getAllJobNames();

    public List<JobName> getByNameLike(String name);

    public List<JobName> findAllWithLimit(int limit);
}
