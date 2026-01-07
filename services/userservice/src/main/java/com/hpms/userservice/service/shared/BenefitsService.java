package com.hpms.userservice.service.shared;

import com.hpms.userservice.model.shared.Benefit;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BenefitsService {
    List<Benefit> getAllBenefits();

}
