package com.hpms.userservice.repository.shared;

import com.hpms.userservice.model.shared.JobName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobNameRepository extends JpaRepository<JobName, Long> {

    Optional<JobName> getByName(String name);

    @Query(nativeQuery = true, value = "select * FROM job_name j WHERE j.name LIKE %:name% limit 20")
    List<JobName> getByNameLike(@Param("name") String name);

    @Query(nativeQuery = true, value = "select * FROM job_name j limit :limit")
    List<JobName> findAllWithLimit(@Param("limit") int limit);

}
