package com.hpms.userservice.service.jobseeker;

import com.hpms.commonlib.dto.ApiResponse;
import com.hpms.userservice.model.jobseeker.JobSeeker;
import com.hpms.userservice.model.jobseeker.Language;
import com.hpms.userservice.repository.JobSeekerProfileRepository;
import com.hpms.userservice.repository.jobseeker.LanguageRepository;
import com.hpms.userservice.utils.FrequentlyUsed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;


@Service
public class LanguageService {

    @Autowired
    private LanguageRepository languageRepository;
    @Autowired
    private JobSeekerProfileRepository jobSeekerProfileRepository;
    @Autowired
    private FrequentlyUsed frequentlyUsed;

    public ApiResponse<?> addLanguage(String token, Language language) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        if(language.getLanguageLevel().isEmpty()){
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .ok(false)
                    .body(language)
                    .message("You can't add a language without a level")
                    .build();
        }


        if (jobSeeker.getLanguages() == null) {
            jobSeeker.setLanguages(new HashSet<>());
        }

        Optional<Language> languageWithNameForJobSeeker = languageRepository.findByLanguageNameAndJobSeekerId(
                language.getLanguageName(), jobSeeker.getId());

        if (languageWithNameForJobSeeker.isPresent()) {
            Optional<Language> sameLanguageForJobSeeker = languageRepository
                    .findByLanguageNameAndLanguageLevelAndJobSeekerId(
                            language.getLanguageName(), language.getLanguageLevel(), jobSeeker.getId());

            if (sameLanguageForJobSeeker.isPresent()) {
                return ApiResponse.builder()
                        .status(HttpStatus.OK.value())
                        .ok(true)
                        .body(language)
                        .message("Language Saved Successfully")
                        .build();
            } else {
                languageRepository.deleteLanguageForJobSeeker(
                        jobSeeker.getId(), languageWithNameForJobSeeker.get().getId());


                Optional<Language> languageWithNameAndLevel = languageRepository.findByLanguageNameAndLanguageLevel(
                        language.getLanguageName(), language.getLanguageLevel());

                // This if happens when the language is not even available in table of languages and levels
                // so, we should add a brand now record to it
                if (languageWithNameAndLevel.isEmpty()) {
                    jobSeeker.getLanguages().add(language);
                } else {
                    // Here we just use record in first table mentioned to add it to 'many to many' table
                    jobSeeker.getLanguages().add(languageWithNameAndLevel.get());
                }
            }
            jobSeekerProfileRepository.save(jobSeeker);

            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .ok(true)
                    .body(language)
                    .message("Language Updated Successfully")
                    .build();
        } else {
            Optional<Language> languageWithNameAndLevel = languageRepository.findByLanguageNameAndLanguageLevel(
                    language.getLanguageName(), language.getLanguageLevel());

            // This if happens when the language is not even available in table of languages and levels
            // so, we should add a brand new record to it
            if (languageWithNameAndLevel.isEmpty()) {
                jobSeeker.getLanguages().add(language);
            } else {
                // Here we just use record in first table mentioned to add it to 'many to many' table
                jobSeeker.getLanguages().add(languageWithNameAndLevel.get());
            }
            jobSeekerProfileRepository.save(jobSeeker);
            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .ok(true)
                    .body(language)
                    .message("Language Added Successfully")
                    .build();
        }

    }

    @Transactional
    public ApiResponse<?> deleteLanguage(String token, Language language) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        Optional<Language> languageToDelete = languageRepository.findByLanguageNameAndLanguageLevelAndJobSeekerId(
                language.getLanguageName(), language.getLanguageLevel(), jobSeeker.getId());

        if (languageToDelete.isPresent()) {
            languageRepository.deleteLanguageForJobSeeker(jobSeeker.getId(), languageToDelete.get().getId());
            return ApiResponse.builder()
                    .status(HttpStatus.OK.value())
                    .ok(true)
                    .body(language)
                    .message("Language deleted Successfully")
                    .build();
        } else {
            return ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .ok(false)
                    .body(language)
                    .message("No such Language to delete")
                    .build();
        }
    }

    public ApiResponse<?> getLanguages(String token) {
        ApiResponse<?> isThereUserFromToken = frequentlyUsed.getUserFromTokenIfExist(token);
        JobSeeker jobSeeker = null;

        if (!isThereUserFromToken.isOk()) {
            return isThereUserFromToken;
        } else {
            jobSeeker = (JobSeeker) isThereUserFromToken.getBody();
        }

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .ok(true)
                .body(jobSeeker.getLanguages())
                .build();
    }
}
