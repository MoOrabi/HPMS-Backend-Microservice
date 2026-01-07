package com.hpms.recommendationservice.repository;

import com.hpms.recommendationservice.model.JobPostProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JobPostProfileRepository extends JpaRepository<JobPostProfile, UUID> {
    List<JobPostProfile> findByOpenTrueAndDeletedFalse();
}
