package com.hpms.userservice.service;

import com.hpms.commonlib.constants.RoleEnum;
import com.hpms.commonlib.handler.BadRequestException;
import com.hpms.userservice.dto.*;
import com.hpms.userservice.mapper.*;
import com.hpms.userservice.model.Company;
import com.hpms.userservice.model.Recruiter;
import com.hpms.userservice.model.User;
import com.hpms.userservice.model.shared.Industry;
import com.hpms.userservice.model.shared.JobName;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.RecruiterRepository;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.repository.shared.IndustryRepository;
import com.hpms.userservice.repository.shared.JobNameRepository;
import com.hpms.userservice.service.client.ReferenceServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobRelatedDataService {

    private final ReferenceServiceClient referenceServiceClient;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobNameRepository jobNameRepository;
    private final IndustryRepository industryRepository;
    private final CompanyDTOMapper companyDTOMapper;
    private final JobPostCreatorDTOMapper jobPostCreatorDTOMapper;
    private final RecruiterDTOMapper recruiterDTOMapper;
    private final JobNameDTOMapper jobNameDTOMapper;
    private final IndustryDTOMapper industryDTOMapper;

    public JobRelatedDataDTO getJobRelatedData(@RequestBody JobRelatedDataRequest request) {

        Optional<Company> optionalCompany = companyRepository.findById(request.getCompanyId());
        if(optionalCompany.isEmpty()) {
            throw new BadRequestException("Company with id " + request.getCompanyId() + " not found");
        }
        CompanyDTO companyDTO = companyDTOMapper.apply(optionalCompany.get());
        Optional<User> optionalUser = userRepository.findById(request.getCreatorId());
        if(optionalUser.isEmpty()) {
            throw new BadRequestException("User with id " + request.getCreatorId() + " not found");
        }

        JobPostCreatorDTO jobPostCreatorDTO = jobPostCreatorDTOMapper.apply(optionalUser.get());
        if(jobPostCreatorDTO == null) {
            throw new BadRequestException("Recruiter Creator doesn't exist any more.");
        }

        List<Recruiter> recruiters = recruiterRepository.findAllById(request.getRecruiterIds());
//        if(recruiters.isEmpty()) {
//            throw new BadRequestException("Recruiters Assigned to this job post don't exist any more.");
//        }
        List<RecruiterNameAndPhoto> recruiterNameAndPhotos = recruiters
                .stream().map(recruiterDTOMapper).toList();
        Optional<JobName> optionalJobName = jobNameRepository.findById(request.getJobNameId());
        if(optionalJobName.isEmpty()) {
            throw new BadRequestException("Job Name with id " + request.getJobNameId() + " not found");
        }
        JobNameDTO jobNameDTO = jobNameDTOMapper.apply(optionalJobName.get());
        Optional<Industry> optionalIndustry = industryRepository.findById(request.getIndustryId());
        if(optionalIndustry.isEmpty()) {
            throw new BadRequestException("Industry with id " + request.getJobNameId() + " not found");
        }
        IndustryDTO industryDTO = industryDTOMapper.apply(optionalIndustry.get());

        Boolean isJobPostSaved = null;

        return JobRelatedDataDTO.builder()
                .company(companyDTO)
                .creator(jobPostCreatorDTO)
                .recruiters(recruiterNameAndPhotos)
                .jobName(jobNameDTO)
                .industry(industryDTO)
                .isJobSavedForJS(isJobPostSaved)
                .build();
    }


    public CompanyAndRecruiterIds getCompanyAndRecruiterIds(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            throw new BadRequestException("No User exists with this id.");
        }
        User user = optionalUser.get();
        if(user.getRole().equals(RoleEnum.ROLE_RECRUITER)) {
            Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(userId);
            if(optionalRecruiter.isEmpty()) {
                throw new BadRequestException("No Recruiter exists with this id.");
            }
            Recruiter recruiter = optionalRecruiter.get();
            return CompanyAndRecruiterIds.builder()
                            .CompanyId(recruiter.getCompany().getId())
                            .recuiterId(recruiter.getId())
                            .build();
        } else if (user.getRole().equals(RoleEnum.ROLE_COMPANY)) {
            Optional<Company> optionalCompany = companyRepository.findById(userId);
            if(optionalCompany.isEmpty()) {
                throw new BadRequestException("No Company with this id!");
            }
            Company company = optionalCompany.get();
            return CompanyAndRecruiterIds.builder()
                            .CompanyId(company.getId())
                            .recuiterId(company.getId())
                            .build();
        }
        throw new BadRequestException("No Recruiter Or Company exists with this id.");
    }

    public List<CompanyLocationAndLogoDTO> getCompanyLocationAndLogos(List<UUID> companyIds) {
        List<CompanyLocationAndLogoDTO> dtos = new ArrayList<>();
        companyIds
            .forEach(companyId -> {
                Optional<Company> optionalCompany = companyRepository.findById(companyId);
                optionalCompany.ifPresent(company -> dtos.add(CompanyLocationAndLogoDTO.builder()
                                .id(company.getId())
                                .logo(company.getLogo())
                                .location(company.getMainBranchLocation()!=null?company.getMainBranchLocation().getCity() + " " + company.getMainBranchLocation().getCountry():null)
                        .build()));
            });
        return dtos;
    }

    public CompanyNameLogoAndLocation getCompanyNameLogoAndLocation(UUID companyId) {
        Optional<Company> optionalCompany = companyRepository.findById(companyId);
        if(optionalCompany.isEmpty()) {
            return CompanyNameLogoAndLocation.builder().build();
        }
        Company company = optionalCompany.get();
        return CompanyNameLogoAndLocation.builder()
                .companyName(company.getName())
                .logo(company.getLogo())
                .city(company.getMainBranchLocation()!=null?company.getMainBranchLocation().getCity():null)
                .country(company.getMainBranchLocation()!=null?company.getMainBranchLocation().getCountry():null)
                .build();
    }

}
