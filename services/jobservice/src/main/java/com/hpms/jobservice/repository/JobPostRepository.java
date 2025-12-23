package com.hpms.jobservice.repository;

import com.hpms.jobservice.model.JobPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JobPostRepository extends JpaRepository<JobPost, UUID> , JpaSpecificationExecutor<JobPost> {

    Optional<JobPost> getJobPostById(UUID id);

    @Query("SELECT jp FROM  JobPost jp WHERE jp.id = :id AND (jp.creatorId =:recId or jp.companyId = :recId " +
            "or :recId MEMBER OF jp.recruiterIds)")
    Optional<JobPost> getJobPostByIdAndCreatorIdOrCompanyIdOrTeamMember(@Param("id")UUID id , @Param("recId") UUID recId);

    @Query("SELECT jp FROM JobPost jp WHERE jp.id = :id")
    Optional<JobPost> getJobPostByIdForAnyUser(@Param("id")UUID id);

    //   SELECT COMPANY POSTS ORDERED BY OPEN AT THE FIRST AND THEN BY UPDATE DATE
    @Query("SELECT jp FROM JobPost jp WHERE jp.companyId = :id OR jp.creatorId = :id OR :id MEMBER OF jp.recruiterIds")
    Page<JobPost> getAllCompanyJobPosts(@Param("id") UUID id, Pageable pageable);

//    @Query("SELECT jp FROM JobSeeker js JOIN js.savedJobs jp WHERE js.id = :jobSeekerId")
//    Page<JobPost> findSavedJobsByJobSeekerId(UUID jobSeekerId, Pageable pageable);

    @Query("SELECT COUNT(*) FROM JobPost jp WHERE (jp.companyId = :companyId or jp.creatorId = :companyId) and jp.open = true")
    int countActiveJobsForCompany(@Param("companyId") UUID companyId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM job_recruiters rt right join job_post jp on jp.id = rt.job_id where " +
            "(rt.recruiter_id = :recruiterId or jp.creator_id = :recruiterId) and jp.open = true ")
    int countActiveJobsForRecruiter(@Param("recruiterId") UUID recruiterId);

    @Query("SELECT jb.jobTitle FROM JobPost jb WHERE jb.id = :id")
    String getJobTitleByJobPostId(@Param("id") UUID id);

    Page<JobPost> getByCompanyId(UUID companyId , Pageable pageable);
    // for delete
    @Query("SELECT jp FROM JobPost jp WHERE jp.id = :postId AND (jp.companyId =:companyIdOrAdminId OR :companyIdOrAdminId MEMBER OF jp.recruiterIds) AND NOT jp.deleted")
    Optional<JobPost> getJobPostByIdAndCompanyIdOrAdminIdNotDeleted(@Param("postId")UUID postId,@Param("companyIdOrAdminId") UUID companyIdOrAdminId);

    // for close
    @Query("SELECT jp FROM JobPost jp WHERE jp.id = :postId AND (jp.companyId =:companyIdOrAdminId OR :companyIdOrAdminId MEMBER OF jp.recruiterIds) AND jp.open")
    Optional<JobPost> getJobPostByIdAndCompanyIdOrAdminIdAndOpen(@Param("postId")UUID postId,@Param("companyIdOrAdminId") UUID companyIdOrAdminId);

}
