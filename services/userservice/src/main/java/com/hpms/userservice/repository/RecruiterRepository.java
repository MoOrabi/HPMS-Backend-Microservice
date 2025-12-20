package com.hpms.userservice.repository;

import com.hpms.userservice.model.Recruiter;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecruiterRepository extends JpaRepository<Recruiter, UUID> {

    @Query("SELECT r.lastName FROM Recruiter r WHERE r.username = :email")
    Optional<String> getLastNameByEmail(@Param("email") String email);

    Optional<Recruiter> getByUsername(String username);

    @Query("select R from Recruiter R where R.company.id= :companyId")
    List<Recruiter> getRecruitersForCompany(UUID companyId);

    @Transactional
    @Modifying
    boolean deleteByUsername(String username);

    @Modifying
    @Transactional
    @Query("delete from Recruiter R where R.company.id= :companyId")
    boolean deleteAllForCompany(UUID companyId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from recruiters_team where recruiter_id = :recruiterId")
    void deleteRecruiterFromCompany(UUID recruiterId);

}