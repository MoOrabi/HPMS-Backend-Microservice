package com.hpms.userservice.controller.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.jobseeker.Language;
import com.hpms.userservice.service.jobseeker.LanguageService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Log
@RestController
@RequestMapping("/api/users/jobseeker-language")
public class LanguageController {

    @Autowired
    private LanguageService languageService;

    @PostMapping
    public ApiResponse<?> saveLanguage(@RequestHeader(name = "Authorization") String token,
                                       @RequestBody Language language) {
        return languageService.addLanguage(token, language);
    }

    @DeleteMapping
    public ApiResponse<?> deleteLanguage(@RequestHeader(name = "Authorization") String token,
                                         @RequestBody Language language) {
        return languageService.deleteLanguage(token, language);
    }

    @GetMapping
    public ApiResponse<?> getLanguages(@RequestHeader(name = "Authorization") String token) {
        return languageService.getLanguages(token);
    }
}
