package com.hpms.userservice.repository;

import com.hpms.userservice.model.AboutCompany;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface AboutCompanyRepository extends JpaRepository<AboutCompany, UUID> {


}
