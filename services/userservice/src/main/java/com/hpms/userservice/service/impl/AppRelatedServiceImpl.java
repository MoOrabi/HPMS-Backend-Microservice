package com.hpms.userservice.service.impl;

import com.hpms.commonlib.constants.RoleEnum;
import com.hpms.commonlib.dto.SelectOption;
import com.hpms.userservice.dto.app.*;
import com.hpms.userservice.model.Company;
import com.hpms.userservice.model.Recruiter;
import com.hpms.userservice.model.User;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.jobseeker.JobSeekerLocation;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.RecruiterRepository;
import com.hpms.userservice.repository.UserRepository;
import com.hpms.userservice.service.client.ReferenceServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppRelatedServiceImpl {
    private final UserRepository userRepository;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final CompanyRepository companyRepository;
    private final RecruiterRepository recruiterRepository;
    private final ReferenceServiceClient referenceServiceClient;

    public RoleEnum getUserRole(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(User::getRole).orElse(null);
    }

    public ApplicantDTO getApplicantInfo(UUID userId) {
        Optional<JobSeeker> optionalHobSeeker = jobSeekerProfileRepository.findById(userId);
        ApplicantDTO applicantDTO = ApplicantDTO.builder().build();
        if(optionalHobSeeker.isPresent()) {
            JobSeeker js = optionalHobSeeker.get();
            JobSeekerLocation livesIn = js.getLivesIn();
            applicantDTO = ApplicantDTO.builder()
                  .careerLevel(js.getCareerLevel()!=null?js.getCareerLevel().name():null)
                  .firstName(js.getFirstName())
                  .lastName(js.getLastName())
                  .profilePhoto(js.getProfilePhoto())
                  .livesIn(livesIn!=null?JobSeekerLocationDTO.builder()
                          .state(livesIn.getState())
                          .city(livesIn.getCity())
                          .country(livesIn.getCountry())
                          .latitude(livesIn.getLatitude())
                          .longitude(livesIn.getLongitude())
                          .build():null)
                  .build();
        }
        return applicantDTO;
    }

    public TimelineEventCreatorNameAndPhoto getCreatorNameAndPhoto(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            return TimelineEventCreatorNameAndPhoto.builder().build();
        }
        String name = "";
        String photo = "";
        User user = optionalUser.get();
        if(user.getRole().equals(RoleEnum.ROLE_COMPANY)){
            Optional<Company> optionalCompany = companyRepository.findById(user.getId());
            if(optionalCompany.isEmpty()) {
                return TimelineEventCreatorNameAndPhoto.builder().build();
            }
            Company creator = optionalCompany.get();
            name = creator.getManagerFirstName()+ " " + creator.getManagerLastName();
            photo = creator.getLogo();
        }else if(user.getRole().equals(RoleEnum.ROLE_RECRUITER)){
            Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(user.getId());
            if(optionalRecruiter.isEmpty()) {
                return TimelineEventCreatorNameAndPhoto.builder().build();
            }
            Recruiter creator = optionalRecruiter.get();
            name = creator.getFirstName() + " " + creator.getLastName();
            photo = creator.getProfilePhoto();
        }
        return TimelineEventCreatorNameAndPhoto
                .builder()
                .name(name)
                .photoUrl(photo)
                .build();
    }

    public String getCreatorName(UUID userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) {
            return null;
        }
        String name = null;
        User user = optionalUser.get();
        if(user.getRole().equals(RoleEnum.ROLE_COMPANY)){
            Optional<Company> optionalCompany = companyRepository.findById(user.getId());
            if(optionalCompany.isEmpty()) {
                return name;
            }
            Company creator = optionalCompany.get();
            name = creator.getManagerFirstName()+ " " + creator.getManagerLastName();
        }else if(user.getRole().equals(RoleEnum.ROLE_RECRUITER)){
            Optional<Recruiter> optionalRecruiter = recruiterRepository.findById(user.getId());
            if(optionalRecruiter.isEmpty()) {
                return name;
            }
            Recruiter creator = optionalRecruiter.get();
            name = creator.getFirstName() + " " + creator.getLastName();
        }
        return name;
    }

    public JobSeekerAllInfoForAppDTO getJobSeekerAllInfo(UUID userId) {
        Optional<JobSeeker> optionalHobSeeker = jobSeekerProfileRepository.findById(userId);
        JobSeekerAllInfoForAppDTO jobSeekerAllInfoForAppDTO = JobSeekerAllInfoForAppDTO.builder().build();
        if(optionalHobSeeker.isPresent()) {
            JobSeeker js = optionalHobSeeker.get();
            JobSeekerLocation livesIn = js.getLivesIn();
            Set<SelectOption> skills = referenceServiceClient.getSkillsNames(js.getSkillIds());
            jobSeekerAllInfoForAppDTO = JobSeekerAllInfoForAppDTO.builder()
                    .careerLevel(js.getCareerLevel()!=null?js.getCareerLevel().name():null)
                    .firstName(js.getFirstName())
                    .lastName(js.getLastName())
                    .profilePhoto(js.getProfilePhoto())
                    .livesIn(livesIn!=null?livesIn.getCity() + ", " + livesIn.getCountry():null)
                    .about(js.getAbout())
                    .CV(js.getCV())
                    .birthDate(js.getBirthDate())
                    .createdDate(js.getCreatedDate())
                    .gender(js.getGender())
                    .jobStatus(js.getJobStatus())
                    .facebookLink(js.getFacebookLink())
                    .jobTitle(js.getJobTitle())
                    .githubLink(js.getGithubLink())
                    .jobsUserInterestedIn(js.getJobsUserInterestedIn())
                    .jobsTypesUserInterestedIn(js.getJobsTypesUserInterestedIn())
                    .linkedinLink(js.getLinkedinLink())
                    .minimumSalaryCurrency(js.getMinimumSalaryCurrency())
                    .mobileNumberCountryCode(js.getMobileNumberCountryCode())
                    .mobileNumber(js.getMobileNumber())
                    .Nationality(js.getNationality())
                    .openToSuggest(js.getOpenToSuggest())
                    .minimumSalaryValue(js.getMinimumSalaryValue())
                    .readyToRelocate(js.isReadyToRelocate())
                    .searchable(js.getSearchable())
                    .username(js.getUsername())
                    .yearsOfExperience(js.getYearsOfExperience())
                    .showMinimumSalary(js.getShowMinimumSalary())
                    .skills(skills)
                    .jobExperiences(js.getJobExperiences()
                            .stream().map(
                                    jobExperience -> JobExperienceDTO
                                            .builder()
                                            .id(jobExperience.getId())
                                            .jobCategory(jobExperience.getJobCategory())
                                            .place(jobExperience.getPlace())
                                            .jobType(jobExperience.getJobType())
                                            .startMonth(jobExperience.getStartMonth())
                                            .startYear(jobExperience.getStartYear())
                                            .endMonth(jobExperience.getEndMonth())
                                            .endYear(jobExperience.getEndYear())
                                            .name(jobExperience.getName())
                                            .build()
                            )
                            .toList())
                    .certificates(js.getCertificates().stream().map(
                            certificate ->
                                    CertificateDTO.builder()
                                            .id(certificate.getId())
                                            .title(certificate.getTitle())
                                            .month(certificate.getMonth())
                                            .year(certificate.getYear())
                                            .organization(certificate.getOrganization())
                                            .build()
                    ).toList())
                    .educations(js.getEducations()
                            .stream().map(
                                    education ->
                                            EducationDTO
                                                    .builder()
                                                    .id(education.getId())
                                                    .fieldOfStudy(education.getFieldOfStudy())
                                                    .degree(education.getDegree())
                                                    .start(education.getStart())
                                                    .end(education.getEnd())
                                                    .institution(education.getInstitution())
                                                    .grade(education.getGrade())
                                                    .build()
                            ).toList())
                    .languages(js.getLanguages()
                            .stream().map(
                                    language ->
                                            LanguageDTO
                                                    .builder()
                                                    .id(language.getId())
                                                    .languageLevel(language.getLanguageLevel())
                                                    .languageName(language.getLanguageName())
                                                    .build()
                            )
                            .toList())
                    .build();
        }
        return jobSeekerAllInfoForAppDTO;
    }
}
