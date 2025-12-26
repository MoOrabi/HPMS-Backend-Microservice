package com.hpms.applicationservice.controller;

import com.hpms.applicationservice.dto.ApplicationCommentDTO;
import com.hpms.applicationservice.service.impl.ApplicationCommentService;
import com.hpms.commonlib.dto.ApiResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log
@RestController
@RequestMapping("/api/apps")
public class ApplicationCommentController {

    @Autowired
    private ApplicationCommentService applicationCommentService;

    @PostMapping("comment")
    public ApiResponse<?> addApplicationComment(@RequestHeader(name = "Authorization") String token,
                                                @RequestBody ApplicationCommentDTO commentDTO) {
        return applicationCommentService.addApplicationComment(token, commentDTO);
    }

    @GetMapping("comments")
    public ApiResponse<?> getCommentsOfApplication(@RequestHeader(name = "Authorization") String token,
                                                   @RequestParam UUID appId) {
        return applicationCommentService.getCommentsOfApplication(token, appId);
    }
}
