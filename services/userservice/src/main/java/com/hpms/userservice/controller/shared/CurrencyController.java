package com.hpms.userservice.controller.shared;

import com.hpms.userservice.model.shared.Currency;
import com.hpms.userservice.service.shared.impl.CurrencyServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/APIs")
public class CurrencyController {

    @Autowired
    private CurrencyServiceImp currencyServiceImp;

    @GetMapping("/currency")
    public List<Currency> getAllCurrencies() {
        return currencyServiceImp.getAllCurrencies();
    }
}
