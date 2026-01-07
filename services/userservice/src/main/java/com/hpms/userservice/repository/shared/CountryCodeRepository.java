package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.shared.CountryCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryCodeRepository extends JpaRepository<CountryCode, String> {
}
