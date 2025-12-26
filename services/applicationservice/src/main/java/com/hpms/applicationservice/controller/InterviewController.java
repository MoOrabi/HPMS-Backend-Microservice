package com.hpms.applicationservice.controller;

import com.hpms.applicationservice.constants.EvaluationType;
import com.hpms.applicationservice.constants.InterviewStatus;
import com.hpms.applicationservice.dto.InterviewDTO;
import com.hpms.applicationservice.service.impl.AssessmentService;
import com.hpms.applicationservice.service.impl.InterviewService;
import com.hpms.commonlib.dto.ApiResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log
@RestController
@RequestMapping("/api/apps")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping("/interview")
    public ApiResponse<?> addInterview(@RequestHeader(name = "Authorization") String token,
                                        @RequestBody InterviewDTO interviewDTO) {
        return interviewService.addInterview(token, interviewDTO);
    }

    @GetMapping("/interview")
    public ApiResponse<?> getInterviewForEmployer(@RequestHeader(name = "Authorization") String token,
                                                   @RequestParam UUID interviewId){
        return interviewService.getInterviewForEmployer(token, interviewId);
    }

    @GetMapping("/interviews")
    public ApiResponse<?> getInterviewsForApplication(@RequestHeader(name = "Authorization") String token,
                                                       @RequestParam UUID applicationId){
        return assessmentService.getEvaluationsForApplication(token, applicationId, EvaluationType.Interview);
    }

    @PutMapping("/interview")
    public ApiResponse<?> editInterview(@RequestHeader(name = "Authorization") String token,
                                         @RequestParam UUID evaluationId,
                                         @RequestBody InterviewDTO interviewDTO) {
        return interviewService.editInterview(token, evaluationId, interviewDTO);
    }

    @PutMapping("/interview-status")
    public ApiResponse<?> editInterviewStatus(@RequestHeader(name = "Authorization") String token,
                                              @RequestParam UUID evaluationId,
                                              @RequestParam InterviewStatus interviewStatus) {
        return interviewService.editInterviewStatus(token, evaluationId, interviewStatus);
    }

    @DeleteMapping("/interview")
    public ApiResponse<?> deleteInterview(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam UUID evaluationId) {
        return interviewService.deleteInterview(token, evaluationId);
    }

}
