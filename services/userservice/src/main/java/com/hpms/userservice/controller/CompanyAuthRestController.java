package com.hpms.userservice.controller;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.CompanyAuthReq;
import com.hpms.userservice.dto.CompanyRegisterDto;
import com.hpms.userservice.dto.LoginReq;
import com.hpms.userservice.service.impl.CompanyAuthServiceImp;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/auth")
public class CompanyAuthRestController {

    private CompanyAuthServiceImp companyAuthService;

    @Autowired
    public CompanyAuthRestController(CompanyAuthServiceImp companyAuthService) {
        this.companyAuthService = companyAuthService;
    }

    @PostMapping("/company/register")
    public ApiResponse<?> register(@Valid @RequestBody CompanyRegisterDto companyRegisterDto) {
        return companyAuthService.register(companyRegisterDto);
    }

    @PostMapping("/company/login")
    public ApiResponse<?> login(@Valid @RequestBody CompanyAuthReq companyAuthReq) {
        return companyAuthService.login(companyAuthReq);
    }

    @PostMapping("/employer/login")
    public ApiResponse<?> loginCompanyAndRecruiter(@Valid @RequestBody LoginReq loginReq) {
        return companyAuthService.loginCompanyAndRecruiter(loginReq);
    }

}
