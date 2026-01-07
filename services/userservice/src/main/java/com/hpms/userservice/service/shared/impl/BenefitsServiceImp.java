package com.hpms.userservice.service.shared.impl;

import com.hpms.userservice.model.shared.Benefit;
import com.hpms.userservice.repository.shared.BenefitsRepository;
import com.hpms.userservice.service.shared.BenefitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BenefitsServiceImp implements BenefitsService {

    @Autowired
    private BenefitsRepository benefitsRepository;

    @Override
    public List<Benefit> getAllBenefits() {
        return benefitsRepository.findAll();
    }

}
