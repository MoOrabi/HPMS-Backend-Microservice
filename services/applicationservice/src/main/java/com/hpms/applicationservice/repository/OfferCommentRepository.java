package com.hpms.applicationservice.repository;

import com.hpms.applicationservice.model.OfferComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface OfferCommentRepository extends JpaRepository<OfferComment, UUID> {

    Set<OfferComment> findByOfferId(UUID offerId);
}
