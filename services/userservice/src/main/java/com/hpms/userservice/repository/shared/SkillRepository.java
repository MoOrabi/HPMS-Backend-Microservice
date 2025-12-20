package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.shared.Skill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface SkillRepository extends JpaRepository<Skill, Long> {
    // should implement find by all attributes to make sure that added record isn't present

    @Query(nativeQuery = true, value = "select * FROM skill s WHERE s.name LIKE %:name% limit 20")
    List<Skill> getByNameLike(@Param("name") String name);

    @Query(nativeQuery = true, value = "select * FROM skill j limit :limit")
    List<Skill> findAllWithLimit(@Param("limit") int limit);
}
