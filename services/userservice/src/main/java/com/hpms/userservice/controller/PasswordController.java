package com.hpms.userservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.ChangePasswordReqDto;
import com.hpms.userservice.dto.ResetPasswordReqDto;
import com.hpms.userservice.service.PasswordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.*;

@RestController("/api/users")
public class PasswordController {

    @Autowired
    private PasswordService passwordService;

    @PostMapping("/auth/forget-password-request")
    public ApiResponse<?> forgetPasswordRequest(@Valid @RequestParam String email) throws MessagingException {
        return passwordService.forgetPasswordRequest(email);
    }

    @PostMapping("/auth/reset-password")
    public ApiResponse<?> resetPasswordReq(@Valid @RequestBody ResetPasswordReqDto resetPasswordReqDto) {
        return passwordService.resetPassword(resetPasswordReqDto);
    }

    @PostMapping("/change-password")
    public ApiResponse<?> changePassword(@Valid @RequestHeader(name = "Authorization") String token, @Valid @RequestBody ChangePasswordReqDto changePasswordReqDto) {
        return passwordService.changePassword(token, changePasswordReqDto);
    }

}