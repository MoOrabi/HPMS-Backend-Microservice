package com.hpms.userservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.service.impl.PublicAuthServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/user")
public class UserController {

    @Autowired
    private PublicAuthServiceImp publicAuthService;

    @GetMapping("my-id")
    public ApiResponse<?> getIdFromToken(@RequestHeader("Authorization") String token) {
        return publicAuthService.getIdFromToken(token);
    }

    @GetMapping("is-profile-owner")
    public ApiResponse<?> checkIsProfileOwner(@RequestHeader("Authorization") String userToken , @RequestParam(name = "profileId") String profileId ){
        return  publicAuthService.checkIsProfileOwner(userToken , profileId) ;
    }

}
