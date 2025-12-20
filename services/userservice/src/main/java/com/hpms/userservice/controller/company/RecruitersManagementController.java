package com.hpms.userservice.controller.company;


import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.company.RecruiterEmailAndTitle;
import com.hpms.userservice.service.company.RecruitersManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/company-recruiters")
public class RecruitersManagementController {

    @Autowired
    private RecruitersManagementService managementService;

    @GetMapping("all")
    public ApiResponse<?> getAllRecruiters(@RequestParam(name = "id") UUID companyId) {
        return managementService.getAllRecruiters(companyId);
    }

    @GetMapping("all-invitations")
    public ApiResponse<?> getAllInvitations(@RequestHeader("Authorization") String token) {
        return managementService.getRecruitersInvitations(token);
    }

    @GetMapping("all-valid-invitations")
    public ApiResponse<?> getValidInvitations(@RequestHeader("Authorization") String token) {
        return managementService.getValidInvitations(token);
    }

    @GetMapping("my-all")
    public ApiResponse<?> getMyAllRecruiters(@RequestHeader("Authorization") String token) {
        return managementService.getMyAllRecruiters(token);
    }

    @PostMapping("invite")
    public ApiResponse<?> inviteRecruiter(@RequestHeader("Authorization") String token,
                                          @RequestParam(name = "recEmail") @Valid String email,
                                          @RequestParam(name = "recJobTitle") @Valid String jobTitle) {
        return managementService.inviteRecruiter(token, email, jobTitle);
    }

    @PostMapping("update")
    public ApiResponse<?> updateRecruiter(@RequestHeader("Authorization") String token,
                                          @RequestParam UUID recruiterId,
                                          @RequestBody RecruiterEmailAndTitle recruiterEmailAndTitleAndName) {
        return managementService.updateRecruiter(token, recruiterId, recruiterEmailAndTitleAndName);
    }

    @DeleteMapping("invitation")
    public ApiResponse<?> deleteRecruiterInvitation(@RequestHeader("Authorization") String token,
                                                    @RequestParam(name = "recEmail") String recEmail) {
        return managementService.deleteRecruiterInvitation(token, recEmail);
    }

    @DeleteMapping("member")
    public ApiResponse<?> deleteRecruiter(@RequestHeader("Authorization") String token, @RequestParam(name = "recEmail") String email) {
        return managementService.deleteRecruiter(token, email);
    }

    @DeleteMapping("all")
    public ApiResponse<?> deleteAllRecruiters(@RequestHeader("Authorization") String token) {
        return managementService.deleteAllRecruiters(token);
    }



}
