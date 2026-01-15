package com.hpms.jobservice.mapper;

import com.hpms.commonlib.dto.SelectOption;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.jobservice.dto.*;
import com.hpms.jobservice.model.JobPost;
import com.hpms.jobservice.repository.JobPostRepository;
import com.hpms.jobservice.service.client.AppServiceClient;
import com.hpms.jobservice.service.client.ReferenceServiceClient;
import com.hpms.jobservice.service.client.UserServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Component
@AllArgsConstructor
@Slf4j
public class JobPostMapper {

    private final UserServiceClient userServiceClient;
    private final AppServiceClient appServiceClient;
    private final JobPostRepository jobPostRepository;
    private final ReferenceServiceClient referenceServiceClient;

    public Page<JobPostDto> toPageDto(Page<JobPost> jobPostPage){
        return jobPostPage.map(this::toPublicDto);
    }
    public Page<JobPostDto> toPageDto(Page<JobPost> jobPostPage,UUID jobSeekerId){
        if(jobSeekerId == null) return jobPostPage.map(this::toPublicDto);
        return jobPostPage.map(jobPost -> toPublicDto(jobPost, jobSeekerId));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public JobPostDto toPublicDto(JobPost jobPost,UUID jobSeekerId){
        if (jobPost == null) {
            return null;
        }

//        CompletableFuture<JobRelatedDataDTO> relatedDataFuture = CompletableFuture.supplyAsync(() ->
//                getJobRelatedDataDTO(jobPost, jobSeekerId)
//        );
//
//        CompletableFuture<Boolean> isSavedFuture = CompletableFuture.supplyAsync(() ->
//                jobPostRepository.isJobPostSaved(jobSeekerId, jobPost.getId())
//        );
//
//        CompletableFuture<Integer> appsNumberFuture = CompletableFuture.supplyAsync(() ->
//                fetchApplicationsCount(jobPost.getId())
//        );
//
//        CompletableFuture.allOf(relatedDataFuture, isSavedFuture, appsNumberFuture).join();
//
//        JobRelatedDataDTO jobRelatedDataDTO = relatedDataFuture.join();
//        boolean isSaved = isSavedFuture.join();
//        int appsNumber = appsNumberFuture.join();

        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, jobSeekerId);
        boolean isSaved = jobPostRepository.isJobPostSaved(jobSeekerId, jobPost.getId());
        int appsNumber = fetchApplicationsCount(jobPost.getId());

        return JobPostDto.builder()
                .id(jobPost.getId())
                .companyImageUrl(jobRelatedDataDTO.getCompany().getImageUrl())
                .applicationNum(appsNumber)
                .jobTitle(jobPost.getJobTitle())
                .jobType(jobPost.getJobType().getValue())
                .company(jobRelatedDataDTO.getCompany().getName())
                .location(jobRelatedDataDTO.getCompany().getLocation())
                .publishedOn(jobPost.getPublishedOn())
                .employmentType(jobPost.getEmploymentType().getValue())
                .minExperienceYears(jobPost.getMinExperienceYears())
                .maxExperienceYears(jobPost.getMaxExperienceYears())
                .jobName(jobRelatedDataDTO.getJobName().getName())
                .industry(jobRelatedDataDTO.getIndustry().getName())
                .skills(jobRelatedDataDTO.getSkills())
                .saved(isSaved)
                .build();

    }

    private JobRelatedDataDTO getJobRelatedDataDTO(JobPost jobPost, UUID jobSeekerId) {


//        CompletableFuture<JobRelatedDataDTO> userDataFuture = CompletableFuture.supplyAsync(() ->
//                fetchUserServiceData(jobPost, jobSeekerId)
//        );
//
//        CompletableFuture<Set<String>> skillsFuture = CompletableFuture.supplyAsync(() ->
//                fetchSkillsData(jobPost.getSkillIds())
//        );

        try {
//            JobRelatedDataDTO jobRelatedData = userDataFuture.join();
//            Set<String> skills = skillsFuture.join();

            JobRelatedDataDTO jobRelatedData = fetchUserServiceData(jobPost, jobSeekerId);
            Set<SelectOption> skills = fetchSkillsData(jobPost.getSkillIds());

            jobRelatedData.setSkills(skills);
            return jobRelatedData;

        } catch (CompletionException e) {
            log.error("Error fetching job related data for job {}, seeker {}",
                    jobPost.getId(), jobSeekerId, e);
            return JobRelatedDataDTO.builder().build();
        }
    }

