package com.hpms.userservice.controller.shared;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.service.shared.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users/api/industries")
public class IndustriesController {

    @Autowired
    private IndustryService industryService;

    @GetMapping
    public ApiResponse<?> getIndustries() {
        return ApiResponse.builder()
                .message("Success")
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(industryService.getAllIndustries())
                .build();
    }

}
