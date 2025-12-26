package com.hpms.applicationservice.repository;

import com.hpms.applicationservice.model.Offer;
import com.hpms.applicationservice.model.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, UUID> {
}
