package com.hpms.userservice.repository.jobseeker;

import com.hpms.userservice.model.jobseeker.JobSeekerLocation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface JobSeekerLocationRepository extends JpaRepository<JobSeekerLocation, Long> {

    Optional<JobSeekerLocation> findByCountryAndStateAndCity(String country, String state, String city);
}
