package com.hpms.userservice.service.impl;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.ChangePasswordReqDto;
import com.hpms.userservice.dto.ForgetPasswordResponseDto;
import com.hpms.userservice.dto.ResetPasswordReqDto;
import com.hpms.userservice.model.ForgetPasswordRequest;
import com.hpms.userservice.model.User;
import com.hpms.userservice.repository.PasswordRepository;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.service.EmailService;
import com.hpms.userservice.service.PasswordService;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.hpms.userservice.utils.AppConstants.RESET_PASSWORD_CLIENT_LINK;

@Service
@Log
public class PasswordServiceImp implements PasswordService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRepository passwordRepository;
    @Autowired
    private FrequentlyUsed frequentlyUsed;

    @Autowired
    JwtTokenUtils tokenUtils;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;

    @Override
    public ApiResponse<?> forgetPasswordRequest(String email) throws MessagingException {
        Optional<User> optionalUser = userRepository.getUserByUsername(email);
        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .message("Email is not exist")
                    .build();
        }
        passwordRepository.updateTokensToInvalidByEmail(email);
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);
        ForgetPasswordRequest forgetPasswordRequest = new ForgetPasswordRequest(email, token, expiryDate);
        ForgetPasswordRequest savedForgetPass = passwordRepository.save(forgetPasswordRequest);
        // send email
        String link = RESET_PASSWORD_CLIENT_LINK.concat(token);
        log.info(link);
        emailService.sendPasswordResetEmail(email, link);
        return ApiResponse.builder()
                .ok(true)
                .message("")
                .status(HttpStatus.CREATED.value())
                .body(ForgetPasswordResponseDto.builder()
                        .email(savedForgetPass.getEmail())
                        .token(savedForgetPass.getToken())
                        .build())
                .build();
    }

    @Override
    public ApiResponse<?> resetPassword(ResetPasswordReqDto resetPasswordReqDto) {
        Optional<ForgetPasswordRequest> changeReq = passwordRepository.getForgetPasswordRequestByToken(resetPasswordReqDto.getToken());
        if (changeReq.isEmpty() ||
                changeReq.get().getExpiredDate().isBefore(LocalDateTime.now()) ||
                !resetPasswordReqDto.getPassword().equals(resetPasswordReqDto.getConfirmedPassword()) ||
                !changeReq.get().isValid()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .message("Your Request is not present or expired")
                    .build();
        }
        Optional<User> optionalUser = userRepository.getUserByUsername(changeReq.get().getEmail());
        User user = optionalUser.get();
        String newPassword = passwordEncoder.encode(resetPasswordReqDto.getPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
        changeReq.get().setValid(false);
        passwordRepository.save(changeReq.get());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Password Updated Successfully")
                .body(user.getRole().toString())
                .build();
    }

    @Override
    public ApiResponse<?> changePassword(String token, ChangePasswordReqDto changePasswordReqDto) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        User user = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            user = (User) isThereUserFromToken.getBody();
        }

        String oldPassword = changePasswordReqDto.getOldPassword();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Old Password is not correct!")
                    .build();
        }
        if (!changePasswordReqDto.getNewPassword()
                .equals(changePasswordReqDto.getConfirmedNewPassword())) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Confirmed New Password is different from New Password")
                    .build();
        }
        String newPassword = passwordEncoder.encode(changePasswordReqDto.getNewPassword());
        user.setPassword(newPassword);
        userRepository.save(user);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Password changed successfully")
                .build();
    }
}
