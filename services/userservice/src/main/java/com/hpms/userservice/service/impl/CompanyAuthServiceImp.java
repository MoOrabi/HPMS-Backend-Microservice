package com.hpms.userservice.service.impl;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.config.AuthenticationProviderService;
import com.hpms.userservice.constants.AuthProviders;
import com.hpms.userservice.constants.RoleEnum;
import com.hpms.userservice.dto.CompanyAuthReq;
import com.hpms.userservice.dto.CompanyRegisterDto;
import com.hpms.userservice.dto.LoginReq;
import com.hpms.userservice.dto.SuccessLoginRes;
import com.hpms.userservice.model.*;
import com.hpms.userservice.repository.AboutCompanyRepository;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.IndustryRepository;
import com.hpms.userservice.repository.RecruiterRepository;
import com.hpms.userservice.service.CompanyAuthService;
import com.hpms.userservice.utils.JwtTokenUtils;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

import static com.hpms.userservice.dto.SuccessLoginRes.getSuccessfulResponse;
import static com.hpms.userservice.utils.AppConstants.LOCAL_PROVIDER_ID;

@AllArgsConstructor
@Log
@Service
public class CompanyAuthServiceImp implements CompanyAuthService {

    private final CompanyRepository companyRepository;

    private final RecruiterRepository recruiterRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationProviderService authenticationProviderService;

    private final JwtTokenUtils jwtTokenUtils;

    private final AboutCompanyRepository aboutCompanyRepository;

    private final IndustryRepository industryRepository;

    @Override
    public ApiResponse<?> register(CompanyRegisterDto companyRegisterDto) {
        Optional<Company> optionalCompany = companyRepository.getByUsername(companyRegisterDto.getUsername());
        if (optionalCompany.isPresent()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .message(companyRegisterDto.getUsername() + " is Exist")
                    .build();
        }
        Company company = createCompany(companyRegisterDto);
        Company savedCompany = companyRepository.save(company);
        if (savedCompany != null) {
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.CREATED.value())
                    .message("Given Company details are successfully registered")
                    .body(SuccessLoginRes.SuccessRegisterResponse(savedCompany))
                    .build();
        }
        return ApiResponse.builder()
                .ok(false)
                .message(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
    }

    private Company createCompany(CompanyRegisterDto companyRegisterDto) {
        String securePassword = passwordEncoder.encode(companyRegisterDto.getPassword());
        User user = User.builder()
                .username(companyRegisterDto.getUsername())
                .password(securePassword)
                .role(RoleEnum.ROLE_COMPANY)
                .provider(AuthProviders.local)
                .providerId(LOCAL_PROVIDER_ID)
                .build();

        Industry industry = industryRepository.getReferenceById(companyRegisterDto.getIndustry());
        Company company = new Company(user, companyRegisterDto.getName(), industry.getName(), new HashSet<>());
        company.setManagerFirstName(companyRegisterDto.getFirstName());
        company.setManagerLastName(companyRegisterDto.getLastName());
        company.setMobileNumberCountryCode(companyRegisterDto.getMobileNumberCountryCode());
        company.setMobileNumber(companyRegisterDto.getMobileNumber());
        AboutCompany aboutCompany = new AboutCompany();
        AboutCompany savedAbout = aboutCompanyRepository.save(aboutCompany);
        company.setAboutCompany(savedAbout);

        return company;
    }

    @Override
    public ApiResponse<?> login(CompanyAuthReq companyAuthRequest) {
        Optional<Company> companyOptional = companyRepository.getByUsername(companyAuthRequest.getUsername());
        if (companyOptional.isPresent()) {
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        companyAuthRequest.getUsername(), companyAuthRequest.getPassword(), companyOptional.get().getAuthorities());
                authentication = authenticationProviderService.authenticate(authentication);
                if (authentication.isAuthenticated()) {
                    String accessToken = jwtTokenUtils.generateAccessToken(companyOptional.get());
                    String refreshToken = jwtTokenUtils.generateRefreshToken(companyOptional.get());
                    return ApiResponse.builder()
                            .ok(true)
                            .message("Logged in Successfully")
                            .status(HttpStatus.ACCEPTED.value())
                            .body(getSuccessfulResponse(companyOptional.get(), accessToken, refreshToken))
                            .build();
                } else {
                    return ApiResponse.builder()
                            .ok(false)
                            .message("You are not authorized to access this account")
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .build();
                }
            } catch (Exception ex) {
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.NOT_ACCEPTABLE.value())
                        .message("Username or/and Password are Not Correct")
                        .build();
            }
        }
        return ApiResponse.builder()
                .ok(false)
                .message("Username or/and Password are Not Correct")
                .status(HttpStatus.NOT_ACCEPTABLE.value())
                .build();
    }

    public ApiResponse<?> loginCompanyAndRecruiter(LoginReq loginReq) {
        Optional<Company> companyOptional = companyRepository.getByUsername(loginReq.getUsername());
        Optional<Recruiter> recruiterOptional = recruiterRepository.getByUsername(loginReq.getUsername());
        User user;
        if(companyOptional.isPresent()){
            user = companyOptional.get();
        } else if (recruiterOptional.isPresent()) {
            user = recruiterOptional.get();
        } else {
            return ApiResponse.builder()
                    .ok(false)
                    .message("Username or/and Password are Not Correct")
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .build();
        }

        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginReq.getUsername(), loginReq.getPassword(), user.getAuthorities());
            authentication = authenticationProviderService.authenticate(authentication);
            System.out.println("Auth" + authentication);
            if (authentication.isAuthenticated()) {
                System.out.println("Is Auth" + authentication.isAuthenticated());
                String accessToken = jwtTokenUtils.generateAccessToken(user);
                System.out.println("a" + accessToken);
                String refreshToken = jwtTokenUtils.generateRefreshToken(user);
                System.out.println("r" + refreshToken);
                return ApiResponse.builder()
                        .ok(true)
                        .message("Logged in Successfully")
                        .status(HttpStatus.ACCEPTED.value())
                        .body(getSuccessfulResponse(user, accessToken, refreshToken))
                        .build();
            } else {
                System.out.println("First Failure");
                return ApiResponse.builder()
                        .ok(false)
                        .message("You are not authorized to access this account")
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build();
            }
        } catch (Exception ex) {
            System.out.println("Second Failure");
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.NOT_ACCEPTABLE.value())
                    .message("Username or/and Password are Not Correct")
                    .build();
        }
    }

}
