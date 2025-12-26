package com.hpms.applicationservice.controller;

import com.hpms.applicationservice.constants.AssessmentStatus;
import com.hpms.applicationservice.constants.EvaluationType;
import com.hpms.applicationservice.dto.AssessmentDTO;
import com.hpms.applicationservice.service.impl.AssessmentService;
import com.hpms.commonlib.dto.ApiResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Log
@RestController
@RequestMapping("/api/apps")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping("/assessment")
    public ApiResponse<?> addAssessment(@RequestHeader(name = "Authorization") String token,
                                         @RequestBody AssessmentDTO assessmentDTO) throws Exception {
        return assessmentService.addAssessment(token, assessmentDTO);
    }

    @GetMapping("/assessment")
    public ApiResponse<?> getAssessmentForEmployer(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam UUID assessmentId){
        return assessmentService.getAssessmentForEmployer(token, assessmentId);
    }

    @GetMapping("/assessments")
    public ApiResponse<?> getAssessmentsForApplication(@RequestHeader(name = "Authorization") String token,
                                        @RequestParam UUID applicationId){
        return assessmentService.getEvaluationsForApplication(token, applicationId, EvaluationType.Assessment);
    }

    @PutMapping("/assessment")
    public ApiResponse<?> editAssessment(@RequestHeader(name = "Authorization") String token,
                                         @RequestParam UUID evaluationId,
                                         @RequestBody AssessmentDTO assessmentDTO) {
        return assessmentService.editAssessment(token, evaluationId, assessmentDTO);
    }

    @PutMapping("/assessment-status")
    public ApiResponse<?> editAssessmentStatus(@RequestHeader(name = "Authorization") String token,
                                               @RequestParam UUID evaluationId,
                                               @RequestParam AssessmentStatus assessmentStatus) {
        return assessmentService.editAssessmentStatus(token, evaluationId, assessmentStatus);
    }

    @PutMapping("/assessment-result")
    public ApiResponse<?> editAssessmentResult(@RequestHeader(name = "Authorization") String token,
                                               @RequestParam UUID evaluationId,
                                               @RequestParam String assessmentResult) {
        return assessmentService.editAssessmentResult(token, evaluationId, assessmentResult);
    }

    @DeleteMapping("/assessment")
    public ApiResponse<?> deleteAssessment(@RequestHeader(name = "Authorization") String token,
                                          @RequestParam UUID evaluationId) {
        return assessmentService.deleteAssessment(token, evaluationId);
    }

    @PutMapping("/evaluations/mark-viewed")
    public ApiResponse<?> markEvaluationsViewed(@RequestHeader(name = "Authorization") String token,
                                           @RequestParam UUID appId, @RequestParam EvaluationType evaluationType) {
        return assessmentService.markEvaluationsViewed(token, appId, evaluationType);
    }
}
