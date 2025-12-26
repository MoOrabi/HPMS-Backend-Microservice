package com.hpms.applicationservice.repository;

import com.hpms.applicationservice.model.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, UUID> {

    List<TimelineEvent> findByApplicationIdOrderByCreatedAtDesc(UUID appId);
}
