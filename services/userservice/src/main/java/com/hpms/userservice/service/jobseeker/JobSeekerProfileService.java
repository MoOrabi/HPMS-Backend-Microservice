package com.hpms.userservice.service.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.dto.jobseeker.*;
import com.hpms.userservice.mapper.jobseeker.JobSeekerSourceDestinationMapper;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.jobseeker.JobSeekerLocation;
import com.hpms.userservice.model.jobseeker.WorkSample;
import com.hpms.userservice.model.shared.Skill;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.jobseeker.JobSeekerLocationRepository;
import com.hpms.userservice.repository.jobseeker.WorkSamplesRepository;
import com.hpms.userservice.repository.shared.JobNameRepository;
import com.hpms.userservice.repository.shared.SkillRepository;
import com.hpms.userservice.utils.FrequentlyUsed;
import com.hpms.userservice.utils.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.hpms.userservice.utils.FrequentlyUsed.cvExtensions;
import static com.hpms.userservice.utils.FrequentlyUsed.imageExtensions;


@Service
public class JobSeekerProfileService {
    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Autowired
    private JobNameRepository jobNameRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private JobSeekerLocationRepository locationRepository;
    @Autowired
    private JwtTokenUtils tokenUtils;
    @Autowired
    private FrequentlyUsed frequentlyUsed;

    @Autowired
    private WorkSamplesRepository workSamplesRepository;

    @Autowired
    JobSeekerSourceDestinationMapper jobSeekerSourceDestinationMapper;


    public ApiResponse<?> saveProfilePhoto(String token, MultipartFile profileFile) throws Exception {
        return frequentlyUsed.addFileToUser(token, profileFile, "js-profile", 2 * 1048,
                "jobSeeker-files/profile/",
                "Photo Uploaded Successfully", jobSeekerProfileRepository, imageExtensions, null);
    }

    public ApiResponse<?> getProfilePhoto(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        String filePathWithName = jobSeeker.getProfilePhoto();

//        int lastOfSlash = filePathWithName.lastIndexOf('\\');
//        String path = filePathWithName.substring(0, lastOfSlash);
//        String filename = filePathWithName.substring(lastOfSlash + 1);
//        System.out.println(path + " " + filename);
//        Resource file = FileUploadUtil.loadImage(path, filename);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(filePathWithName)
                .build();
    }

    public ApiResponse<?> addWorkSample(String token, MultipartFile file, String title, String description,
                                        String link) throws Exception {
        return frequentlyUsed.addFileToUser(token, file, "js-workSample", 5 * 1024,
                "jobSeeker-files/work-samples/", "Work sample uploaded successfully",
                jobSeekerProfileRepository, imageExtensions, new WorkSample(null, title, description, link, null));
    }

    public ApiResponse<?> getWorkSamples(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Set<WorkSample> filePathsWithName = jobSeeker.getWorkSamples();

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(filePathsWithName)
                .build();
    }

    public ApiResponse<?> getWorkSamplesById(String token,String id){

        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        Optional<JobSeeker> optionalJobSeeker ;
//        Validate USER
        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        }

         optionalJobSeeker = jobSeekerProfileRepository.findById(UUID.fromString(id)) ;

//        VALIDATE ID
        if(optionalJobSeeker.isPresent()){
            Set<WorkSample> filePathsWithName = optionalJobSeeker.get().getWorkSamples();
            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .ok(true)
                    .body(filePathsWithName)
                    .build();
        }
        return ApiResponse.builder()
                .ok(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Not A Valid User id")
                .build();
    }


    public ApiResponse<?> deleteWorkSample(String userToken, String sampleId) {
        ApiResponse<?> getCurrentUserResponse = frequentlyUsed.getUserFromTokenIfExist(userToken);
        Optional<WorkSample> optionalWorkSample = workSamplesRepository.findById(UUID.fromString(sampleId));
        if (optionalWorkSample.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Work sample id is not correct")
                    .build();
        }
        if (!getCurrentUserResponse.isOk()) {
            return getCurrentUserResponse;
        }
        JobSeeker currentUser = (JobSeeker) getCurrentUserResponse.getBody();
        WorkSample targetWorkSample = optionalWorkSample.get();

        if (!targetWorkSample.getJobSeeker().equals(currentUser)) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .message("You have no authority to delete others educations")
                    .build();
        }

