package com.hpms.userservice.repository.jobseeker;

import com.hpms.userservice.model.jobseeker.Language;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface LanguageRepository extends JpaRepository<Language, Long> {
    // should implement find by all attributes to make sure that added record isn't present


    Optional<Language> findByLanguageNameAndLanguageLevel(String name, String level);

    @Query(nativeQuery = true, value = "select distinct L.* from language L inner join jobseeker_languages j " +
            " on L.id = j.language_id" +
            " where L.language_name= :name " +
            " and L.language_level= :level" +
            " and j.job_seeker_id= :jobSeekerID")
    Optional<Language> findByLanguageNameAndLanguageLevelAndJobSeekerId(String name, String level, UUID jobSeekerID);

    @Query(nativeQuery = true, value = "select distinct L.* from language L inner join jobseeker_languages j" +
            " on L.id = j.language_id" +
            " where L.language_name= :name " +
            " and j.job_seeker_id= :jobSeekerID")
    Optional<Language> findByLanguageNameAndJobSeekerId(String name, UUID jobSeekerID);

    @Modifying
    @Query(nativeQuery = true, value = "delete from jobseeker_languages where job_seeker_id= :jobSeekerId " +
            " and language_id= :languageId")
    void deleteLanguageForJobSeeker(UUID jobSeekerId, Long languageId);
}
