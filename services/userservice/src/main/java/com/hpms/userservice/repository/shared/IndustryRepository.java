package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.shared.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, Long> {
}
