package com.hpms.userservice.repository.jobseeker;

import com.hpms.userservice.model.jobseeker.JobExperience;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Transactional
public interface JobExperienceRepository extends JpaRepository<JobExperience, UUID> {

}
