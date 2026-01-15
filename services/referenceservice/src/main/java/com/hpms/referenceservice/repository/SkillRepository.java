package com.hpms.referenceservice.repository;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.referenceservice.model.Skill;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public interface SkillRepository extends JpaRepository<Skill, Long> {
    // should implement find by all attributes to make sure that added record isn't present

    @Query(nativeQuery = true, value = "select * FROM skill s WHERE s.name LIKE %:name% limit 20")
    List<Skill> getByNameLike(@Param("name") String name);

    @Query(nativeQuery = true, value = "select * FROM skill j limit :limit")
    List<Skill> findAllWithLimit(@Param("limit") int limit);

    @Query(value = "select NEW com.hpms.commonlib.dto.SelectOption(s.id, s.name) from Skill s where s.id in :skillIds")
    Set<SelectOption> findAllNamesByIds(Set<Long> skillIds);
}
