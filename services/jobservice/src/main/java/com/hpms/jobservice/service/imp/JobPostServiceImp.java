package com.hpms.jobservice.service.imp;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.dto.PageResponse;
import com.hpms.commonlib.handler.ServiceCommunicationException;
import com.hpms.commonlib.util.PageUtils;
import com.hpms.commonlib.util.PublicJwtTokenUtils;
import com.hpms.jobservice.constants.EmploymentType;
import com.hpms.jobservice.constants.JobType;
import com.hpms.jobservice.constants.QuestionType;
import com.hpms.jobservice.dto.*;
import com.hpms.jobservice.mapper.JobPostMapper;
import com.hpms.jobservice.mapper.QuestionMapper;
import com.hpms.jobservice.model.JobPost;
import com.hpms.jobservice.model.Question;
import com.hpms.jobservice.repository.JobPostRepository;
import com.hpms.jobservice.repository.QuestionRepository;
import com.hpms.jobservice.service.JobPostService;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class JobPostServiceImp implements JobPostService {

    private final JobPostRepository jobPostRepository;

    private final JobPostMapper jobPostMapper;

    private final QuestionRepository questionRepository;

    private final PublicJwtTokenUtils jwtTokenUtils;
    private final UserServiceClient userServiceClient;
    private final QuestionMapper questionMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public ApiResponse<?> getJobInitInfo(String token, UUID postId) {
        UUID userId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
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
        UUID userId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
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
//        JobApplicationNumberPerStatus applicationNumberPerStatus = jobApplicationService.getPostNumbersPerStatus(jobPost.getId());
        JobPostResponse jobPostForUserResponse = jobPostMapper.toEmployerDto(jobPost,
//                applicationNumberPerStatus
                null
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
//        List<JobApplication> applications = jobPost.getJobApplications();
//
//        long applied = applications.size();
//        long viewed = applications.stream().filter(JobApplication::isViewed).count();
//        long inConsideration = applications.stream().filter(jobApplication ->
//                !(new ArrayList<>(Arrays.asList(null, ApplicationStatus.APPLIED, ApplicationStatus.DISQUALIFIED))
//                        .contains(jobApplication.getStatus()))
//                ).count();
//        long rejected = applications.stream().filter(jobApplication ->
//                jobApplication.getStatus().equals(ApplicationStatus.DISQUALIFIED))
//                .count();
//        return JobPostPublicNumbers.builder()
//                .applied(applied)
//                .viewed(viewed)
//                .inConsideration(inConsideration)
//                .rejected(rejected)
//                .build();
        return null;
    }

    @Override
    public ApiResponse<?> updateInitJobInfo(String token, JobPostRequest jobPostRequest) {
        UUID companyId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCreatorIdOrCompanyIdOrTeamMember(jobPostRequest.getId(),companyId);
        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost = jobPostOptional.get();
        updateInitInfo(jobPost, jobPostRequest);
        jobPostRepository.save(jobPost);

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
        UUID companyId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
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
        UUID companyIdOrCreatorId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
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
    public ApiResponse<?> deleteJobPost(String token, UUID postId) {
        UUID companyIdOrAdminId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCompanyIdOrAdminIdNotDeleted(postId,companyIdOrAdminId);

        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost =  jobPostOptional.get();
        jobPost.setDeleted(true);
        jobPostRepository.save(jobPost);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Deleted")
                .build();
    }

    @Override
    public ApiResponse<?> closeJobPost(String token, UUID postId) {
        UUID companyIdOrAdminId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
        Optional<JobPost> jobPostOptional = jobPostRepository.getJobPostByIdAndCompanyIdOrAdminIdAndOpen(postId,companyIdOrAdminId);

        if (jobPostOptional.isEmpty()) {
            return ApiResponse.getDefaultErrorResponse();
        }
        JobPost jobPost =  jobPostOptional.get();
        jobPost.setOpen(false);
        jobPostRepository.save(jobPost);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.ACCEPTED.value())
                .message("Closed")
                .build();
    }

//    @Override
//    public ApiResponse<?> getApplicationsJobPostsForJobSeeker(String token) {
//        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
//        User user = null;
//
//        if (!isThereUserFromToken.isOk()) {
//            return isThereUserFromToken;
//        } else {
//            user = (User) isThereUserFromToken.getBody();
//            if(!user.getRole().equals(RoleEnum.ROLE_JOBSEEKER)){
//                return ApiResponse.builder()
//                        .ok(false)
//                        .status(HttpStatus.UNAUTHORIZED.value())
//                        .message("You have no authority to access this")
//                        .build();
//            }else {
//                List<JobApplication> jobApplications = jobApplicationRepository.findByJobSeekerId(user.getId());
//                List<JobPostForApplication> jobPosts = new ArrayList<>();
//                for (JobApplication app :
//                        jobApplications) {
//                    JobPost jobPost = app.getJobPost();
//                    Location companyLocation = jobPost.getCompany().getMainBranchLocation();
//                    JobPostPublicNumbers publicNumbers = getApplicationNumbersOfJobPost(jobPost);
//                    JobPostForApplication jobPostForApplication = JobPostForApplication
//                            .builder()
//                            .applicationId(app.getId())
//                            .applicationStatus(app.getStatus())
//                            .jobTitle(jobPost.getJobTitle())
//                            .jobId(jobPost.getId())
//                            .jobType(jobPost.getJobType())
//                            .employmentType(jobPost.getEmploymentType())
//                            .companyLocation(companyLocation.getCity() + " - " + companyLocation.getCountry())
//                            .companyLogo(jobPost.getCompany().getLogo())
//                            .open(jobPost.isOpen())
//                            .publicNumbers(publicNumbers)
//                            .newActionsNumber(getApplicationNewActionsNumber(app))
//                            .deleted(jobPost.isDeleted())
//                            .appUpdatedAt(app.getUpdatedAt())
//                            .build();
//                    jobPosts.add(jobPostForApplication);
//                }
//                return ApiResponse.builder()
//                        .ok(true)
//                        .status(HttpStatus.ACCEPTED.value())
//                        .message("Job posts data returned")
//                        .body(jobPosts)
//                        .build();
//
//            }
//        }
//
//    }
//

    @Transactional
    @Modifying
    public ApiResponse<?> toggleSavedJob(String token, UUID id) {
        UUID userId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
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

//    private int getApplicationNewActionsNumber (JobApplication application) {
//
//        int evaluationsNumber = 0;
//        if(application.getEvaluations() != null) {
//            evaluationsNumber = application.getEvaluations().stream().filter((app) -> {
//                return !app.isViewed();
//            }).toList().size();
//        }
//        int offerNumbers = 0;
//        int offerCommentsNumber = 0;
//        if(application.getOffer() != null){
//            offerNumbers = (application.getOffer().isViewed())? 0: 1;
//            offerCommentsNumber = application.getOffer().getComments().stream().filter((comment) -> {
//                return !comment.isViewed();
//            }).toList().size();
//        }
//
//        return evaluationsNumber + offerNumbers + offerCommentsNumber;
//    }

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
        UUID userId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));

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
        UUID userId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));

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
        UUID userId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));

        JobPost jobPost = createJob(jobPostRequest);
        CompanyAndRecruiterIds companyAndRecruiterIds = null;
        try {
            companyAndRecruiterIds = userServiceClient.getCompanyAndRecruiterIds(userId);
        } catch (ServiceCommunicationException e) {
            log.warn("User service unavailable for user {}", userId);
            companyAndRecruiterIds = CompanyAndRecruiterIds.builder().build();
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
        UUID companyId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
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
        UUID companyId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("draft"),
                Sort.Order.asc("createdOn"),
                Sort.Order.asc("updatedOn")
        ));
        Page<JobPost> allPosts = jobPostRepository.getAllCompanyJobPosts(companyId,pageable);
        List<UUID> jobIds = allPosts.stream().map(JobPost::getId).toList();

        Page<JobPostResponse> mappedPosts = mapJobPosts(allPosts);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(mappedPosts)
                .build();
    }

    @Override
    public ApiResponse<?> getNumberOfActiveJobPosts(String token) {
        UUID companyId = UUID.fromString(jwtTokenUtils.extractId(token.substring(7)));
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
//                        jobApplicationService.getPostNumbersPerStatus(jobPost.getId())
                        null
                ));
    }

}
