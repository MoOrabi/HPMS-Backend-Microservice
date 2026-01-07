package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.LanguageName;
import com.hpms.userservice.repository.shared.LanguageNameRepository;
import com.hpms.userservice.service.shared.LanguageNameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LanguageNameServiceImp implements LanguageNameService {

    @Autowired
    private LanguageNameRepository languageNameRepository;

    public List<LanguageName> getAllLanguageNames() {
        return languageNameRepository.findAll();
    }

}
