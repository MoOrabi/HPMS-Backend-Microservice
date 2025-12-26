package com.hpms.applicationservice.repository;

import com.hpms.applicationservice.constants.ApplicationStatus;
import com.hpms.applicationservice.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    boolean existsByJobPostIdAndJobSeekerId(UUID jobPostId , UUID jobSeekerId);

    @Query("SELECT jb FROM JobApplication jb WHERE jb.id = :id AND jb.jobSeekerId = :jobSeekerId")
    Optional<JobApplication>  getByIdAndJobSeekerId(UUID id, UUID jobSeekerId);

    @Query("SELECT COUNT(*) FROM JobApplication jb WHERE jb.jobPostId = :jobId and jb.status = :status")
    int countApplicationsByStatus(UUID jobId, ApplicationStatus status);

    @Query("SELECT COUNT(*) FROM JobApplication jb WHERE jb.jobPostId = :jobId")
    int countAllApplications(UUID jobId);

    List<JobApplication> findByJobPostId(UUID jobId);

    List<JobApplication> findByJobSeekerId(UUID jobId);
}
