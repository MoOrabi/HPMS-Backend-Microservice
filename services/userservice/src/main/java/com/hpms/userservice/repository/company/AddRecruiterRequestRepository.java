package com.hpms.userservice.repository.company;

import com.hpms.userservice.model.company.AddRecruiterRequest;
import com.hpms.userservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddRecruiterRequestRepository extends JpaRepository<AddRecruiterRequest, Long> {

    Optional<AddRecruiterRequest> getByToken(String token);

    Optional<AddRecruiterRequest> getByCompanyAndRecruiterEmail(Company company, String recruiterEmail);

    List<AddRecruiterRequest> getByCompany(Company company);

    @Query(nativeQuery = true, value = "SELECT * FROM add_recruiter_request where valid = true and expiration_date > now();")
    List<AddRecruiterRequest> getByCompanyIfValidAndNotExpired(Company company);


}
