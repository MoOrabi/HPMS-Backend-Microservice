package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.shared.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitsRepository extends JpaRepository<Benefit, Long> {
}
