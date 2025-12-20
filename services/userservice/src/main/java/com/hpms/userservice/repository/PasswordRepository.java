package com.hpms.userservice.repository;

import com.hpms.userservice.model.ForgetPasswordRequest;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordRepository extends JpaRepository<ForgetPasswordRequest, Long> {

    Optional<ForgetPasswordRequest> getForgetPasswordRequestByToken(String token);

    @Transactional
    @Modifying
    @Query("UPDATE ForgetPasswordRequest fpr SET fpr.valid = false WHERE fpr.email = :email")
    int updateTokensToInvalidByEmail(@Param("email") String email);

}
