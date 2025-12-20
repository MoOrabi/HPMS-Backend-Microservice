package com.hpms.userservice.controller;


import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.RecruiterLoginRequest;
import com.hpms.userservice.dto.RecruiterSignUpRequest;
import com.hpms.userservice.service.impl.RecruiterAuthServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/auth/recruiter")
public class RecruiterAuthController {

    @Autowired
    private RecruiterAuthServiceImp recruiterAuthService;

    @PostMapping(value = "/register",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> registerRecruiter(@RequestPart(name = "photo") MultipartFile profilePhoto,
                                            @RequestPart(name = "invitationId") String id,
                                            @RequestPart(name = "firstName") String firstName,
                                            @RequestPart(name = "lastName") String lastName,
                                            @RequestPart(name = "countryCode") String countryCode,
                                            @RequestPart(name = "phoneNumber") String phoneNumber,
                                            @RequestPart(name = "password") String password) throws Exception {
        RecruiterSignUpRequest request = new RecruiterSignUpRequest();
        request.setId(id);
        System.out.println(id);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setPhoneNumberCountryCode(countryCode);
        request.setPhoneNumber(phoneNumber);
        request.setPassword(password);

        return recruiterAuthService.registerRecruiter(request, profilePhoto);
    }

    @PostMapping(value = "/test",
            consumes = {
                    MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ApiResponse<?> registerRec(@RequestPart(name = "photo") MultipartFile profilePhoto,
                                      @RequestPart(name = "text") String text) {
        RecruiterSignUpRequest request = new RecruiterSignUpRequest();
        request.setId(text);
        System.out.println(text);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Hi")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<?> loginRecruiter(@RequestBody RecruiterLoginRequest request) {
        return recruiterAuthService.loginRecruiter(request);
    }
}
