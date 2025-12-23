package com.hpms.userservice.service;

import com.hpms.commonlib.handler.BadRequestException;
import com.hpms.userservice.constants.RoleEnum;
import com.hpms.userservice.dto.*;
import com.hpms.userservice.mapper.*;
import com.hpms.userservice.model.Company;
import com.hpms.userservice.model.Industry;
import com.hpms.userservice.model.Recruiter;
import com.hpms.userservice.model.User;
import com.hpms.userservice.model.shared.JobName;
import com.hpms.userservice.model.shared.Skill;
import com.hpms.userservice.repository.*;
import com.hpms.userservice.repository.shared.JobNameRepository;
import com.hpms.userservice.repository.shared.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobRelatedDataService {

    private JobSeekerProfileRepository jobSeekerProfileRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RecruiterRepository recruiterRepository;
    private final JobNameRepository jobNameRepository;
    private final IndustryRepository industryRepository;
    private final SkillRepository skillRepository;
    private final CompanyDTOMapper companyDTOMapper;
    private final JobPostCreatorDTOMapper jobPostCreatorDTOMapper;
    private final RecruiterDTOMapper recruiterDTOMapper;
    private final JobNameDTOMapper jobNameDTOMapper;
    private final SkillDTOMapper skillDTOMapper;
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
        List<Skill> skills = skillRepository.findAllById(request.getSkillIds());
        if(skills.isEmpty()) {
            throw new BadRequestException("Skills Assigned to this job post don't exist any more.");
        }
        List<SkillDTO> skillDTOS = skills
                .stream().map(skillDTOMapper).toList();
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

        boolean isJobPostSaved = false;

        if(request.getJobSeekerCallerId() != null) {
            isJobPostSaved = jobSeekerProfileRepository.isJobPostSaved(request.getJobSeekerCallerId(), request.getJobPostId());
        }

        return JobRelatedDataDTO.builder()
                .company(companyDTO)
                .creator(jobPostCreatorDTO)
                .recruiters(recruiterNameAndPhotos)
                .jobName(jobNameDTO)
                .industry(industryDTO)
                .skills(skillDTOS)
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
}