        workSamplesRepository.delete(targetWorkSample);
        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body("Sample Deleted Successfully")
                .build();
    }


    public ApiResponse<?> saveBasicInfo(String token, JobSeekerBasicInfoDTO basicInfoDTO) {

        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }


        jobSeeker.setFirstName(basicInfoDTO.getFirstName());
        jobSeeker.setLastName(basicInfoDTO.getLastName());
        jobSeeker.setMobileNumberCountryCode(basicInfoDTO.getMobileNumberCountryCode());
        jobSeeker.setMobileNumber(basicInfoDTO.getMobileNumber());
        jobSeeker.setGender(basicInfoDTO.getGender());
        jobSeeker.setAbout(basicInfoDTO.getAbout());
        jobSeeker.setBirthDate(basicInfoDTO.getBirthDate());
        jobSeeker.setNationality(basicInfoDTO.getNationality());

        JobSeekerLocation newLocation = basicInfoDTO.getLivesIn();

        Optional<JobSeekerLocation> location = locationRepository.findByCountryAndStateAndCity(newLocation.getCountry(),
                newLocation.getState(), newLocation.getCity());
        location.ifPresent(basicInfoDTO::setLivesIn);
        jobSeeker.setLivesIn(basicInfoDTO.getLivesIn());


        jobSeeker.setFacebookLink(basicInfoDTO.getFacebookLink());
        jobSeeker.setLinkedinLink(basicInfoDTO.getLinkedinLink());
        jobSeeker.setGithubLink(basicInfoDTO.getGithubLink());
        jobSeeker.setReadyToRelocate(basicInfoDTO.getReadyToRelocate());

        jobSeekerProfileRepository.save(jobSeeker);

        return ApiResponse.builder().ok(true)
                .status(HttpStatus.OK.value())
                .message("")
                .body(basicInfoDTO)
                .build();
    }


    public ApiResponse<?> getBasicInfo(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        JobSeekerBasicInfoDTO basicInfoDTO = getJobSeekerBasicInfoDTO(jobSeeker);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(basicInfoDTO)
                .message("")
                .build();
    }

    private static JobSeekerBasicInfoDTO getJobSeekerBasicInfoDTO(JobSeeker jobSeeker) {
        return new JobSeekerBasicInfoDTO(jobSeeker.getFirstName(),
                jobSeeker.getLastName(), jobSeeker.getMobileNumberCountryCode(),
                jobSeeker.getMobileNumber(), jobSeeker.getBirthDate(), jobSeeker.getNationality(),
                jobSeeker.getLivesIn(), jobSeeker.getGender(), jobSeeker.getAbout(),
                jobSeeker.getFacebookLink(), jobSeeker.getLinkedinLink(), jobSeeker.getGithubLink(),
                jobSeeker.isReadyToRelocate());
    }

    public ApiResponse<?> saveCareerInterests(String token,
                                              JobSeekerCareerInterestsDTO careerInterestsDTO) {

        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        jobSeeker.setCareerLevel(careerInterestsDTO.getCareerLevel());
        jobSeeker.setJobStatus(careerInterestsDTO.getJobStatus());
        jobSeeker.setJobsTypesUserInterestedIn(careerInterestsDTO.getJobsTypesUserInterestedIn());
        jobSeeker.setJobsUserInterestedIn(careerInterestsDTO.getJobsUserInterestedIn());
        jobSeeker.setMinimumSalaryValue(careerInterestsDTO.getMinimumSalaryValue());
        jobSeeker.setMinimumSalaryCurrency(careerInterestsDTO.getMinimumSalaryCurrency());
        jobSeeker.setShowMinimumSalary(careerInterestsDTO.getShowMinimumSalary());
        jobSeeker.setSearchable(careerInterestsDTO.getSearchable());
        jobSeeker.setOpenToSuggest(careerInterestsDTO.getOpenToSuggest());

        jobSeekerProfileRepository.save(jobSeeker);


        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(careerInterestsDTO)
                .message("")
                .build();
    }

    public ApiResponse<?> getCareerInterests(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }
        JobSeekerCareerInterestsDTO jobSeekerCareerInterestsDTO = getJobSeekerCareerInterestsDTO(jobSeeker);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(jobSeekerCareerInterestsDTO)
                .message("")
                .build();
    }

    private static JobSeekerCareerInterestsDTO getJobSeekerCareerInterestsDTO(JobSeeker jobSeeker) {
        return new JobSeekerCareerInterestsDTO(
                jobSeeker.getCareerLevel(), jobSeeker.getMinimumSalaryValue(),
                jobSeeker.getMinimumSalaryCurrency(), jobSeeker.getShowMinimumSalary(),
                jobSeeker.getJobsTypesUserInterestedIn(), jobSeeker.getJobsUserInterestedIn(),
                jobSeeker.getJobStatus(), jobSeeker.getOpenToSuggest(), jobSeeker.getSearchable());
    }

    public ApiResponse<?> saveProfessionalInfo(String token,
                                               JobSeekerProfessionalInfoDTO professionalInfoDTO) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        jobSeeker.setYearsOfExperience(professionalInfoDTO.getYearsOfExperience());
        jobSeeker.setSkills(professionalInfoDTO.getSkills());

        List<Skill> availableSkills = skillRepository.findAll();
        var skills = professionalInfoDTO.getSkills();
        for (String skill : skills) {
            if (!availableSkills.contains(new Skill(skill))) {
                skillRepository.save(new Skill(skill));
            }
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(professionalInfoDTO)
                .message("")
                .build();
    }


    public ApiResponse<?> getProfessionalInfo(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        JobSeekerProfessionalInfoDTO jobSeekerProfessionalInfoDTO = new JobSeekerProfessionalInfoDTO(
                jobSeeker.getYearsOfExperience(), jobSeeker.getSkills());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(jobSeekerProfessionalInfoDTO)
                .message("")
                .build();
    }

    public ApiResponse<?> setCV(String token, MultipartFile file) throws Exception {
        return frequentlyUsed.addFileToUser(token, file, "js-cv", 5 * 1048,
                "jobSeeker-files/cv/",
                "CV Uploaded Successfully", jobSeekerProfileRepository, cvExtensions, null);
    }

    public ApiResponse<?> getCV(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        String filePathsWithName = jobSeeker.getCV();

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(filePathsWithName)
                .build();
    }

    public ApiResponse<?> deleteCV(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        jobSeeker.setCV(null);
        jobSeekerProfileRepository.save(jobSeeker);

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .message("CV deleted Successfully")
                .build();
    }

    public ApiResponse<?> getJobSeekerAllInfo(String token, UUID uuid) {
        // token could be used later if we want to add blocking feature
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        }

        Optional<JobSeeker> optionalTargetProfile = jobSeekerProfileRepository.findById(uuid);

        if (optionalTargetProfile.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("No user available for this id")
                    .build();
        }

        JobSeeker targetProfile = optionalTargetProfile.get();

        JobSeekerAllInfoDTO jobSeekerAllInfoDTO = jobSeekerSourceDestinationMapper
                .jobSeekerToJobSeekerInfoDTO(targetProfile);

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(jobSeekerAllInfoDTO)
                .message("")
                .build();
    }

    public ApiResponse<?> setJobTitle(String token, String jobTitle) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        jobSeeker.setJobTitle(jobTitle);
        jobSeekerProfileRepository.save(jobSeeker);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Job Title " + jobTitle + " is saved successfully")
                .build();
    }

    public ApiResponse<?> getJobTitle(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(jobSeeker.getJobTitle())
                .build();
    }

    public ApiResponse<?> setJobAlertsStatus(String token, boolean value) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        jobSeeker.setReceiveJobAlerts(value);
        jobSeekerProfileRepository.save(jobSeeker);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Receive Notifications Status changed successfully")
                .build();
    }

    public ApiResponse<?> getJobAlertsStatus(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(jobSeeker.isReceiveJobAlerts())
                .build();
    }

    public ApiResponse<?> setPrivateStatus(String token, boolean value) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        jobSeeker.setPrivateAccount(value);
        jobSeekerProfileRepository.save(jobSeeker);
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .message("Private Account Status changed successfully")
                .build();
    }

    public ApiResponse<?> getPrivateStatus(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(jobSeeker.isPrivateAccount())
                .build();
    }

    public ApiResponse<?> checkNumberIsExist(String prefix, String number) {
        Optional<JobSeeker> jobSeeker = jobSeekerProfileRepository.
                getByMobileNumberCountryCodeAndMobileNumber(prefix, number);
        if (jobSeeker.isEmpty()) {
            return ApiResponse.builder()
                    .ok(true)
                    .status(HttpStatus.OK.value())
                    .body(false)
                    .build();
        }
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(true)
                .build();
    }

    public ApiResponse<?> getNameAndEmail(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        JobSeekerNameAndEmail nameAndEmail = new JobSeekerNameAndEmail(jobSeeker.getFirstName(),
                jobSeeker.getLastName(), jobSeeker.getUsername());
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(nameAndEmail)
                .build();
    }



    }
