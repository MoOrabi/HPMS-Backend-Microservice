package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.Industry;
import com.hpms.userservice.repository.shared.IndustryRepository;
import com.hpms.userservice.service.shared.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndustryServiceImp implements IndustryService {

    @Autowired
    private IndustryRepository industryRepository;

    @Override
    public List<Industry> getAllIndustries() {
        return industryRepository.findAll();
    }

}