    private JobRelatedDataDTO fetchUserServiceData(JobPost jobPost, UUID jobSeekerId) {
        try {
            return userServiceClient.getJobsRelatedData(
                    JobRelatedDataRequest.builder()
                            .jobPostId(jobPost.getId())
                            .jobNameId(jobPost.getJobNameId())
                            .companyId(jobPost.getCompanyId())
                            .creatorId(jobPost.getCreatorId())
                            .recruiterIds(jobPost.getRecruiterIds())
                            .industryId(jobPost.getIndustryId())
                            .jobSeekerCallerId(jobSeekerId)
                            .build()
            );
        } catch (ServiceCommunicationException e) {
            log.warn("User service unavailable for job {}, returning partial data", jobPost.getId(), e);
            return JobRelatedDataDTO.builder().build();
        }
    }

    private Set<SelectOption> fetchSkillsData(Set<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return Collections.emptySet();
        }

        try {
            return referenceServiceClient.getSkillsNames(skillIds);
        } catch (ServiceCommunicationException e) {
            log.warn("Reference service unavailable, returning empty skills", e);
            return Collections.emptySet();
        }
    }

    private int fetchApplicationsCount(UUID jobPostId) {
        try {
            return appServiceClient.getNumberOfApplications(jobPostId);
        } catch (ServiceCommunicationException e) {
            log.warn("Application service unavailable for job {}", jobPostId);
            return 0;
        }
    }

    public JobPostDto toPublicDto(JobPost jobPost){
        if ( jobPost == null ) {
            return null;
        }

        Integer applicationsNumber = null;
        try {
            applicationsNumber = appServiceClient.getNumberOfApplications(jobPost.getId());
        } catch (ServiceCommunicationException e) {
            log.warn("Application service unavailable for job post {}", jobPost.getId());
        }
        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, null);
        return JobPostDto.builder()
                .id(jobPost.getId())
                .companyImageUrl(jobRelatedDataDTO.getCompany().getImageUrl())
                .applicationNum(applicationsNumber)
                .jobTitle(jobPost.getJobTitle())
                .jobType(jobPost.getJobType().getValue())
                .company(jobRelatedDataDTO.getCompany().getName())
                .location(jobRelatedDataDTO.getCompany().getLocation())
                .publishedOn(jobPost.getPublishedOn())
                .employmentType(jobPost.getEmploymentType().getValue())
                .minExperienceYears(jobPost.getMinExperienceYears())
                .maxExperienceYears(jobPost.getMaxExperienceYears())
                .jobName(jobRelatedDataDTO.getJobName().getName())
                .industry(jobRelatedDataDTO.getIndustry().getName())
                .skills(jobRelatedDataDTO.getSkills())
                .build();

    }

    private List<RecruiterNameAndPhoto> getRecruitersInfo(JobPost jobPost) {
        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, null);

        return jobRelatedDataDTO.getRecruiters();
    }
//    private JobPostDashboardNumbers getDashboardNumbers(JobPost jobPost){
//        List<JobApplication> applications = jobPost.getJobApplications();
//        long applicationsNumber, newAppsNumber, commentsNumber = 0;
//        applicationsNumber = applications.size();
//        newAppsNumber = applications.stream().filter((app) -> !app.isViewed()).count();
//        for (JobApplication application:
//                applications) {
//            commentsNumber += application.getComments().size();
//        }
//        return JobPostDashboardNumbers
//                .builder()
//                .applied(applicationsNumber)
//                .newApps(newAppsNumber)
//                .comments(commentsNumber)
//                .build();
//    }

    public JobPostResponse toEmployerDto(JobPost jobPost, JobApplicationNumberPerStatus numberPerStatus){
        if ( jobPost == null ) {
            return null;
        }
//        JobPostDashboardNumbers dashboardNumbers = getDashboardNumbers(jobPost);
        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, null);

        JobPostResponse.JobPostResponseBuilder jobPostResponse = JobPostResponse.builder();

        jobPostResponse.id( jobPost.getId() );
        jobPostResponse.jobTitle( jobPost.getJobTitle() );
        jobPostResponse.jobType( jobPost.getJobType() );
        jobPostResponse.employmentType( jobPost.getEmploymentType() );
        jobPostResponse.minExperienceYears( jobPost.getMinExperienceYears() );
        jobPostResponse.maxExperienceYears( jobPost.getMaxExperienceYears() );
        jobPostResponse.jobName( jobRelatedDataDTO.getJobName() );
        jobPostResponse.draft(jobPost.isDraft());
