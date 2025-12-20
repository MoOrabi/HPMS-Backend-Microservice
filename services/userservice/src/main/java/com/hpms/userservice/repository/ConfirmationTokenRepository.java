package com.hpms.userservice.repository;

import com.hpms.userservice.model.ConfirmationToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Long> {

    Optional<ConfirmationToken> getByToken(String token);

    @Modifying
    @Query("UPDATE ConfirmationToken c SET c.confirmedAt = :confirmedAt WHERE c.token = :token")
    int confirmToken(@Param("token") String token, @Param("confirmedAt") LocalDateTime confirmedAt);

    @Modifying
    @Query("UPDATE ConfirmationToken c SET c.expired =true WHERE c.user.id = :userId")
    void expireAllToken(@Param("userId") UUID id);
}
