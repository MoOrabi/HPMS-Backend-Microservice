package com.hpms.userservice.service;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.CompanyAuthReq;
import com.hpms.userservice.dto.CompanyRegisterDto;
import com.hpms.userservice.dto.LoginReq;

public interface CompanyAuthService {

    ApiResponse<?> register(CompanyRegisterDto companyRegisterDto);

    ApiResponse<?> login(CompanyAuthReq companyAuthRequest);

    ApiResponse<?> loginCompanyAndRecruiter(LoginReq loginReq);

}
