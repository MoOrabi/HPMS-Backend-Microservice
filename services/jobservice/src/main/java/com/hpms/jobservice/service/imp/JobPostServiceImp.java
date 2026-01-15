package com.hpms.jobservice.service.imp;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.dto.DeleteJobRelatedEvent;
import com.hpms.commonlib.dto.PageResponse;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.commonlib.util.PageUtils;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import com.hpms.jobservice.constants.EmploymentType;
import com.hpms.jobservice.constants.JobType;
import com.hpms.jobservice.constants.QuestionType;
import com.hpms.jobservice.dto.*;
import com.hpms.jobservice.dto.app.CompanyLocationAndLogoDTO;
import com.hpms.jobservice.event.JobPostEventPublisher;
import com.hpms.jobservice.mapper.JobPostMapper;
import com.hpms.jobservice.mapper.QuestionMapper;
import com.hpms.jobservice.model.JobPost;
import com.hpms.jobservice.model.Question;
import com.hpms.jobservice.repository.JobPostRepository;
import com.hpms.jobservice.repository.QuestionRepository;
import com.hpms.jobservice.service.DeleteJobRelatedProducer;
import com.hpms.jobservice.service.JobPostService;
import com.hpms.jobservice.service.client.AppServiceClient;
import com.hpms.jobservice.service.client.UserServiceClient;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class JobPostServiceImp implements JobPostService {

    private final JobPostRepository jobPostRepository;

    private final JobPostMapper jobPostMapper;

    private final QuestionRepository questionRepository;

    private final UserServiceClient userServiceClient;
    private final QuestionMapper questionMapper;

    private final AppServiceClient appServiceClient;

    private final DeleteJobRelatedProducer deleteJobRelatedProducer;
    private final JobPostEventPublisher jobPostEventPublisher;

    @Override
    public ApiResponse<?> getJobInitInfo(String token, UUID postId) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCreatorIdOrCompanyIdOrTeamMember(postId,userId);
        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPostResponse jobPostResponse = jobPostMapper.toInitInfoDto(jobPostOptional.get());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(jobPostResponse)
                .build();
    }

    public ApiResponse<?> getJobInfoForAnyUser(String token, UUID postId) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdForAnyUser(postId);
        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost = jobPostOptional.get();
        JobPostForUserResponse jobPostForUserResponse = jobPostMapper.toInfoForUser(jobPost,userId);
        JobPostPublicNumbers jobPostPublicNumbers = getApplicationNumbersOfJobPost(jobPost);
        jobPostForUserResponse.setPublicNumbers(jobPostPublicNumbers);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(jobPostForUserResponse)
                .build();
    }

    public ApiResponse<?> getJobInfoForAppPage(String token, UUID postId) {
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdForAnyUser(postId);
        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost = jobPostOptional.get();
        JobApplicationNumberPerStatus applicationNumberPerStatus = appServiceClient.getPostNumbersPerStatus(jobPost.getId());
        JobPostResponse jobPostForUserResponse = jobPostMapper.toEmployerDto(jobPost,
                applicationNumberPerStatus
        );
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(jobPostForUserResponse)
                .build();
    }

    @Override
    public ApiResponse<?> getJobApplicationQuestions(UUID jobId) {
        List<Question> questionList = questionRepository.findByJobPostId(jobId);
        String jobTitle = jobPostRepository.getJobTitleByJobPostId(jobId);
        List<QuestionDto> questionDtosList = questionMapper.toDtoList(questionList);

        JobApplicationFormDto jobApplicationFormDto = JobApplicationFormDto.builder()
                .questionDtosList(questionDtosList)
                .jobTitle(jobTitle)
                .jobPostId(jobId)
                .build();

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(jobApplicationFormDto)
                .message("Success")
                .build();
    }

    private JobPostPublicNumbers getApplicationNumbersOfJobPost(JobPost jobPost) {
        return appServiceClient.getPostPublicNumbers(jobPost.getId());
    }

    private static final int MIN_UPDATE_INTERVAL_HOURS = 1;

    public boolean canUpdateProfile(LocalDateTime lastUpdate) {
        if (lastUpdate == null) {
            return true;
        }
        Duration duration = Duration.between(lastUpdate, LocalDateTime.now());
        return duration.toHours() >= MIN_UPDATE_INTERVAL_HOURS;
    }
    @Override
    public ApiResponse<?> updateInitJobInfo(String token, JobPostRequest jobPostRequest) {
        UUID companyId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCreatorIdOrCompanyIdOrTeamMember(jobPostRequest.getId(),companyId);
        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost = jobPostOptional.get();

        if (jobPost.getUpdatedOn() != null) {
            if (!canUpdateProfile(jobPost.getUpdatedOn())) {
                log.warn("Job post {} was updated too frequently", jobPost.getId());
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.TOO_MANY_REQUESTS.value())
                        .message("You must wait for an hour from last update you have done!")
                        .build();
            }
        }
        boolean matchingPropChanged = (jobPostRequest.getMinExperienceYears() == jobPost.getMinExperienceYears()
        || jobPostRequest.getMaxExperienceYears() == jobPost.getMinExperienceYears()
        ||jobPostRequest.getJobType().equals(jobPost.getJobType().name())
        ||jobPostRequest.isRemote() == jobPost.isRemote());

        updateInitInfo(jobPost, jobPostRequest);
        jobPost.setUpdatedOn(LocalDateTime.now());
        jobPostRepository.save(jobPost);


        if(matchingPropChanged) {
            jobPostEventPublisher.publishJobMatchingPropUpdated(jobPost);
        } else {
            jobPostEventPublisher.publishJobUpdated(jobPost);
        }

        JobPostResponse jobPostResponse = jobPostMapper.toInitInfoDto(jobPostOptional.get());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Updated")
                .body(jobPostResponse)
                .build();
    }

    @Override
    public ApiResponse<?> getJobAdvancedSetting(String token, UUID postId) {
        UUID companyId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCreatorIdOrCompanyIdOrTeamMember(postId,companyId);
        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobSettingDto advancedSettingDto = jobPostMapper.toAdvancedSettingDto(jobPostOptional.get());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(advancedSettingDto)
                .build();
    }

    @Override
    public ApiResponse<?> deleteNewJobPost(String token, UUID postId) {
        UUID companyIdOrCreatorId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCreatorIdOrCompanyIdOrTeamMember(postId,companyIdOrCreatorId);

        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost =  jobPostOptional.get();
        jobPostRepository.delete(jobPost);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Deleted")
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<?> deleteJobPost(String token, UUID postId) {
        UUID companyIdOrAdminId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCompanyIdOrAdminIdNotDeleted(postId,companyIdOrAdminId);

        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost =  jobPostOptional.get();
        jobPost.setDeleted(true);
        jobPostRepository.save(jobPost);
        deleteJobRelatedProducer.sendDeleteEvent(
                new DeleteJobRelatedEvent(postId)
        );

        jobPostEventPublisher.publishJobDeleted(jobPost.getId());

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Deleted")
                .build();
    }

    @Override
    public ApiResponse<?> closeJobPost(String token, UUID postId) {
        UUID companyIdOrAdminId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCompanyIdOrAdminIdAndOpen(postId,companyIdOrAdminId);

        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost =  jobPostOptional.get();
        jobPost.setOpen(false);
        jobPostRepository.save(jobPost);

        jobPostEventPublisher.publishJobClosed(jobPost.getId());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Closed")
                .build();
    }

    @Override
    public ApiResponse<?> getApplicationsJobPostsForJobSeeker(String token) {
        UUID userId = PublicJwtTokenUtils.extractUUID(token.substring(7));
        try {
            // Step 1: Get applications from application service
            log.info("Fetching applications for user: {}", userId);
            List<JobPostForApplication> applications = appServiceClient.getJobPostsForApplications(userId);

            if (applications == null || applications.isEmpty()) {
                log.info("No applications found for user: {}", userId);
                return ApiResponse.<List<JobPostForApplication>>builder()
                        .ok(true)
                        .status(HttpStatus.OK.value())
                        .message("No applications found")
                        .body(Collections.emptyList())
                        .build();
            }

            // Step 2: Extract job IDs
            List<UUID> jobIds = applications.stream()
                    .map(JobPostForApplication::getJobId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            Map<UUID, JobPost> jobPostsMap = fetchJobPosts(jobIds);

            if (jobPostsMap.isEmpty()) {
                log.warn("No job posts found for applications");
                return ApiResponse.<List<JobPostForApplication>>builder()
                        .ok(true)
                        .status(HttpStatus.OK.value())
                        .message("No applications found")
                        .body(Collections.emptyList())
                        .build();
            }

            // Step 3: Extract company IDs and fetch company data
            List<UUID> companyIds = jobPostsMap.values().stream()
                    .map(JobPost::getCompanyId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            Map<UUID, CompanyLocationAndLogoDTO> companyDataMap = fetchCompanyData(companyIds);

            // Step 4: Enrich applications with job and company data
            List<JobPostForApplication> enrichedApplications = enrichApplications(
                    applications,
                    jobPostsMap,
                    companyDataMap
            );

            log.info("Successfully enriched {} applications for user: {}", enrichedApplications.size(), userId);

            return ApiResponse.<List<JobPostForApplication>>builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .message("Job posts data returned")
                    .body(enrichedApplications)
                    .build();

        } catch (ServiceCommunicationException e) {
            log.error("Error fetching job posts for applications for user: {}", userId, e);
            throw new ServiceCommunicationException("application service or user service ","Failed to fetch job post applications", e);
        }
    }

    // Helper: Fetch job posts and convert to map for O(1) lookup
    private Map<UUID, JobPost> fetchJobPosts(List<UUID> jobIds) {
        if (jobIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            log.debug("Fetching {} job posts from repository", jobIds.size());
            List<JobPost> jobPosts = jobPostRepository.findAllById(jobIds);

            return jobPosts.stream()
                    .collect(Collectors.toMap(JobPost::getId, Function.identity()));
        } catch (ServiceCommunicationException e) {
            log.error("Error fetching job posts: {}", jobIds, e);
            return Collections.emptyMap();
        }
    }

    // Helper: Fetch company data and convert to map for O(1) lookup
    private Map<UUID, CompanyLocationAndLogoDTO> fetchCompanyData(List<UUID> companyIds) {
        if (companyIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            log.debug("Fetching company data for {} companies", companyIds.size());
            List<CompanyLocationAndLogoDTO> companyData =
                    userServiceClient.getCompanyLocationAndLogos(companyIds);

            return companyData.stream()
                    .collect(Collectors.toMap(CompanyLocationAndLogoDTO::getId, Function.identity()));
        } catch (ServiceCommunicationException e) {
            log.error("Error fetching company data from user service for IDs: {}", companyIds, e);
            return Collections.emptyMap();
        }
    }

    private List<JobPostForApplication> enrichApplications(
            List<JobPostForApplication> applications,
            Map<UUID, JobPost> jobPostsMap,
            Map<UUID, CompanyLocationAndLogoDTO> companyDataMap) {

        return applications.stream()
                .map(app -> enrichSingleApplication(app, jobPostsMap, companyDataMap))
                .filter(Objects::nonNull)
                .toList();
    }

    // Helper: Enrich single application with job and company data
    private JobPostForApplication enrichSingleApplication(
            JobPostForApplication application,
            Map<UUID, JobPost> jobPostsMap,
            Map<UUID, CompanyLocationAndLogoDTO> companyDataMap) {

        UUID jobId = application.getJobId();
        if (jobId == null) {
            log.warn("Application has null jobId, skipping");
            return null;
        }

        JobPost jobPost = jobPostsMap.get(jobId);
        if (jobPost == null) {
            log.warn("JobPost not found for ID: {}, skipping application", jobId);
            return null;
        }

        // Enrich with job data
        application.setJobTitle(jobPost.getJobTitle());
        application.setJobId(jobPost.getId());
        application.setJobType(jobPost.getJobType());
        application.setEmploymentType(jobPost.getEmploymentType());
        application.setOpen(jobPost.isOpen());
        application.setDeleted(jobPost.isDeleted());

        // Enrich with company data (graceful if unavailable)
        CompanyLocationAndLogoDTO companyData = companyDataMap.get(jobPost.getCompanyId());
        if (companyData != null) {
            application.setCompanyLocation(companyData.getLocation());
            application.setCompanyLogo(companyData.getLogo());
        } else {
            log.debug("Company data not found for company ID: {}", jobPost.getCompanyId());
            application.setCompanyLocation(null);
            application.setCompanyLogo(null);
        }

        // Enrich with public numbers
        try {
            JobPostPublicNumbers publicNumbers = getApplicationNumbersOfJobPost(jobPost);
            application.setPublicNumbers(publicNumbers);
        } catch (Exception e) {
            log.error("Error calculating public numbers for job: {}", jobId, e);
            application.setPublicNumbers(JobPostPublicNumbers.builder().build());
        }

        return application;
    }


    @Transactional
    @Modifying
    public ApiResponse<?> toggleSavedJob(String token, UUID id) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.findById(id);
        if(jobPostOptional.isEmpty()) return ApiResponse.getDefaultErrorResponse();
        JobPost jobPost = jobPostOptional.get();
        if(jobPost.getJobseekerSavers().contains(userId)) {
            jobPost.getJobseekerSavers().remove(userId);
        }
        else {
            jobPost.getJobseekerSavers().add(userId);
        }
        jobPostRepository.save(jobPost);

        return ApiResponse.getDefaultSuccessResponse();
    }

    @Override
    public ApiResponse<?> getPublishedJobs(String token, UUID companyId, int page , int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("publishedOn")
        ));
        Page<JobPost> jobPostPage = jobPostRepository.getByCompanyId(companyId,pageable);
        Page<JobPostDto> jobPostDtoPage = jobPostPage.map(jobPostMapper::toPublicDto);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("success")
                .body(jobPostDtoPage)
                .build();
    }

    public ApiResponse<?> saveJobPost(String token, UUID postId) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));

        Optional<JobPost> jobPostOptional = jobPostRepository.findById(postId);
        if(jobPostOptional.isEmpty()){
            return ApiResponse.getDefaultErrorResponse();
        }

        JobPost jobPost = jobPostOptional.get();
        jobPost.getJobseekerSavers().add(userId);
        jobPostRepository.save(jobPost);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Job Saved")
                .build();
    }

    @Override
    public ApiResponse<?> getSavedJobs(String token,int page , int size) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));

        Pageable pageable = PageRequest.of(page, size);
        Page<JobPost> jobPostPage = jobPostRepository.findSavedJobsByJobSeekerId(userId,pageable);
        Page<JobPostDto> jobPostDtoPage = jobPostPage.map(jobPost -> jobPostMapper.toPublicDto(jobPost,userId));
        PageResponse<JobPostDto> response = PageUtils.toPageResponse(jobPostDtoPage);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("success")
                .body(response)
                .build();
    }


    private void updateInitInfo(JobPost jobPost, JobPostRequest jobPostRequest) {
        jobPost.setJobTitle(jobPostRequest.getJobTitle());
        jobPost.setJobType(JobType.valueOf(jobPostRequest.getJobType()));
        jobPost.setDescription(jobPostRequest.getDescription());
        jobPost.setRequirements(jobPostRequest.getRequirements());
        jobPost.setMinExperienceYears(jobPostRequest.getMinExperienceYears());
        jobPost.setMaxExperienceYears(jobPostRequest.getMaxExperienceYears());
        jobPost.setMinSalary(jobPostRequest.getMinSalary());
        jobPost.setMaxSalary(jobPostRequest.getMaxSalary());
        jobPost.setCurrency(jobPostRequest.getCurrency());
        jobPost.setBenefits(jobPostRequest.getBenefits());
        jobPost.setSkillIds(jobPostRequest.getSkills());
        jobPost.setJobNameId(jobPostRequest.getJobNameId());
        jobPost.setIndustryId(jobPostRequest.getIndustryId());
    }


    @Override
    public ApiResponse<?> createInitJobInfo(String token, JobPostRequest jobPostRequest) {
        UUID userId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));

        JobPost jobPost = createJob(jobPostRequest);
        CompanyAndRecruiterIds companyAndRecruiterIds = CompanyAndRecruiterIds.builder().build();
        try {
            companyAndRecruiterIds = userServiceClient.getCompanyAndRecruiterIds(userId);
        } catch (ServiceCommunicationException e) {
            log.warn("User service unavailable for user {}", userId);
        }

        jobPost.setCompanyId(companyAndRecruiterIds.getCompanyId());
        jobPost.setCreatorId(companyAndRecruiterIds.getRecuiterId());

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        JobPostResponse jobPostResponse = jobPostMapper.toInitInfoDto(savedJobPost);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Job Saved as Draft")
                .body(jobPostResponse)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<?> publishOrSaveJob(String token, JobSettingDto jobSettingDto) {
        UUID companyId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCreatorIdOrCompanyIdOrTeamMember(jobSettingDto.getJobPostId(),companyId);

        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }

        JobPost jobPost = jobPostOptional.get();
        updateJobPost(jobPost, jobSettingDto);
        if (jobSettingDto.isPublish()) {
            jobPost.setDraft(false);
            jobPost.setOpen(true);
            jobPost.setPublishedOn(LocalDateTime.now());
        }

        JobPost savedJobPost = jobPostRepository.save(jobPost);
        if(jobSettingDto.isPublish()) {
            jobPostEventPublisher.publishJobPublished(savedJobPost);
        }
        JobSettingDto advancedSettingDto = jobPostMapper.toAdvancedSettingDto(savedJobPost);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .body(advancedSettingDto)
                .message("Job Updated")
                .build();
    }

    private JobPost createJob(JobPostRequest jobPostRequest) {
        JobPost jobPost = JobPost.builder()
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .jobTitle(jobPostRequest.getJobTitle())
                .industryId(jobPostRequest.getIndustryId())
                .jobType(JobType.valueOf(jobPostRequest.getJobType()))
                .description(jobPostRequest.getDescription())
                .requirements(jobPostRequest.getRequirements())
                .employmentType(EmploymentType.valueOf(jobPostRequest.getEmploymentType()))
                .benefits(jobPostRequest.getBenefits())
                .currency(jobPostRequest.getCurrency())
                .maxSalary(jobPostRequest.getMaxSalary())
                .minSalary(jobPostRequest.getMinSalary())
                .open(false)
                .draft(true)
                .deleted(false)
                .questions(new ArrayList<>())
                .recruiterIds(new HashSet<>())
                .skillIds(jobPostRequest.getSkills())
                .jobNameId(jobPostRequest.getJobNameId())
                .build();
        return jobPost;
    }


    private void updateJobPost(JobPost jobPost, JobSettingDto jobSettingDto) {
        List<Question> newQuestionList = new ArrayList<>();
        jobPost.setGender(jobSettingDto.getGender());

        jobPost.setMaxApplication(jobSettingDto.getMaxApplication());

        jobPost.setEducationLevel(jobSettingDto.getEducationLevel());

        if(jobSettingDto.getSelectedTeamMembers() != null){
            Set<UUID> teamIds = new HashSet<>();
            jobSettingDto.getSelectedTeamMembers().forEach(el -> {
                teamIds.add(el.getId());
            });
          //  notificationService.sendAddRecruiterToHPNotification(jobPost.getCompany(), recruiterList,jobPost.getJobTitle());
            jobPost.setRecruiterIds(teamIds);
        }

        if(jobSettingDto.getQuestionDtos() != null){
            jobSettingDto.getQuestionDtos().forEach(el -> {
                Question q = Question.builder()
                        .question(el.question)
                        .questionType(QuestionType.valueOf(el.questionType))
                        .jobPost(jobPost).build();
                if(el.getId() != null ){
                    q.setId(el.getId());
                }
                newQuestionList.add(q);
            });
        }
        questionRepository.saveAll(newQuestionList);
        // questionRepository.deleteAllByJobPostId(jobPost.getId());

        jobPost.setQuestions(newQuestionList);

    }

    public ApiResponse<?> getCompanyJobs(String token , int page, int size) {
        UUID companyId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("draft"),
                Sort.Order.asc("createdOn"),
                Sort.Order.asc("updatedOn")
        ));
        Page<JobPost> allPosts = jobPostRepository.getAllCompanyJobPosts(companyId,pageable);

        Page<JobPostResponse> mappedPosts = mapJobPosts(allPosts);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(mappedPosts)
                .build();
    }

    @Override
    public ApiResponse<?> getNumberOfActiveJobPosts(String token) {
        UUID companyId = UUID.fromString(PublicJwtTokenUtils.extractId(token.substring(7)));
        int count =  jobPostRepository.countActiveJobsForCompany(companyId);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(count)
                .build();
    }



    private Page<JobPostResponse> mapJobPosts(Page<JobPost> jobPostsPage) {
        return jobPostsPage
                .map(jobPost -> jobPostMapper.toEmployerDto(jobPost,
                        appServiceClient.getPostNumbersPerStatus(jobPost.getId())
                ));
    }

}
