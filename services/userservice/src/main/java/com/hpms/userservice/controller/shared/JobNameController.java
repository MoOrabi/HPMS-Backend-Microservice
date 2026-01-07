package com.hpms.userservice.controller.shared;

import com.hpms.userservice.model.shared.JobName;
import com.hpms.userservice.service.shared.JobNameService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Log
@RestController
@RequestMapping("/api/users/APIs")
public class JobNameController {

    @Autowired
    private JobNameService jobNameService;

    @GetMapping("/jobNames")
    public List<JobName> getAllJobNames() {
        return jobNameService.getAllJobNames();
    }

    @GetMapping("/jobNamesLike")
    public List<JobName> getByNameLike(@RequestParam(name = "stringToSearch") String stringToSearch) {
        return jobNameService.getByNameLike(stringToSearch);
    }

    @GetMapping("/jobNamesLimit")
    public List<JobName> findAllWithLimit(@RequestParam(name = "limit") int limit) {
        return jobNameService.findAllWithLimit(limit);
    }
}
