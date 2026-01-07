package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.CountryCode;
import com.hpms.userservice.repository.shared.CountryCodeRepository;
import com.hpms.userservice.service.shared.CountryCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryCodeServiceImp implements CountryCodeService {

    @Autowired
    private CountryCodeRepository countryCodeRepository;

    public List<CountryCode> getAllCountryCodes() {
        return countryCodeRepository.findAll();
    }


}
