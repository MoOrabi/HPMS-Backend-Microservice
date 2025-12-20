package com.hpms.userservice.repository.jobseeker;

import com.hpms.userservice.model.jobseeker.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

}
