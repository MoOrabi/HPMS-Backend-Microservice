package com.hpms.userservice.repository;

import com.hpms.userservice.model.DeletionRequest;
import com.hpms.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DeletionRequestRepository extends JpaRepository<DeletionRequest, UUID> {

    @Query("select u.user from DeletionRequest u where u.isValid=true and :now - u.modificationTime >= 30")
    List<User> getDeletedUserFor30Days(LocalDate now);

}
