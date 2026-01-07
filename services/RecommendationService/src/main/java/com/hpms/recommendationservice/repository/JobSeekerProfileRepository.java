package com.hpms.recommendationservice.repository;

import com.hpms.recommendationservice.model.JobSeekerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobSeekerProfileRepository extends JpaRepository<JobSeekerProfile, UUID> {
}
