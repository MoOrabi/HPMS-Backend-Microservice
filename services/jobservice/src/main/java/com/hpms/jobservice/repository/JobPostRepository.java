package com.hpms.jobservice.repository;

import com.hpms.jobservice.model.JobPost;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query(value = "SELECT jp.* FROM job_post jp " +
            "INNER JOIN jobseeker_saved_jobs jss ON jp.id = jss.job_post_id " +
            "WHERE jss.job_seeker_id = :jobSeekerId",
            countQuery = "SELECT COUNT(*) FROM job_post jp " +
                    "INNER JOIN jobseeker_saved_jobs jss ON jp.id = jss.job_post_id " +
                    "WHERE jss.job_seeker_id = :jobSeekerId",
            nativeQuery = true)
    Page<JobPost> findSavedJobsByJobSeekerId(UUID jobSeekerId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(js) > 0 THEN true ELSE false END " +
            "FROM JobPost jp JOIN jp.jobseekerSavers js " +
            "WHERE jp.id = :jobPostId AND js = :jobSeekerId")
    boolean isJobPostSaved(@Param("jobSeekerId") UUID jobSeekerId,
                           @Param("jobPostId") UUID jobPostId);

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

    @Modifying
    @Transactional
    void deleteByCompanyIdOrCreatorId(UUID companyId, UUID creatorId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
    value = "DELETE FROM job_recruiters rt WHERE rt.recruiter_id = :recruiterId")
    void deleteRecruiterFromRecruiterIdsById(UUID recruiterId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE FROM jobseeker_saved_jobs rt WHERE rt.job_seeker_id = :jobSeekerId")
    void deleteSaverFromJobseekerSaversById(UUID jobSeekerId);
}
