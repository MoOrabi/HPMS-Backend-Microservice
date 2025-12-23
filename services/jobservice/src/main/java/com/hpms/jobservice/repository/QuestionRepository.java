package com.hpms.jobservice.repository;

import com.hpms.jobservice.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Modifying
    @Query("DELETE FROM Question q WHERE q.jobPost.id = :jobPostId")
    void deleteAllByJobPostId(@Param("jobPostId") UUID jobPostId);
    @Query("SELECT q FROM Question q WHERE q.jobPost.id = :jobPostId ")
    List<Question> findByJobPostId(@Param("jobPostId") UUID jobPostId);
}
