package com.hpms.userservice.service;

import com.hpms.commonlib.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface PublicAuthService {

    ApiResponse<?> sendConfirmationMail(String token);

    ApiResponse<?> confirmAccount(String token);

    ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception;

    ApiResponse<?> checkEmailIsExist(String email);

    ApiResponse<?> getIdFromToken(String token);

     ApiResponse<?> checkIsProfileOwner(String userToken , String profileId);

}
