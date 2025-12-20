package com.hpms.userservice.repository.jobseeker;

import com.hpms.userservice.model.Education;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface EducationRepository extends JpaRepository<Education, UUID> {
}
