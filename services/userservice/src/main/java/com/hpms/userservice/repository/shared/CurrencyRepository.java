package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.shared.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends JpaRepository<Currency, String> {
}
