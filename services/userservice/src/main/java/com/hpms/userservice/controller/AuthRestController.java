package com.hpms.userservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.service.impl.PublicAuthServiceImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Log
@RestController
@RequestMapping("/api/users/auth")
public class AuthRestController {

    private final PublicAuthServiceImp publicAuthService;

    @Autowired
    public AuthRestController(PublicAuthServiceImp publicAuthService) {
        this.publicAuthService = publicAuthService;
    }

    @GetMapping("/send-confirm-email")
    public ApiResponse<?> sendConfirmationMail(@Valid @RequestParam("email") String email) {
        return publicAuthService.sendConfirmationMail(email);
    }

    @GetMapping("/confirm-email")
    public ApiResponse<?> confirmAccount(@Valid @RequestParam("token") String token) {
        return publicAuthService.confirmAccount(token);
    }

    @PostMapping("/refresh-token")
    public ApiResponse<?> refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return publicAuthService.refreshToken(request, response);
    }

    @GetMapping("/is-email-exist")
    public ApiResponse<?> isEmailExist(@Valid @RequestParam("email") String email) {
        return publicAuthService.checkEmailIsExist(email);
    }

}
