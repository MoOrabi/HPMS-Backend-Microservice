package com.hpms.userservice.service.impl;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.util.FileUploadUtil;
import com.hpms.userservice.config.AuthenticationProviderService;
import com.hpms.userservice.dto.RecruiterLoginRequest;
import com.hpms.userservice.dto.RecruiterSignUpRequest;
import com.hpms.userservice.dto.company.RecruiterInfo;
import com.hpms.userservice.mapper.RecruiterInfoMapper;
import com.hpms.userservice.model.company.AddRecruiterRequest;
import com.hpms.userservice.model.Company;
import com.hpms.userservice.model.Recruiter;
import com.hpms.userservice.repository.company.AddRecruiterRequestRepository;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.RecruiterRepository;
import com.hpms.userservice.service.RecruiterAuthService;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;

import static com.hpms.userservice.dto.SuccessLoginRes.getSuccessfulResponse;

@Service
public class RecruiterAuthServiceImp implements RecruiterAuthService {

    @Autowired
    private RecruiterRepository recruiterRepository;
    @Autowired
    private AddRecruiterRequestRepository addRecruiterRequestRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private AuthenticationProviderService authenticationProviderService;
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RecruiterInfoMapper recruiterInfoMapper;


    public ApiResponse<?> addFile(Recruiter recruiter, MultipartFile file, String place,
                                  int sizeInKiloBytes, String directory,
                                  String successMessage,
                                  HashSet<String> allowedExtensions) throws Exception {
        String profileFileName = StringUtils.cleanPath(Objects
                .requireNonNull(file.getOriginalFilename()));

        if (file.getContentType() == null || !allowedExtensions.contains(file.getContentType())) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(file.getOriginalFilename() + " has no valid extension")
                    .build();
        }

        if (file.getSize() > sizeInKiloBytes * 1024L) {
            return ApiResponse.builder().ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(file.getOriginalFilename() + " size is above allowed size(2MB).")
                    .build();
        }


        String uploadDir = directory + recruiter.getId();

        String pathWithFileName = FileUploadUtil
                .saveFile(uploadDir, profileFileName, file);
        switch (place) {
            case "profile" -> recruiter.setProfilePhoto(pathWithFileName);
//            case "cover" -> recruiter.setCoverPhoto(pathWithFileName);
            default -> throw new Exception("Non supported place");
        }

        recruiterRepository.save(recruiter);

        FileUploadUtil.saveFile(uploadDir, profileFileName, file);

        return ApiResponse.builder().ok(true)
                .body(pathWithFileName)
                .status(HttpStatus.OK.value())
                .message(successMessage).build();
    }

    private ApiResponse<?> saveProfilePhotoWithRegister(Recruiter recruiter, MultipartFile profileFile) throws Exception {
        return addFile(recruiter, profileFile, "profile", 2 * 1048,
                "recruiter-files/profile/",
                "Photo Uploaded Successfully", FrequentlyUsed.imageExtensions);
    }

    @Override
    public ApiResponse<?> registerRecruiter(RecruiterSignUpRequest request, MultipartFile profilePhoto) throws Exception {
        Optional<AddRecruiterRequest> optionalInvitationRequest = addRecruiterRequestRepository
                .getByToken(String.valueOf(request.getId()));
        if (optionalInvitationRequest.isEmpty()) {
            return ApiResponse.builder()
                    .message("You have no invitation to join our website")
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .build();
        }
        AddRecruiterRequest invitationRequest = optionalInvitationRequest.get();

        Optional<Company> optionalCompany = companyRepository.getByUsername(invitationRequest.getCompany().getUsername());
        if (optionalCompany.isEmpty()) {
            return ApiResponse.builder()
                    .message("Company invited you is no longer exist in our website")
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .build();
        }

        if (recruiterRepository.getByUsername(invitationRequest.getRecruiterEmail()).isPresent()) {
            return ApiResponse.builder()
                    .message("The entered email is already a recruiter in our website")
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .build();
        }

        if (!invitationRequest.isValid()) {
            return ApiResponse.builder()
                    .message("Your invitation token is not valid anymore")
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .build();
        }

        if (invitationRequest.getExpirationDate().isBefore(LocalDateTime.now())) {
            return ApiResponse.builder()
                    .message("Your invitation token is expired, ask your manager to renew it")
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .build();
        }

        request.setCompany(optionalCompany.get());
        request.setJobTitle(invitationRequest.getJobTitle());

        Recruiter recruiter = new Recruiter(request.getFirstName(), request.getLastName(), optionalCompany.get(),
                request.getPhoneNumberCountryCode(), request.getPhoneNumber(),
                invitationRequest.getJobTitle(), LocalDateTime.now(), invitationRequest.getRecruiterEmail(),
                passwordEncoder.encode(request.getPassword()));
        recruiterRepository.save(recruiter);
        invitationRequest.setValid(false);
        invitationRequest.setUsedAt(LocalDateTime.now());

        ApiResponse<?> saveResponse = saveProfilePhotoWithRegister(recruiter, profilePhoto);

        RecruiterInfo recruiterInfo = recruiterInfoMapper.recruiterToInfo(recruiter);

        return ApiResponse.builder()
                .message("Recruiter Successfully registered")
                .ok(true)
                .status(HttpStatus.CREATED.value())
                .body(recruiterInfo)
                .build();
    }

    @Override
    public ApiResponse<?> loginRecruiter(RecruiterLoginRequest loginRequest) {
        Optional<Recruiter> optionalRecruiter = recruiterRepository.getByUsername(loginRequest.getUsername());
        if (optionalRecruiter.isPresent()) {
            try {
                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword(), optionalRecruiter.get().getAuthorities());
                authentication = authenticationProviderService.authenticate(authentication);
                if (authentication.isAuthenticated()) {
                    String accessToken = jwtTokenUtils.generateAccessToken(optionalRecruiter.get());
                    String refreshToken = jwtTokenUtils.generateRefreshToken(optionalRecruiter.get());
                    return ApiResponse.builder()
                            .ok(true)
                            .message("Logged in Successfully")
                            .status(HttpStatus.ACCEPTED.value())
                            .body(getSuccessfulResponse(optionalRecruiter.get(), accessToken, refreshToken))
                            .build();
                } else {
                    return ApiResponse.builder()
                            .ok(false)
                            .message("Sorry, there is an internal error, we working on it.")
                            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
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
}
