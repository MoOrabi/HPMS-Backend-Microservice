package com.hpms.userservice.controller.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.jobseeker.Certificate;
import com.hpms.userservice.service.jobseeker.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/jobseeker-certificate")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @PostMapping
    public ApiResponse<?> addCertificate(@RequestHeader(name = "Authorization") String token,
                                         @RequestBody Certificate certificate) {
        return certificateService.addCertificate(token, certificate);
    }

    @PutMapping
    public ApiResponse<?> editCertificate(@RequestHeader(name = "Authorization") String token,
                                          @RequestBody Certificate certificate,
                                          @RequestParam("certId") String id) {
        return certificateService.editCertificate(token, certificate, id);
    }

    @DeleteMapping
    public ApiResponse<?> deleteCertificate(@RequestHeader(name = "Authorization") String token,
                                            @RequestParam("certId") String id) {
        return certificateService.deleteCertificate(token, id);
    }

    @GetMapping
    public ApiResponse<?> getCertifications(@RequestHeader(name = "Authorization") String token) {
        return certificateService.getCertificates(token);
    }
}
