package com.hpms.userservice.controller.shared;

import com.hpms.userservice.model.shared.LanguageName;
import com.hpms.userservice.service.shared.LanguageNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/APIs")
public class LanguageNameController {

    @Autowired
    private LanguageNameService languageNameService;

    @GetMapping("/languageNames")
    public List<LanguageName> getAllCurrencies() {
        return languageNameService.getAllLanguageNames();
    }

}
