package com.hpms.userservice.service.impl;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.ConfirmationToken;
import com.hpms.userservice.model.User;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.service.PublicAuthService;
import com.hpms.userservice.service.EmailService;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.hpms.userservice.dto.SuccessLoginRes.getSuccessfulResponse;
import static com.hpms.userservice.utils.AppConstants.EMAIL_CONFORMATION_PREFIX;


@Log
@Service
@AllArgsConstructor
public class PublicAuthServiceImp implements PublicAuthService {

    private final JwtTokenUtils jwtTokenUtils;

    private final UserRepository userRepository;

    private final ConfirmationTokenServiceImp confirmationTokenServiceImp;

    private final EmailService emailService;

    private final FrequentlyUsed frequentlyUsed;

    @Override
    public ApiResponse<?> sendConfirmationMail(String email) {
        Optional<User> jobSeekerOptional = userRepository.getUserByUsername(email);
        // If the user is already Enabled, response should be different
        if (jobSeekerOptional.isPresent() && !jobSeekerOptional.get().isEnabled()) {
            try {
                User user = jobSeekerOptional.get();
                ConfirmationToken token = confirmationTokenServiceImp.generateConfirmationToken(user);
                String link = EMAIL_CONFORMATION_PREFIX + token.getToken();
                System.out.println("Activate Link " + link);
                log.info(link);
                emailService.sendVerificationEmail(user, link);
                return ApiResponse.builder()
                        .ok(true)
                        .message("Email Send Successfully")
                        .status(HttpStatus.ACCEPTED.value())
                        .build();
            } catch (MessagingException e) {
                return ApiResponse.builder()
                        .ok(false)
                        .message("Send Email Failed , try again!")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .build();
            }
        }
        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
    }

    @Override
    public ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String authHeader = request.getHeader("refresh-token");
        final String refreshToken;
        final String userEmail;
        log.info(authHeader);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Token is not valid")
                    .build();
        }
        refreshToken = authHeader.substring(7);
        if (!jwtTokenUtils.isTokenExpired(refreshToken)) {
            log.info("Value not valid");
            userEmail = jwtTokenUtils.extractUsername(refreshToken);
            if (userEmail != null) {
                var user = this.userRepository.getUserByUsername(userEmail).orElseThrow();
                String newAccessToken = jwtTokenUtils.generateAccessToken(user);
                String newRefreshToken = jwtTokenUtils.generateRefreshToken(user);
                return ApiResponse.builder()
                        .ok(true)
                        .message("Token Refreshed Successfully!")
                        .status(HttpStatus.ACCEPTED.value())
                        .body(getSuccessfulResponse(user, newAccessToken, newRefreshToken))
                        .build();
            }
        }
        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
    }

    @Override
    public ApiResponse<?> confirmAccount(String token) {
        ConfirmationToken confirmationToken = confirmationTokenServiceImp.getConfirmationToken(token);
        if (confirmationToken == null) {

            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .message("Token value is not Valid")
                    .build();
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if (confirmationToken.getConfirmedAt() != null || expiredAt.isBefore(LocalDateTime.now()) || confirmationToken.isExpired()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .message("Token is Expired")
                    .build();
        }
        int confirm = confirmationTokenServiceImp.confirmToken(token);
        if (confirm > 0) {
            User user = confirmationToken.getUser();
            user.setEnabled(true);
            user.setLocked(false);
            User savedJobSeeker = userRepository.save(user);
            String accessToken = jwtTokenUtils.generateAccessToken(savedJobSeeker);
            String refreshToken = jwtTokenUtils.generateRefreshToken(savedJobSeeker);
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.ACCEPTED.value())
                    .message("Account Confirmed Successfully")
                    .body(getSuccessfulResponse(user, accessToken, refreshToken))
                    .build();
        }
        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
    }

    @Override
    public ApiResponse<?> checkEmailIsExist(String email) {
        Optional<User> user = userRepository.getUserByUsername(email);
        if (user.isEmpty()) {
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .body(false)
                    .build();
        }
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(true)
                .build();
    }

    public ApiResponse<?> getIdFromToken(String companyToken) {

        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(companyToken);
        User user = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            user = (User) isThereUserFromToken.getBody();
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(user.getId())
                .build() ;
    }

    public ApiResponse<?> checkIsProfileOwner(String userToken , String profileId){
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(userToken);
        User user = null;
        boolean isProfileOwner = false;

        if (isThereUserFromToken.isOk()) {
            user = (User) isThereUserFromToken.getBody();
            isProfileOwner = (user.getId().equals(UUID.fromString(profileId) )) ;
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(isProfileOwner)
                .build();
    }


}
