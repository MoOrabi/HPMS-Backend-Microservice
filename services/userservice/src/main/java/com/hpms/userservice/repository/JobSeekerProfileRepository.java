package com.hpms.userservice.repository;


import com.hpms.userservice.model.jobseeker.JobSeeker;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface JobSeekerProfileRepository extends JpaRepository<JobSeeker, UUID> , JpaSpecificationExecutor<JobSeeker> {
    Optional<JobSeeker> getByUsername(String username);

    Optional<JobSeeker> findById(UUID id);

    Optional<JobSeeker> getByMobileNumberCountryCodeAndMobileNumber(String prefix, String number);

    @Query("SELECT j.firstName FROM JobSeeker j WHERE j.username = :email")
    Optional<String> getFirstNameByEmail(@Param("email") String email);

    void deleteByLastName(String orabi);

//    @Query("SELECT CASE WHEN COUNT(js) > 0 THEN true ELSE false END " +
//            "FROM JobSeeker js JOIN js.savedJobs sj " +
//            "WHERE js.id = :jobSeekerId AND sj.id = :jobPostId")
//    boolean isJobPostSaved(@Param("jobSeekerId") UUID jobSeekerId, @Param("jobPostId") UUID jobPostId);

}
