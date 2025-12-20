package com.hpms.userservice.controller.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.Education;
import com.hpms.userservice.service.jobseeker.EducationService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Log
@RestController
@RequestMapping("/api/users/jobseeker-education")
public class EducationController {

    @Autowired
    private EducationService educationService;

    @PostMapping
    public ApiResponse<?> saveEducation(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody Education education) {
        return educationService.addEducation(token, education);
    }

    @PutMapping
    public ApiResponse<?> editEducation(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody Education education,
                                        @RequestParam("eduId") String id) {
        return educationService.editEducation(token, education, id);
    }

    @DeleteMapping
    public ApiResponse<?> deleteEducation(@RequestHeader(name = "Authorization") String token,
                                          @RequestParam("eduId") String id) {
        return educationService.deleteEducation(token, id);
    }

    @GetMapping
    public ApiResponse<?> getEducations(@RequestHeader(name = "Authorization") String token) {
        return educationService.getEducations(token);
    }
}
