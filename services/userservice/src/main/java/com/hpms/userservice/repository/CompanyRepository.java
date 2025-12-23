package com.hpms.userservice.repository;

import com.hpms.userservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> , JpaSpecificationExecutor<Company> {

    @Query("SELECT c.name FROM Company c WHERE c.username = :email")
    Optional<String> getByEmail(@Param("email") String email);

    Optional<Company> getByUsername(String username);

    @Query("SELECT c FROM Company c WHERE c.mobileNumberCountryCode = :mobileNumberCountryCode AND c.mobileNumber = :mobileNumber")
    Optional<Company> getByMobileNumberCountryCodeAndMobileNumber(String mobileNumberCountryCode, String mobileNumber);

    Company getCompanyByUsername(String companyUsername);

    @Query(value = "SELECT \n" +
            "    (SELECT COUNT(r.id)\n" +
            "     FROM recruiter r\n" +
            "     JOIN company c ON r.company_id = c.id\n" +
            "     WHERE c.id = :companyId)  ,\n" +
            "    \n" +
            "     (SELECT COUNT(req.id)\n" +
            "     FROM add_recruiter_request req \n" +
            "     JOIN company c ON req.company_id = c.id\n" +
            "     WHERE c.id = :companyId and valid = 1 and expiration_date > now())   ;"
            , nativeQuery = true)
    List<List<Integer>> getCompanyStatisticsById(UUID companyId);

    @Query(nativeQuery = true, value = "SELECT COUNT(r.id) FROM recruiter r JOIN company c ON r.company_id = c.id " +
            "WHERE c.id = :companyId")
    int getNumberOfRecruitersForCompany(UUID companyId);

}
