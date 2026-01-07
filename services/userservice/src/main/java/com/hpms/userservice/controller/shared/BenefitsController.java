package com.hpms.userservice.controller.shared;

import com.hpms.userservice.model.shared.Benefit;
import com.hpms.userservice.service.shared.BenefitsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users/api/benefits")
public class BenefitsController {

    @Autowired
    private BenefitsService benefitsService;

    @GetMapping
    public List<Benefit> getAllBenefits() {
        return benefitsService.getAllBenefits();
    }

}
