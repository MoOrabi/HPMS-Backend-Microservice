package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.Currency;
import com.hpms.userservice.repository.shared.CurrencyRepository;
import com.hpms.userservice.service.shared.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyServiceImp implements CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
}
