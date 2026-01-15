package com.hpms.userservice.mapper.jobseeker;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.userservice.dto.jobseeker.JobSeekerAllInfoDTO;
import com.hpms.userservice.dto.jobseeker.JobSeekerSimpleDto;
import com.hpms.userservice.model.Education;
import com.hpms.userservice.model.jobseeker.Certificate;
import com.hpms.userservice.model.jobseeker.JobExperience;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.jobseeker.Language;
import com.hpms.userservice.service.client.ReferenceServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JobSeekerSourceDestinationMapper {

    private final ReferenceServiceClient referenceServiceClient;

    public JobSeekerAllInfoDTO jobSeekerToJobSeekerInfoDTO(JobSeeker js) {
        if ( js == null ) {
            return null;
        }

        JobSeekerAllInfoDTO.JobSeekerAllInfoDTOBuilder jobSeekerAllInfoDTO = JobSeekerAllInfoDTO.builder();

        jobSeekerAllInfoDTO.firstName( js.getFirstName() );
        jobSeekerAllInfoDTO.lastName( js.getLastName() );
        jobSeekerAllInfoDTO.username( js.getUsername() );
        jobSeekerAllInfoDTO.mobileNumberCountryCode( js.getMobileNumberCountryCode() );
        jobSeekerAllInfoDTO.mobileNumber( js.getMobileNumber() );
        jobSeekerAllInfoDTO.jobTitle( js.getJobTitle() );
        jobSeekerAllInfoDTO.createdDate( js.getCreatedDate() );
        jobSeekerAllInfoDTO.birthDate( js.getBirthDate() );
        jobSeekerAllInfoDTO.gender( js.getGender() );
        jobSeekerAllInfoDTO.profilePhoto( js.getProfilePhoto() );
        jobSeekerAllInfoDTO.livesIn( js.getLivesIn() );
        jobSeekerAllInfoDTO.readyToRelocate( js.isReadyToRelocate() );
        jobSeekerAllInfoDTO.yearsOfExperience( js.getYearsOfExperience() );
        jobSeekerAllInfoDTO.careerLevel( js.getCareerLevel() );
        jobSeekerAllInfoDTO.minimumSalaryValue( js.getMinimumSalaryValue() );
        jobSeekerAllInfoDTO.minimumSalaryCurrency( js.getMinimumSalaryCurrency() );
        jobSeekerAllInfoDTO.showMinimumSalary( js.getShowMinimumSalary() );
        Set<String> set = js.getJobsTypesUserInterestedIn();
        if ( set != null ) {
            jobSeekerAllInfoDTO.jobsTypesUserInterestedIn( new LinkedHashSet<String>( set ) );
        }
        Set<String> set1 = js.getJobsUserInterestedIn();
        if ( set1 != null ) {
            jobSeekerAllInfoDTO.jobsUserInterestedIn( new LinkedHashSet<String>( set1 ) );
        }
        jobSeekerAllInfoDTO.jobStatus( js.getJobStatus() );
        jobSeekerAllInfoDTO.searchable( js.getSearchable() );
        jobSeekerAllInfoDTO.openToSuggest( js.getOpenToSuggest() );
        Set<Certificate> set2 = js.getCertificates();
        if ( set2 != null ) {
            jobSeekerAllInfoDTO.certificates( new LinkedHashSet<Certificate>( set2 ) );
        }
        Set<JobExperience> set3 = js.getJobExperiences();
        if ( set3 != null ) {
            jobSeekerAllInfoDTO.jobExperiences( new LinkedHashSet<JobExperience>( set3 ) );
        }
        Set<Education> set4 = js.getEducations();
        if ( set4 != null ) {
            jobSeekerAllInfoDTO.educations( new LinkedHashSet<Education>( set4 ) );
        }
        Set<Long> set5 = js.getSkillIds();
        Set<SelectOption> skills = referenceServiceClient.getSkillsNames(set5);
        if ( set5 != null ) {
            jobSeekerAllInfoDTO.skills( new LinkedHashSet<SelectOption>( skills ) );
        }
        Set<Language> set6 = js.getLanguages();
        if ( set6 != null ) {
            jobSeekerAllInfoDTO.languages( new LinkedHashSet<Language>( set6 ) );
        }
        jobSeekerAllInfoDTO.about( js.getAbout() );
        jobSeekerAllInfoDTO.facebookLink( js.getFacebookLink() );
        jobSeekerAllInfoDTO.linkedinLink( js.getLinkedinLink() );
        jobSeekerAllInfoDTO.githubLink( js.getGithubLink() );
        jobSeekerAllInfoDTO.CV( js.getCV() );

        return jobSeekerAllInfoDTO.build();
    }

    public JobSeekerSimpleDto toSimpleDto(JobSeeker jobSeeker) {
        if ( jobSeeker == null ) {
            return null;
        }

        JobSeekerSimpleDto.JobSeekerSimpleDtoBuilder jobSeekerSimpleDto = JobSeekerSimpleDto.builder();

        jobSeekerSimpleDto.id( jobSeeker.getId() );
        jobSeekerSimpleDto.firstName( jobSeeker.getFirstName() );
        jobSeekerSimpleDto.lastName( jobSeeker.getLastName() );
        jobSeekerSimpleDto.username( jobSeeker.getUsername() );
        jobSeekerSimpleDto.jobTitle( jobSeeker.getJobTitle() );
        jobSeekerSimpleDto.profilePhoto( jobSeeker.getProfilePhoto() );
        jobSeekerSimpleDto.livesIn( jobSeeker.getLivesIn() );
        jobSeekerSimpleDto.yearsOfExperience( jobSeeker.getYearsOfExperience() );
        jobSeekerSimpleDto.careerLevel( jobSeeker.getCareerLevel() );
        jobSeekerSimpleDto.jobStatus( jobSeeker.getJobStatus() );
        jobSeekerSimpleDto.about( jobSeeker.getAbout() );

        return jobSeekerSimpleDto.build();
    }
}
