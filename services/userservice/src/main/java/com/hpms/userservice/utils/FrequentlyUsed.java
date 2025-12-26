package com.hpms.userservice.utils;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.commonlib.util.FileUploadUtil;
import com.hpms.commonlib.constants.RoleEnum;
import com.hpms.userservice.model.*;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.jobseeker.WorkSample;
import com.hpms.userservice.repository.CompanyRepository;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.RecruiterRepository;
import com.hpms.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class FrequentlyUsed {

    @Autowired
    private JwtTokenUtils tokenUtils;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private RecruiterRepository recruiterRepository;

    public static HashSet<String> imageExtensions =
            new HashSet<>(Arrays.asList(MediaType.IMAGE_JPEG_VALUE,
                    MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_GIF_VALUE,
                    "image/svg+xml", "image/webp", "image/bmp", "image/bmp",
                    "image/tiff", "image/x-icon", "image/vnd.microsoft.icon",
                    "image/vnd.wap.wbmp", "image/x-xbitmap", "image/x-xpixmap",
                    "image/x-xwindowdump", "image/ico", "image/jp2", "image/jp2",
                    "image/jpx", "image/jpm", "image/j2k", "image/jxr", "image/jpm",
                    "image/xbm", "image/ief", "image/pjpeg", "image/x-cmu-raster"));

    public static HashSet<String> cvExtensions =
            new HashSet<>(Arrays.asList(MediaType.APPLICATION_PDF_VALUE));

    public <T extends User> ApiResponse<?> addFileToUser(String token, MultipartFile file, String place,
                                                         int sizeInKiloBytes, String directory,
                                                         String successMessage, JpaRepository repo,
                                                         HashSet<String> allowedExtensions, Object otherData) throws Exception {
        Optional<T> userOptional = Optional.empty();
        if (place.startsWith("js")) {
            JobSeekerProfileRepository nRepo = (JobSeekerProfileRepository) repo;
            userOptional = (Optional<T>) nRepo
                    .getByUsername(tokenUtils.extractUsername(token.substring(7)));
        } else if (place.startsWith("com")) {
            CompanyRepository nRepo = (CompanyRepository) repo;
            userOptional = (Optional<T>) nRepo
                    .getByUsername(tokenUtils.extractUsername(token.substring(7)));
        } else if (place.startsWith("rec")) {
            RecruiterRepository nRepo = (RecruiterRepository) repo;
            userOptional = (Optional<T>) nRepo
                    .getByUsername(tokenUtils.extractUsername(token.substring(7)));
        }
        if (userOptional.isEmpty()) {
            return ApiResponse.builder().ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no user for the entered token")
                    .build();
        }
        T user = userOptional.get();
        String profileFileName = StringUtils.cleanPath(Objects
                .requireNonNull(file.getOriginalFilename()));

        if (file.getContentType() == null || !allowedExtensions.contains(file.getContentType())) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(file.getOriginalFilename() + " has no valid extension")
                    .build();
        }

        if (file.getSize() > sizeInKiloBytes * 1024L) {
            return ApiResponse.builder().ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(file.getOriginalFilename() + " size is above allowed size( "+sizeInKiloBytes/1024+"2MB).")
                    .build();
        }


        String uploadDir = directory + user.getId();

        String pathWithFileName = FileUploadUtil
                .saveFile(uploadDir, profileFileName, file);
        switch (place) {
            case "js-profile" -> {
                JobSeeker nUser = (JobSeeker) user;
                nUser.setProfilePhoto(pathWithFileName);
            }
            case "com-logo" -> {
                Company nUser = (Company) user;
                nUser.setLogo(pathWithFileName);
            }
            case "com-cover" -> {
                Company nUser = (Company) user;
                nUser.setCoverPhoto(pathWithFileName);
            }
            case "js-cv" -> {
                JobSeeker nUser = (JobSeeker) user;
                nUser.setCV(pathWithFileName);
            }
            case "js-workSample" -> {
                JobSeeker jobSeeker = (JobSeeker) user;
                WorkSample data = (WorkSample) otherData;
                if (jobSeeker.getWorkSamples() == null) {
                    jobSeeker.setWorkSamples(new HashSet<>());
                }
                WorkSample workSample = new WorkSample(pathWithFileName, data.getTitle(), data.getDescription(),
                        data.getLink(), jobSeeker);
                jobSeeker.getWorkSamples().add(workSample);
            }
            case "rec-profile" -> {
                Recruiter nUser = (Recruiter) user;
                nUser.setProfilePhoto(pathWithFileName);
            }
            default -> {
                return ApiResponse.builder()
                        .ok(false)
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message(file.getOriginalFilename() + " has no valid place")
                        .build();
            }
        }

        repo.save(user);

        return ApiResponse.builder().ok(true)
                .status(HttpStatus.OK.value())
                .body(pathWithFileName)
                .message(successMessage).build();
    }

    public ApiResponse<?> getUserFromTokenIfExist(String token) {
        Optional<User> optionalUser = userRepository
                .findById(UUID.fromString(tokenUtils.extractId(token.substring(7))));
        if (optionalUser.isEmpty()) {
            return ApiResponse.builder()
                    .ok(false)
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("There is no user for the entered token")
                    .build();
        }
        User user = optionalUser.get();
        return ApiResponse.builder()
                .ok(true)
                .status(HttpStatus.OK.value())
                .body(user)
                .build();
    }

    public String getUserFirstAndLastName(User creator) {
        String creatorName = "";
        if(creator.getRole().equals(RoleEnum.ROLE_COMPANY)){
            Company company = companyRepository.findById(creator.getId()).get();
            creatorName = company.getManagerFirstName() + " " + company.getManagerLastName();
        } else if (creator.getRole().equals(RoleEnum.ROLE_RECRUITER)) {
            Recruiter recruiter = recruiterRepository.findById(creator.getId()).get();
            creatorName = recruiter.getFirstName() + " " + recruiter.getLastName();
        }
        return creatorName;
    }
}