//        jobPostResponse.dashboardNumbers(dashboardNumbers);
        jobPostResponse.recruitersTeam(getRecruitersInfo(jobPost));

        jobPostResponse.industry(jobRelatedDataDTO.getIndustry());
        jobPostResponse.skills(jobRelatedDataDTO.getSkills());
        jobPostResponse.minSalary( jobPost.getMinSalary() );
        jobPostResponse.maxSalary( jobPost.getMaxSalary() );
        jobPostResponse.currency( jobPost.getCurrency() );
        jobPostResponse.description( jobPost.getDescription() );
        jobPostResponse.requirements( jobPost.getRequirements() );
        jobPostResponse.benefits( jobPost.getBenefits() );
        jobPostResponse.createdOn( jobPost.getCreatedOn() );
        jobPostResponse.updatedOn( jobPost.getUpdatedOn() );
        jobPostResponse.maxApplication( jobPost.getMaxApplication() );
        jobPostResponse.open( jobPost.isOpen() );
        jobPostResponse.deleted(jobPost.isDeleted());
        jobPostResponse.numberPerStatus(numberPerStatus);
        jobPostResponse.companyLocation(jobRelatedDataDTO.getCompany().getLocation());

        return jobPostResponse.build();
    }



    public JobPostResponse toInitInfoDto(JobPost jobPost) {
        if ( jobPost == null ) {
            return null;
        }

        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, null);
        JobPostResponse.JobPostResponseBuilder jobPostResponse = JobPostResponse.builder();

        jobPostResponse.id( jobPost.getId() );
        jobPostResponse.jobTitle( jobPost.getJobTitle() );
        jobPostResponse.jobType( jobPost.getJobType() );
        jobPostResponse.employmentType( jobPost.getEmploymentType() );
        jobPostResponse.minExperienceYears( jobPost.getMinExperienceYears() );
        jobPostResponse.maxExperienceYears( jobPost.getMaxExperienceYears() );
        jobPostResponse.jobName(jobRelatedDataDTO.getJobName());
        jobPostResponse.draft(jobPost.isDraft());

        jobPostResponse.industry(jobRelatedDataDTO.getIndustry());
        jobPostResponse.skills(jobRelatedDataDTO.getSkills());
        jobPostResponse.minSalary( jobPost.getMinSalary() );
        jobPostResponse.maxSalary( jobPost.getMaxSalary() );
        jobPostResponse.currency( jobPost.getCurrency() );
        jobPostResponse.description( jobPost.getDescription() );
        jobPostResponse.requirements( jobPost.getRequirements() );
        jobPostResponse.benefits( jobPost.getBenefits() );
        jobPostResponse.createdOn( jobPost.getCreatedOn() );
        jobPostResponse.updatedOn( jobPost.getUpdatedOn() );
        jobPostResponse.maxApplication( jobPost.getMaxApplication() );
        jobPostResponse.open( jobPost.isOpen() );
        jobPostResponse.deleted(jobPost.isDeleted());
        jobPostResponse.companyLocation(jobRelatedDataDTO.getCompany()!=null?jobRelatedDataDTO.getCompany().getLocation():null);

        return jobPostResponse.build();
    }

    public JobPostForUserResponse toInfoForUser(JobPost jobPost,UUID jobSeekerId) {
        if ( jobPost == null ) {
            return null;
        }
        boolean isSaved  = jobPostRepository.isJobPostSaved(jobSeekerId,jobPost.getId());
        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, jobSeekerId);
        JobPostForUserResponse.JobPostForUserResponseBuilder jobPostForUserResponse = JobPostForUserResponse.builder();

        jobPostForUserResponse.id( jobPost.getId() );
        jobPostForUserResponse.jobTitle( jobPost.getJobTitle() );
        jobPostForUserResponse.jobType( jobPost.getJobType() );
        jobPostForUserResponse.employmentType( jobPost.getEmploymentType() );
        jobPostForUserResponse.minExperienceYears( jobPost.getMinExperienceYears() );
        jobPostForUserResponse.maxExperienceYears( jobPost.getMaxExperienceYears() );
        jobPostForUserResponse.jobName(jobRelatedDataDTO.getJobName());
        jobPostForUserResponse.draft(jobPost.isDraft());
        jobPostForUserResponse.skills(jobRelatedDataDTO.getSkills());
        jobPostForUserResponse.industry(jobRelatedDataDTO.getIndustry());
        jobPostForUserResponse.saved(isSaved);
        jobPostForUserResponse.minSalary( jobPost.getMinSalary() );
        jobPostForUserResponse.maxSalary( jobPost.getMaxSalary() );
        jobPostForUserResponse.currency( jobPost.getCurrency() );
        jobPostForUserResponse.description( jobPost.getDescription() );
        jobPostForUserResponse.requirements( jobPost.getRequirements() );
        jobPostForUserResponse.benefits( jobPost.getBenefits() );
        jobPostForUserResponse.createdOn( jobPost.getCreatedOn() );
        jobPostForUserResponse.updatedOn( jobPost.getUpdatedOn() );
        jobPostForUserResponse.maxApplication( jobPost.getMaxApplication() );
        jobPostForUserResponse.open( jobPost.isOpen() );
        jobPostForUserResponse.deleted(jobPost.isDeleted());
        jobPostForUserResponse.companyLocation(jobRelatedDataDTO.getCompany()!=null?jobRelatedDataDTO.getCompany().getLocation():null);

        return jobPostForUserResponse.build();
    }

    public JobSettingDto toAdvancedSettingDto(JobPost jobPost) {
        if ( jobPost == null ) {
            return null;
        }
        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, null);
        JobSettingDto.JobSettingDtoBuilder jobSettingDto  = JobSettingDto.builder();
        jobSettingDto.title(jobPost.getJobTitle());
        jobSettingDto.jobPostId( jobPost.getId() );
        jobSettingDto.gender(jobPost.getGender() );
        jobSettingDto.open( jobPost.isOpen() );
        jobSettingDto.publish(!jobPost.isDraft());
        jobSettingDto.maxApplication(jobPost.getMaxApplication());
        jobSettingDto.educationLevel(jobPost.getEducationLevel());
        jobSettingDto.companyId(jobRelatedDataDTO.getCompany().getId());
        jobSettingDto.creatorId(jobRelatedDataDTO.getCreator().getId());

        List<QuestionDto> questionDtos = new ArrayList<>();
        jobPost.getQuestions().forEach(question -> {
            QuestionDto dto = QuestionDto.builder()
                    .id(question.getId())
                    .question(question.getQuestion())
                    .questionType(question.getQuestionType().name())
                    .build();
            System.out.println("*******************************************");
            System.out.println(question.getQuestion());
            questionDtos.add(dto);
        });
        jobSettingDto.questionDtos(questionDtos);

        SimpleRecruiterDto jobAdmin = getAdmin(jobPost);
        jobSettingDto.admin(jobAdmin);

        List<SimpleRecruiterDto> selectedTeamMembers = new ArrayList<>();
        jobRelatedDataDTO.getRecruiters().forEach(member -> {
            selectedTeamMembers.add(toRecruiterDto(member));
        });
        jobSettingDto.selectedTeamMembers(selectedTeamMembers);

        List<SimpleRecruiterDto> companyTeamMembers = getTeamMembers(jobRelatedDataDTO.getRecruiters(),jobRelatedDataDTO.getCreator().getId());
        System.out.println("*******************************************");
        System.out.println(companyTeamMembers.isEmpty());
        jobSettingDto.companyTeamMembers(companyTeamMembers);
        return jobSettingDto.build();
    }

    private SimpleRecruiterDto getAdmin(JobPost jobPost){
        JobRelatedDataDTO jobRelatedDataDTO = getJobRelatedDataDTO(jobPost, null);
        SimpleRecruiterDto jobAdmin = SimpleRecruiterDto.builder().build();
        if (jobRelatedDataDTO.getCompany().getId() == jobRelatedDataDTO.getCreator().getId()){
            jobAdmin.setJobTitle(jobRelatedDataDTO.getCompany().getManagerFirstName()+" "+jobRelatedDataDTO.getCompany().getManagerLastName());
            jobAdmin.setFullName(jobRelatedDataDTO.getCompany().getName());
            jobAdmin.setId(jobRelatedDataDTO.getCompany().getId());
            jobAdmin.setImageUrl(jobRelatedDataDTO.getCompany().getImageUrl());
        }else {
            JobPostCreatorDTO recruiter = jobRelatedDataDTO.getCreator();
            jobAdmin = SimpleRecruiterDto.builder()
                    .fullName(recruiter.getName())
                    .imageUrl(recruiter.getImageUrl())
                    .jobTitle(recruiter.getTitle())
                    .id(recruiter.getId())
                    .build();
        }
        return jobAdmin;
    }
    private List<SimpleRecruiterDto> getTeamMembers(List<RecruiterNameAndPhoto> recruiterNameAndPhotos, UUID creatorId) {
        List<SimpleRecruiterDto> companyTeamMembers = new ArrayList<>();
        recruiterNameAndPhotos.forEach(member -> {
            if(member.getId() != creatorId)
                companyTeamMembers.add(toRecruiterDto(member));
        });
        return companyTeamMembers ;
    }

    private SimpleRecruiterDto toRecruiterDto(RecruiterNameAndPhoto recruiter){
       return SimpleRecruiterDto.builder()
               .fullName(recruiter.getName())
               .imageUrl(recruiter.getPhoto())
               .jobTitle(recruiter.getTitle())
               .id(recruiter.getId())
               .build();
    }


}
