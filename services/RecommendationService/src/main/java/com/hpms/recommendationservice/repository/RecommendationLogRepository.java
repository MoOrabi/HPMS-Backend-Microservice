package com.hpms.recommendationservice.repository;

import com.hpms.recommendationservice.model.RecommendationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, UUID> {

    // Count matches for a job seeker
    @Query("SELECT COUNT(r) FROM RecommendationLog r WHERE r.jobSeekerId = :seekerId")
    long countBySeekerId(@Param("seekerId") UUID seekerId);

    // Count matches for a job post
    @Query("SELECT COUNT(r) FROM RecommendationLog r WHERE r.jobPostId = :jobPostId")
    long countByJobPostId(@Param("jobPostId") UUID jobPostId);

    // Find low-score matches for a job seeker beyond limit
    @Query("SELECT r FROM RecommendationLog r WHERE r.jobSeekerId = :seekerId ORDER BY r.score DESC")
    List<RecommendationLog> findBySeekerIdOrderByScoreDesc(@Param("seekerId") UUID seekerId);

    // Find low-score matches for a job post beyond limit
    @Query("SELECT r FROM RecommendationLog r WHERE r.jobPostId = :jobPostId ORDER BY r.score DESC")
    List<RecommendationLog> findByJobPostIdOrderByScoreDesc(@Param("jobPostId") UUID jobPostId);

    @Query("SELECT r FROM RecommendationLog r WHERE r.jobPostId = :jobPostId ORDER BY r.score ASC")
    Page<RecommendationLog> findByJobPostIdOrderByScoreAsc(@Param("jobPostId") UUID jobPostId,
                                                           Pageable pageable);

    @Query("SELECT r FROM RecommendationLog r WHERE r.jobSeekerId = :seekerId ORDER BY r.score ASC")
    Page<RecommendationLog> findBySeekerIdOrderByScoreAsc(@Param("seekerId") UUID seekerId,
                                                          Pageable pageable);


    // Delete all recommendations for a job seeker
    @Modifying
    @Query("DELETE FROM RecommendationLog r WHERE r.jobSeekerId = :seekerId")
    void deleteBySeekerIdCascade(@Param("seekerId") UUID seekerId);

    // Delete all recommendations for a job post
    @Modifying
    @Query("DELETE FROM RecommendationLog r WHERE r.jobPostId = :jobPostId")
    void deleteByJobPostIdCascade(@Param("jobPostId") UUID jobPostId);

    // Get all recommendations for a job seeker
    List<RecommendationLog> findByJobSeekerId(UUID jobSeekerId);

    // Get all recommendations for a job post
    List<RecommendationLog> findByJobPostId(UUID jobPostId);
}
