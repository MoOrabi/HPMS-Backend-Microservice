package com.hpms.userservice.controller.shared;

import com.hpms.userservice.model.shared.CountryCode;
import com.hpms.userservice.service.shared.CountryCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/APIs")
public class CountryCodeController {

    @Autowired
    private CountryCodeService countryCodeService;

    @GetMapping("/countryCodes")
    public List<CountryCode> getAllCountryCode() {
        return countryCodeService.getAllCountryCodes();
    }
}
