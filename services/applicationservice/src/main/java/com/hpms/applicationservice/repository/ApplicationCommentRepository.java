package com.hpms.applicationservice.repository;

import com.hpms.applicationservice.model.ApplicationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ApplicationCommentRepository extends JpaRepository<ApplicationComment, UUID> {
}
