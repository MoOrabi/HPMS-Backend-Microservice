package com.hpms.userservice.service;


import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.ChangePasswordReqDto;
import com.hpms.userservice.dto.ResetPasswordReqDto;
import org.springframework.messaging.MessagingException;

public interface PasswordService {

    ApiResponse<?> forgetPasswordRequest(String email) throws MessagingException;

    ApiResponse<?> resetPassword(ResetPasswordReqDto resetPasswordReqDto);

    ApiResponse<?> changePassword(String token, ChangePasswordReqDto changePasswordReqDto);
}
